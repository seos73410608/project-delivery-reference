package com.seos.pmis.wbs.service;

import com.seos.pmis.project.entity.Project;
import com.seos.pmis.project.repository.ProjectRepository;
import com.seos.pmis.wbs.dto.request.WbsCreateRequest;
import com.seos.pmis.wbs.dto.request.WbsSearchRequest;
import com.seos.pmis.wbs.dto.request.WbsUpdateRequest;
import com.seos.pmis.wbs.dto.response.WbsResponse;
import com.seos.pmis.wbs.dto.response.WbsTreeResponse;
import com.seos.pmis.wbs.entity.Wbs;
import com.seos.pmis.wbs.repository.WbsRepository;
import com.seos.pmis.wbs.specification.WbsSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * WBS Service
 *
 * WBS 생성, 조회, 검색, 수정, 삭제 및
 * 계층 구조(Tree) 조회를 담당한다.
 *
 * 주요 책임:
 *
 * - 프로젝트 검증
 * - 상위 WBS 검증
 * - WBS Level 계산
 * - WBS Code 중복 검증
 * - WBS CRUD
 * - 동적 검색
 * - WBS Tree 구성
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WbsService {

    private final WbsRepository wbsRepository;
    private final ProjectRepository projectRepository;

    /**
     * WBS 생성
     *
     * @param projectId 프로젝트 ID
     * @param request WBS 생성 요청
     * @return 생성된 WBS
     */
    @Transactional
    public WbsResponse create(
            Long projectId,
            WbsCreateRequest request
    ) {

        /*
         * 프로젝트 조회
         */
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "존재하지 않는 프로젝트입니다. projectId=" + projectId
                        )
                );

        /*
         * WBS Code 중복 검증
         *
         * WBS Code는 프로젝트 내부에서 유일하다.
         */
        if (wbsRepository.existsByProjectIdAndWbsCode(
                projectId,
                request.getWbsCode()
        )) {
            throw new IllegalArgumentException(
                    "이미 존재하는 WBS 코드입니다. wbsCode="
                            + request.getWbsCode()
            );
        }

        /*
         * 부모 WBS 조회
         */
        Wbs parent = findParent(
                request.getParentId()
        );

        /*
         * 부모 WBS가 존재하는 경우
         *
         * 부모 WBS와 현재 프로젝트가
         * 동일한 프로젝트인지 검증한다.
         */
        validateParentProject(
                parent,
                projectId
        );

        /*
         * WBS Level 계산
         *
         * 최상위 WBS:
         * level = 1
         *
         * 하위 WBS:
         * parent.level + 1
         */
        int level = calculateLevel(parent);

        /*
         * WBS Entity 생성
         */
        Wbs wbs = Wbs.builder()
                .project(project)
                .parent(parent)
                .wbsCode(request.getWbsCode())
                .wbsName(request.getWbsName())
                .level(level)
                .sortOrder(request.getSortOrder())
                .status(request.getStatus())
                .description(request.getDescription())
                .build();

        /*
         * 저장
         */
        Wbs savedWbs = wbsRepository.save(wbs);

        return WbsResponse.from(savedWbs);
    }

    /**
     * WBS 단건 조회
     *
     * @param id WBS ID
     * @return WBS 응답
     */
    public WbsResponse findById(Long id) {

        Wbs wbs = findWbs(id);

        return WbsResponse.from(wbs);
    }

    /**
     * WBS 검색
     *
     * WbsSearchRequest의 검색 조건을
     * Specification으로 변환하여 조회한다.
     *
     * 정렬과 페이징은 Pageable에서 처리한다.
     *
     * @param request WBS 검색 요청
     * @return WBS 페이지
     */
    public Page<WbsResponse> search(
            WbsSearchRequest request
    ) {

        Pageable pageable = createPageable(request);

        Page<Wbs> page = wbsRepository.findAll(
                WbsSpecification.search(request),
                pageable
        );

        return page.map(WbsResponse::from);
    }

    /**
     * 프로젝트별 WBS 조회
     *
     * @param projectId 프로젝트 ID
     * @return WBS 목록
     */
    public List<WbsResponse> findByProjectId(
            Long projectId
    ) {

        List<Wbs> wbsList =
                wbsRepository.findByProjectIdOrderBySortOrderAsc(
                        projectId
                );

        return wbsList.stream()
                .map(WbsResponse::from)
                .toList();
    }

    /**
     * 프로젝트의 최상위 WBS 조회
     *
     * @param projectId 프로젝트 ID
     * @return 최상위 WBS 목록
     */
    public List<WbsResponse> findRootWbs(
            Long projectId
    ) {

        List<Wbs> wbsList =
                wbsRepository
                        .findByProjectIdAndParentIsNullOrderBySortOrderAsc(
                                projectId
                        );

        return wbsList.stream()
                .map(WbsResponse::from)
                .toList();
    }

    /**
     * 특정 부모 WBS의 하위 WBS 조회
     *
     * @param parentId 부모 WBS ID
     * @return 하위 WBS 목록
     */
    public List<WbsResponse> findChildren(
            Long parentId
    ) {

        List<Wbs> wbsList =
                wbsRepository.findByParentIdOrderBySortOrderAsc(
                        parentId
                );

        return wbsList.stream()
                .map(WbsResponse::from)
                .toList();
    }

    /**
     * WBS Tree 조회
     *
     * 프로젝트에 속한 모든 WBS를 조회한 후
     * parent-child 관계를 이용하여 Tree 구조를 구성한다.
     *
     * @param projectId 프로젝트 ID
     * @return WBS Tree 목록
     */
    public List<WbsTreeResponse> findTree(
            Long projectId
    ) {

        List<Wbs> wbsList =
                wbsRepository.findByProjectIdOrderBySortOrderAsc(
                        projectId
                );

        /*
         * Entity → Tree Response 변환
         */
        Map<Long, WbsTreeResponse> responseMap =
                new LinkedHashMap<>();

        for (Wbs wbs : wbsList) {

            responseMap.put(
                    wbs.getId(),
                    WbsTreeResponse.from(wbs)
            );
        }

        /*
         * Root Tree
         */
        List<WbsTreeResponse> roots = new ArrayList<>();

        /*
         * Parent → Child 연결
         */
        for (Wbs wbs : wbsList) {

            WbsTreeResponse current =
                    responseMap.get(wbs.getId());

            /*
             * 최상위 WBS
             */
            if (wbs.getParent() == null) {

                roots.add(current);

                continue;
            }

            /*
             * 부모 WBS
             */
            WbsTreeResponse parent =
                    responseMap.get(
                            wbs.getParent().getId()
                    );

            /*
             * 부모가 존재하는 경우
             */
            if (parent != null) {

                parent.addChild(current);
            }
        }

        return roots;
    }

    /**
     * WBS 수정
     *
     * @param id WBS ID
     * @param request WBS 수정 요청
     * @return 수정된 WBS
     */
    @Transactional
    public WbsResponse update(
            Long id,
            WbsUpdateRequest request
    ) {

        Wbs wbs = findWbs(id);

        /*
         * 현재 WBS의 프로젝트
         */
        Long projectId =
                wbs.getProject().getId();

        /*
         * WBS Code 중복 검증
         *
         * 자기 자신은 제외한다.
         */
        if (!wbs.getWbsCode().equals(request.getWbsCode())
                && wbsRepository.existsByProjectIdAndWbsCode(
                        projectId,
                        request.getWbsCode()
                )) {

            throw new IllegalArgumentException(
                    "이미 존재하는 WBS 코드입니다. wbsCode="
                            + request.getWbsCode()
            );
        }

        /*
         * 새로운 부모 WBS 조회
         */
        Wbs parent = findParent(
                request.getParentId()
        );

        /*
         * 부모 프로젝트 검증
         */
        validateParentProject(
                parent,
                projectId
        );

        /*
         * 자기 자신을 부모로 지정하는 것을 방지
         */
        validateNotSelfParent(
                wbs,
                parent
        );

        /*
         * 순환 참조 방지
         */
        validateNoCircularReference(
                wbs,
                parent
        );

        /*
         * 부모 변경에 따라 Level 재계산
         */
        int level = calculateLevel(parent);

        /*
         * Entity 수정
         *
         * 현재 Entity의 update() 메서드는
         * parent와 level을 받지 않으므로
         * 여기서는 현재 Entity 구조에 맞게
         * 수정 메서드를 호출한다.
         */
        wbs.update(
                request.getWbsName(),
                request.getDescription(),
                request.getStatus(),
                request.getSortOrder()
        );

        /*
         * 현재 Entity에는 parent / level 변경 메서드가
         * 없기 때문에 부모 변경이나 level 변경은
         * 아직 Entity API에서 지원하지 않는다.
         *
         * 따라서 현재 단계에서는
         * 부모 변경 요청이 기존 부모와 다른 경우
         * 명시적으로 예외를 발생시킨다.
         */
        Long currentParentId =
                wbs.getParent() != null
                        ? wbs.getParent().getId()
                        : null;

        if (!equalsNullable(
                currentParentId,
                request.getParentId()
        )) {

            throw new IllegalStateException(
                    "현재 Wbs Entity는 부모 WBS 변경을 지원하지 않습니다."
            );
        }

        /*
         * Level 역시 현재 부모 기준으로 유지된다.
         */
        if (wbs.getLevel() != level) {

            throw new IllegalStateException(
                    "현재 Wbs Entity는 WBS Level 변경을 지원하지 않습니다."
            );
        }

        return WbsResponse.from(wbs);
    }

    /**
     * WBS 삭제
     *
     * 하위 WBS가 존재하는 경우 삭제하지 않는다.
     *
     * @param id WBS ID
     */
    @Transactional
    public void delete(Long id) {

        Wbs wbs = findWbs(id);

        /*
         * 하위 WBS 존재 여부 확인
         */
        if (wbsRepository.existsByParentId(id)) {

            throw new IllegalStateException(
                    "하위 WBS가 존재하여 삭제할 수 없습니다."
            );
        }

        wbsRepository.delete(wbs);
    }

    /**
     * WBS 상태 변경
     *
     * @param id WBS ID
     * @param status 변경할 상태
     * @return 변경된 WBS
     */
    @Transactional
    public WbsResponse changeStatus(
            Long id,
            com.seos.pmis.wbs.entity.WbsStatus status
    ) {

        Wbs wbs = findWbs(id);

        wbs.changeStatus(status);

        return WbsResponse.from(wbs);
    }

    /**
     * WBS 정렬 순서 변경
     *
     * @param id WBS ID
     * @param sortOrder 변경할 정렬 순서
     * @return 변경된 WBS
     */
    @Transactional
    public WbsResponse changeSortOrder(
            Long id,
            Integer sortOrder
    ) {

        Wbs wbs = findWbs(id);

        wbs.changeSortOrder(sortOrder);

        return WbsResponse.from(wbs);
    }

    /**
     * WBS 조회
     *
     * @param id WBS ID
     * @return WBS Entity
     */
    private Wbs findWbs(Long id) {

        return wbsRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "존재하지 않는 WBS입니다. wbsId=" + id
                        )
                );
    }

    /**
     * 부모 WBS 조회
     *
     * parentId가 null이면 최상위 WBS이므로
     * null을 반환한다.
     *
     * @param parentId 부모 WBS ID
     * @return 부모 WBS
     */
    private Wbs findParent(Long parentId) {

        if (parentId == null) {
            return null;
        }

        return wbsRepository.findById(parentId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "존재하지 않는 부모 WBS입니다. parentId="
                                        + parentId
                        )
                );
    }

    /**
     * WBS Level 계산
     *
     * 최상위 WBS는 1부터 시작한다.
     *
     * @param parent 부모 WBS
     * @return 계산된 Level
     */
    private int calculateLevel(Wbs parent) {

        if (parent == null) {
            return 1;
        }

        return parent.getLevel() + 1;
    }

    /**
     * 부모 WBS의 프로젝트 검증
     *
     * 서로 다른 프로젝트의 WBS를
     * 부모-자식 관계로 연결할 수 없도록 한다.
     *
     * @param parent 부모 WBS
     * @param projectId 프로젝트 ID
     */
    private void validateParentProject(
            Wbs parent,
            Long projectId
    ) {

        if (parent == null) {
            return;
        }

        Long parentProjectId =
                parent.getProject().getId();

        if (!parentProjectId.equals(projectId)) {

            throw new IllegalArgumentException(
                    "부모 WBS가 다른 프로젝트에 속해 있습니다."
            );
        }
    }

    /**
     * 자기 자신을 부모로 지정하는 것을 방지한다.
     *
     * @param wbs 현재 WBS
     * @param parent 새로운 부모 WBS
     */
    private void validateNotSelfParent(
            Wbs wbs,
            Wbs parent
    ) {

        if (parent == null) {
            return;
        }

        if (wbs.getId().equals(parent.getId())) {

            throw new IllegalArgumentException(
                    "자기 자신을 부모 WBS로 지정할 수 없습니다."
            );
        }
    }

    /**
     * WBS 순환 참조 방지
     *
     * 현재 WBS의 하위 WBS를 새로운 부모로 지정하면
     * Tree 구조에 순환이 발생한다.
     *
     * @param wbs 현재 WBS
     * @param parent 새로운 부모 WBS
     */
    private void validateNoCircularReference(
            Wbs wbs,
            Wbs parent
    ) {

        if (parent == null) {
            return;
        }

        Wbs current = parent;

        while (current != null) {

            if (current.getId().equals(wbs.getId())) {

                throw new IllegalArgumentException(
                        "WBS 계층 구조에 순환 참조가 발생합니다."
                );
            }

            current = current.getParent();
        }
    }

    /**
     * Pageable 생성
     *
     * WbsSearchRequest의 정렬 및 페이징 조건을
     * Spring Data Pageable로 변환한다.
     *
     * @param request WBS 검색 요청
     * @return Pageable
     */
    private Pageable createPageable(
            WbsSearchRequest request
    ) {

        if (request == null) {

            return PageRequest.of(
                    0,
                    20,
                    Sort.by(
                            Sort.Direction.ASC,
                            "sortOrder"
                    )
            );
        }

        Sort.Direction direction =
                parseDirection(
                        request.getDirection()
                );

        String sortBy =
                normalizeSortBy(
                        request.getSortBy()
                );

        return PageRequest.of(
                request.getPage(),
                request.getSize(),
                Sort.by(direction, sortBy)
        );
    }

    /**
     * 정렬 방향 변환
     *
     * @param direction 정렬 방향
     * @return Sort.Direction
     */
    private Sort.Direction parseDirection(
            String direction
    ) {

        if ("DESC".equalsIgnoreCase(direction)) {
            return Sort.Direction.DESC;
        }

        return Sort.Direction.ASC;
    }

    /**
     * 정렬 필드 검증
     *
     * 허용되지 않은 필드는
     * 기본값 sortOrder를 사용한다.
     *
     * @param sortBy 정렬 필드
     * @return Entity 필드명
     */
    private String normalizeSortBy(
            String sortBy
    ) {

        if (sortBy == null || sortBy.isBlank()) {
            return "sortOrder";
        }

        return switch (sortBy) {

            case "wbsCode" ->
                    "wbsCode";

            case "wbsName" ->
                    "wbsName";

            case "sortOrder" ->
                    "sortOrder";

            case "createdAt" ->
                    "createdAt";

            case "level" ->
                    "level";

            default ->
                    "sortOrder";
        };
    }

    /**
     * Nullable Long 비교
     *
     * @param first 첫 번째 값
     * @param second 두 번째 값
     * @return 동일 여부
     */
    private boolean equalsNullable(
            Long first,
            Long second
    ) {

        if (first == null) {
            return second == null;
        }

        return first.equals(second);
    }
}