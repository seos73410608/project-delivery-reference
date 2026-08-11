package com.seos.pmis.wbs.service;

import com.seos.pmis.project.entity.Project;
import com.seos.pmis.project.repository.ProjectRepository;
import com.seos.pmis.wbs.dto.request.WbsCreateRequest;
import com.seos.pmis.wbs.dto.request.WbsSearchRequest;
import com.seos.pmis.wbs.dto.request.WbsUpdateRequest;
import com.seos.pmis.wbs.dto.response.WbsResponse;
import com.seos.pmis.wbs.dto.response.WbsTreeResponse;
import com.seos.pmis.wbs.entity.Wbs;
import com.seos.pmis.wbs.entity.WbsStatus;
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
 * - 부모 WBS 변경
 * - 하위 WBS Level 재계산
 * - 순환 참조 검증
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
        validateWbsCodeDuplicate(
                projectId,
                request.getWbsCode()
        );

        /*
         * 부모 WBS 조회
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

        validateProjectExists(projectId);

        return wbsRepository
                .findByProjectIdOrderBySortOrderAsc(projectId)
                .stream()
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

        validateProjectExists(projectId);

        return wbsRepository
                .findByProjectIdAndParentIsNullOrderBySortOrderAsc(projectId)
                .stream()
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

        findWbs(parentId);

        return wbsRepository
                .findByParentIdOrderBySortOrderAsc(parentId)
                .stream()
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

        validateProjectExists(projectId);

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
        List<WbsTreeResponse> roots =
                new ArrayList<>();

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
     * WBS의 기본 정보뿐만 아니라
     * 부모 WBS 변경까지 처리한다.
     *
     * 부모가 변경되는 경우:
     *
     * 1. 부모 프로젝트 검증
     * 2. 자기 자신 검증
     * 3. 순환 참조 검증
     * 4. 새로운 Level 계산
     * 5. 부모 변경
     * 6. 현재 WBS Level 변경
     * 7. 하위 WBS Level 재계산
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

        Long projectId =
                wbs.getProject().getId();

        /*
         * WBS Code 중복 검증
         *
         * 자기 자신의 기존 Code와 동일한 경우 허용한다.
         */
        if (!wbs.getWbsCode().equals(request.getWbsCode())) {

            validateWbsCodeDuplicate(
                    projectId,
                    request.getWbsCode()
            );
        }

        /*
         * 새로운 부모 조회
         */
        Wbs newParent =
                findParent(request.getParentId());

        /*
         * 부모 프로젝트 검증
         */
        validateParentProject(
                newParent,
                projectId
        );

        /*
         * 자기 자신을 부모로 지정하는 것을 방지
         */
        validateNotSelfParent(
                wbs,
                newParent
        );

        /*
         * 순환 참조 방지
         */
        validateNoCircularReference(
                wbs,
                newParent
        );

        /*
         * 기존 Level
         */
        int oldLevel =
                wbs.getLevel();

        /*
         * 새로운 Level
         */
        int newLevel =
                calculateLevel(newParent);

        /*
         * 부모 변경 여부
         */
        boolean parentChanged =
                !equalsNullable(
                        getId(wbs.getParent()),
                        request.getParentId()
                );

        /*
         * Level 변경 여부
         */
        boolean levelChanged =
                oldLevel != newLevel;

        /*
         * 기본 정보 수정
         */
        wbs.update(
                request.getWbsCode(),
                request.getWbsName(),
                request.getDescription(),
                request.getStatus(),
                request.getSortOrder()
        );

        /*
         * 부모 변경
         */
        if (parentChanged) {

            wbs.changeParent(newParent);
        }

        /*
         * Level 변경
         */
        if (levelChanged) {

            wbs.changeLevel(newLevel);

            /*
             * 하위 WBS의 Level도 함께 재계산한다.
             */
            updateChildrenLevel(
                    wbs
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
            WbsStatus status
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
     * 프로젝트 존재 여부 검증
     *
     * @param projectId 프로젝트 ID
     */
    private void validateProjectExists(
            Long projectId
    ) {

        if (!projectRepository.existsById(projectId)) {

            throw new IllegalArgumentException(
                    "존재하지 않는 프로젝트입니다. projectId="
                            + projectId
            );
        }
    }

    /**
     * WBS Code 중복 검증
     *
     * @param projectId 프로젝트 ID
     * @param wbsCode WBS Code
     */
    private void validateWbsCodeDuplicate(
            Long projectId,
            String wbsCode
    ) {

        if (wbsRepository.existsByProjectIdAndWbsCode(
                projectId,
                wbsCode
        )) {

            throw new IllegalArgumentException(
                    "이미 존재하는 WBS 코드입니다. wbsCode="
                            + wbsCode
            );
        }
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
     * 현재 WBS의 하위 WBS를
     * 새로운 부모로 지정하면 순환 구조가 발생한다.
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
     * 하위 WBS Level 재계산
     *
     * 현재 WBS의 Level이 변경되면
     * 모든 하위 WBS의 Level도 함께 변경한다.
     *
     * 예:
     *
     * 기존:
     *
     * 1       level 1
     * └─ 1.1  level 2
     *    └─ 1.1.1 level 3
     *
     * 부모 이동 후:
     *
     * 2       level 1
     * └─ 2.1  level 2
     *    └─ 2.1.1 level 3
     *
     * @param parent 현재 WBS
     */
    private void updateChildrenLevel(
            Wbs parent
    ) {

        List<Wbs> children =
                wbsRepository.findByParentIdOrderBySortOrderAsc(
                        parent.getId()
                );

        for (Wbs child : children) {

            int childLevel =
                    parent.getLevel() + 1;

            child.changeLevel(childLevel);

            updateChildrenLevel(child);
        }
    }

    /**
     * WBS ID 조회
     *
     * @param wbs WBS
     * @return WBS ID
     */
    private Long getId(Wbs wbs) {

        if (wbs == null) {
            return null;
        }

        return wbs.getId();
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

    /**
     * Pageable 생성
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
}
