
package com.seos.pmis.wbs.service;

import com.seos.common.search.SearchPageableFactory;
import com.seos.pmis.project.entity.Project;
import com.seos.pmis.project.repository.ProjectRepository;
import com.seos.pmis.task.dto.response.TaskProgressAggregation;
import com.seos.pmis.task.entity.Task;
import com.seos.pmis.task.repository.TaskRepository;
import com.seos.pmis.task.service.TaskProgressAggregationService;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
 * - Task 기반 WBS 진척률 집계 결과 제공
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WbsService {

        private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
                        "id",
                        "wbsCode",
                        "wbsName",
                        "level",
                        "sortOrder",
                        "status",
                        "createdAt",
                        "updatedAt");

        private final WbsRepository wbsRepository;
        private final ProjectRepository projectRepository;
        private final TaskRepository taskRepository;
        private final TaskProgressAggregationService taskProgressAggregationService;

        /**
         * WBS 생성
         *
         * @param projectId 프로젝트 ID
         * @param request   WBS 생성 요청
         * @return 생성된 WBS
         */
        @Transactional
        public WbsResponse create(
                        Long projectId,
                        WbsCreateRequest request) {

                /*
                 * 프로젝트 조회
                 */
                Project project = projectRepository.findById(projectId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "존재하지 않는 프로젝트입니다. projectId=" + projectId));

                /*
                 * WBS Code 중복 검증
                 */
                validateWbsCodeDuplicate(
                                projectId,
                                request.getWbsCode());

                /*
                 * 부모 WBS 조회
                 */
                Wbs parent = findParent(
                                request.getParentId());

                /*
                 * 부모 프로젝트 검증
                 */
                validateParentProject(
                                parent,
                                projectId);

                /*
                 * WBS Level 계산
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

                /*
                 * 프로젝트 전체 기준으로 WBS 집계
                 */
                Map<Long, TaskProgressAggregation> aggregationMap = loadWbsAggregations(projectId);

                return WbsResponse.from(
                                savedWbs,
                                aggregationMap.get(savedWbs.getId()));
        }

        /**
         * WBS 단건 조회
         *
         * @param id WBS ID
         * @return WBS 응답
         */
        public WbsResponse findById(Long id) {

                Wbs wbs = findWbs(id);

                Long projectId = wbs.getProject().getId();

                Map<Long, TaskProgressAggregation> aggregationMap = loadWbsAggregations(projectId);

                return WbsResponse.from(
                                wbs,
                                aggregationMap.get(wbs.getId()));
        }

        /**
         * WBS 검색
         *
         * 검색 페이지에 포함된 프로젝트를 기준으로
         * 프로젝트별 WBS 및 Task를 일괄 조회하여 집계한다.
         *
         * @param request WBS 검색 요청
         * @return WBS 페이지
         */
        public Page<WbsResponse> search(
                        WbsSearchRequest request) {

                validateSearchRequest(request);

                Pageable pageable = createPageable(request);

                Page<Wbs> page = wbsRepository.findAll(
                                WbsSpecification.search(request),
                                pageable);

                if (page.isEmpty()) {
                        return page.map(WbsResponse::from);
                }

                /*
                 * 검색 결과에 포함된 프로젝트 ID 수집
                 */
                Set<Long> projectIds = new LinkedHashSet<>();

                for (Wbs wbs : page.getContent()) {

                        projectIds.add(
                                        wbs.getProject().getId());
                }

                /*
                 * 여러 프로젝트의 WBS 집계 결과 일괄 조회
                 */
                Map<Long, TaskProgressAggregation> aggregationMap = loadWbsAggregations(projectIds);

                return page.map(wbs -> WbsResponse.from(
                                wbs,
                                aggregationMap.get(wbs.getId())));
        }

        /**
         * 프로젝트별 WBS 조회
         *
         * @param projectId 프로젝트 ID
         * @return WBS 목록
         */
        public List<WbsResponse> findByProjectId(
                        Long projectId) {

                validateProjectExists(projectId);

                List<Wbs> wbsList = wbsRepository.findByProjectIdOrderBySortOrderAsc(
                                projectId);

                Map<Long, TaskProgressAggregation> aggregationMap = loadWbsAggregations(projectId, wbsList);

                return wbsList.stream()
                                .map(wbs -> WbsResponse.from(
                                                wbs,
                                                aggregationMap.get(wbs.getId())))
                                .toList();
        }

        /**
         * 프로젝트의 최상위 WBS 조회
         *
         * 하위 WBS에 연결된 Task까지 포함하여
         * 최상위 WBS의 진척률을 집계한다.
         *
         * @param projectId 프로젝트 ID
         * @return 최상위 WBS 목록
         */
        public List<WbsResponse> findRootWbs(
                        Long projectId) {

                validateProjectExists(projectId);

                List<Wbs> rootWbsList = wbsRepository.findByProjectIdAndParentIsNullOrderBySortOrderAsc(
                                projectId);

                Map<Long, TaskProgressAggregation> aggregationMap = loadWbsAggregations(projectId);

                return rootWbsList.stream()
                                .map(wbs -> WbsResponse.from(
                                                wbs,
                                                aggregationMap.get(wbs.getId())))
                                .toList();
        }

        /**
         * 특정 부모 WBS의 하위 WBS 조회
         *
         * 조회된 하위 WBS의 모든 후손에 연결된 Task까지
         * 진척률 집계에 포함한다.
         *
         * @param parentId 부모 WBS ID
         * @return 하위 WBS 목록
         */
        public List<WbsResponse> findChildren(
                        Long parentId) {

                Wbs parent = findWbs(parentId);

                Long projectId = parent.getProject().getId();

                List<Wbs> children = wbsRepository.findByParentIdOrderBySortOrderAsc(
                                parentId);

                Map<Long, TaskProgressAggregation> aggregationMap = loadWbsAggregations(projectId);

                return children.stream()
                                .map(wbs -> WbsResponse.from(
                                                wbs,
                                                aggregationMap.get(wbs.getId())))
                                .toList();
        }

        /**
         * WBS Tree 조회
         *
         * 프로젝트에 속한 모든 WBS와 Task를 조회한 후
         * 진척률 집계 및 parent-child 관계를 구성한다.
         *
         * WBS 목록은 집계와 Tree 구성에 재사용한다.
         *
         * @param projectId 프로젝트 ID
         * @return WBS Tree 목록
         */
        public List<WbsTreeResponse> findTree(
                        Long projectId) {

                validateProjectExists(projectId);

                List<Wbs> wbsList = wbsRepository.findByProjectIdOrderBySortOrderAsc(
                                projectId);

                /*
                 * 이미 조회한 WBS 목록을 재사용하여 집계
                 */
                Map<Long, TaskProgressAggregation> aggregationMap = loadWbsAggregations(projectId, wbsList);

                /*
                 * Entity → Tree Response 변환
                 */
                Map<Long, WbsTreeResponse> responseMap = new LinkedHashMap<>();

                for (Wbs wbs : wbsList) {

                        responseMap.put(
                                        wbs.getId(),
                                        WbsTreeResponse.from(
                                                        wbs,
                                                        aggregationMap.get(wbs.getId())));
                }

                /*
                 * Root Tree
                 */
                List<WbsTreeResponse> roots = new ArrayList<>();

                /*
                 * Parent → Child 연결
                 */
                for (Wbs wbs : wbsList) {

                        WbsTreeResponse current = responseMap.get(wbs.getId());

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
                        WbsTreeResponse parent = responseMap.get(
                                        wbs.getParent().getId());

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
         * @param id      WBS ID
         * @param request WBS 수정 요청
         * @return 수정된 WBS
         */
        @Transactional
        public WbsResponse update(
                        Long id,
                        WbsUpdateRequest request) {

                Wbs wbs = findWbs(id);

                Long projectId = wbs.getProject().getId();

                /*
                 * WBS Code 중복 검증
                 */
                if (!wbs.getWbsCode().equals(request.getWbsCode())) {

                        validateWbsCodeDuplicate(
                                        projectId,
                                        request.getWbsCode());
                }

                /*
                 * 새로운 부모 조회
                 */
                Wbs newParent = findParent(request.getParentId());

                /*
                 * 부모 프로젝트 검증
                 */
                validateParentProject(
                                newParent,
                                projectId);

                /*
                 * 자기 자신을 부모로 지정하는 것을 방지
                 */
                validateNotSelfParent(
                                wbs,
                                newParent);

                /*
                 * 순환 참조 방지
                 */
                validateNoCircularReference(
                                wbs,
                                newParent);

                /*
                 * 기존 Level
                 */
                int oldLevel = wbs.getLevel();

                /*
                 * 새로운 Level
                 */
                int newLevel = calculateLevel(newParent);

                /*
                 * 부모 변경 여부
                 */
                boolean parentChanged = !equalsNullable(
                                getId(wbs.getParent()),
                                request.getParentId());

                /*
                 * Level 변경 여부
                 */
                boolean levelChanged = oldLevel != newLevel;

                /*
                 * 기본 정보 수정
                 */
                wbs.update(
                                request.getWbsCode(),
                                request.getWbsName(),
                                request.getDescription(),
                                request.getStatus(),
                                request.getSortOrder());

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
                        updateChildrenLevel(wbs);
                }

                /*
                 * 변경된 계층 구조를 기준으로 집계
                 */
                Map<Long, TaskProgressAggregation> aggregationMap = loadWbsAggregations(projectId);

                return WbsResponse.from(
                                wbs,
                                aggregationMap.get(wbs.getId()));
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
                                        "하위 WBS가 존재하여 삭제할 수 없습니다.");
                }

                /*
                 * 연결된 Task 존재 여부 확인
                 */
                if (taskRepository.existsByWbs_Id(id)) {

                        throw new IllegalStateException(
                                        "Task가 연결되어 있어 WBS를 삭제할 수 없습니다.");
                }

                wbsRepository.delete(wbs);
        }

        /**
         * WBS 상태 변경
         *
         * @param id     WBS ID
         * @param status 변경할 상태
         * @return 변경된 WBS
         */
        @Transactional
        public WbsResponse changeStatus(
                        Long id,
                        WbsStatus status) {

                Wbs wbs = findWbs(id);

                wbs.changeStatus(status);

                Map<Long, TaskProgressAggregation> aggregationMap = loadWbsAggregations(
                                wbs.getProject().getId());

                return WbsResponse.from(
                                wbs,
                                aggregationMap.get(wbs.getId()));
        }

        /**
         * WBS 정렬 순서 변경
         *
         * @param id        WBS ID
         * @param sortOrder 변경할 정렬 순서
         * @return 변경된 WBS
         */
        @Transactional
        public WbsResponse changeSortOrder(
                        Long id,
                        Integer sortOrder) {

                Wbs wbs = findWbs(id);

                wbs.changeSortOrder(sortOrder);

                Map<Long, TaskProgressAggregation> aggregationMap = loadWbsAggregations(
                                wbs.getProject().getId());

                return WbsResponse.from(
                                wbs,
                                aggregationMap.get(wbs.getId()));
        }

        /**
         * 프로젝트의 WBS별 진척률 집계
         *
         * WBS 목록과 Task 목록을 각각 일괄 조회한다.
         *
         * @param projectId 프로젝트 ID
         * @return WBS ID별 집계 결과
         */
        private Map<Long, TaskProgressAggregation> loadWbsAggregations(
                        Long projectId) {

                List<Wbs> wbsList = wbsRepository.findByProjectIdOrderBySortOrderAsc(
                                projectId);

                return loadWbsAggregations(projectId, wbsList);
        }

        /**
         * 전달받은 WBS 목록을 재사용하여 진척률을 집계한다.
         *
         * @param projectId 프로젝트 ID
         * @param wbsList   프로젝트의 전체 WBS 목록
         * @return WBS ID별 집계 결과
         */
        private Map<Long, TaskProgressAggregation> loadWbsAggregations(
                        Long projectId,
                        List<Wbs> wbsList) {

                List<Task> tasks = taskRepository.findByProject_IdOrderBySortOrderAsc(
                                projectId);

                return taskProgressAggregationService.aggregateByWbs(
                                wbsList,
                                tasks);
        }

        /**
         * 여러 프로젝트의 WBS별 진척률 집계
         *
         * 검색 결과에 포함된 프로젝트들의 WBS와 Task를
         * 각각 한 번씩 일괄 조회한 후 프로젝트별로 집계한다.
         *
         * @param projectIds 프로젝트 ID 목록
         * @return WBS ID별 집계 결과
         */
        private Map<Long, TaskProgressAggregation> loadWbsAggregations(
                        Set<Long> projectIds) {

                if (projectIds == null || projectIds.isEmpty()) {
                        return Map.of();
                }

                List<Wbs> wbsList = wbsRepository.findByProject_IdInOrderBySortOrderAsc(
                                projectIds);

                List<Task> tasks = taskRepository.findByProject_IdInOrderBySortOrderAsc(
                                projectIds);

                /*
                 * 프로젝트별 WBS 그룹
                 */
                Map<Long, List<Wbs>> wbsByProject = new HashMap<>();

                for (Wbs wbs : wbsList) {

                        Long projectId = wbs.getProject().getId();

                        wbsByProject.computeIfAbsent(
                                        projectId,
                                        key -> new ArrayList<>()).add(wbs);
                }

                /*
                 * 프로젝트별 Task 그룹
                 */
                Map<Long, List<Task>> tasksByProject = new HashMap<>();

                for (Task task : tasks) {

                        Long projectId = task.getProject().getId();

                        tasksByProject.computeIfAbsent(
                                        projectId,
                                        key -> new ArrayList<>()).add(task);
                }

                /*
                 * 프로젝트별 집계 결과 통합
                 */
                Map<Long, TaskProgressAggregation> result = new LinkedHashMap<>();

                for (Long projectId : projectIds) {

                        List<Wbs> projectWbs = wbsByProject.getOrDefault(
                                        projectId,
                                        List.of());

                        List<Task> projectTasks = tasksByProject.getOrDefault(
                                        projectId,
                                        List.of());

                        Map<Long, TaskProgressAggregation> projectAggregation = taskProgressAggregationService
                                        .aggregateByWbs(
                                                        projectWbs,
                                                        projectTasks);

                        result.putAll(projectAggregation);
                }

                return result;
        }

        /**
         * WBS 조회
         *
         * @param id WBS ID
         * @return WBS Entity
         */
        private Wbs findWbs(Long id) {

                return wbsRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "존재하지 않는 WBS입니다. wbsId=" + id));
        }

        /**
         * 프로젝트 존재 여부 검증
         *
         * @param projectId 프로젝트 ID
         */
        private void validateProjectExists(
                        Long projectId) {

                if (!projectRepository.existsById(projectId)) {

                        throw new IllegalArgumentException(
                                        "존재하지 않는 프로젝트입니다. projectId="
                                                        + projectId);
                }
        }

        /**
         * WBS Code 중복 검증
         *
         * @param projectId 프로젝트 ID
         * @param wbsCode   WBS Code
         */
        private void validateWbsCodeDuplicate(
                        Long projectId,
                        String wbsCode) {

                if (wbsRepository.existsByProjectIdAndWbsCode(
                                projectId,
                                wbsCode)) {

                        throw new IllegalArgumentException(
                                        "이미 존재하는 WBS 코드입니다. wbsCode="
                                                        + wbsCode);
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
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "존재하지 않는 부모 WBS입니다. parentId="
                                                                + parentId));
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
         * @param parent    부모 WBS
         * @param projectId 프로젝트 ID
         */
        private void validateParentProject(
                        Wbs parent,
                        Long projectId) {

                if (parent == null) {
                        return;
                }

                Long parentProjectId = parent.getProject().getId();

                if (!parentProjectId.equals(projectId)) {

                        throw new IllegalArgumentException(
                                        "부모 WBS가 다른 프로젝트에 속해 있습니다.");
                }
        }

        /**
         * 자기 자신을 부모로 지정하는 것을 방지한다.
         *
         * @param wbs    현재 WBS
         * @param parent 새로운 부모 WBS
         */
        private void validateNotSelfParent(
                        Wbs wbs,
                        Wbs parent) {

                if (parent == null) {
                        return;
                }

                if (wbs.getId().equals(parent.getId())) {

                        throw new IllegalArgumentException(
                                        "자기 자신을 부모 WBS로 지정할 수 없습니다.");
                }
        }

        /**
         * WBS 순환 참조 방지
         *
         * @param wbs    현재 WBS
         * @param parent 새로운 부모 WBS
         */
        private void validateNoCircularReference(
                        Wbs wbs,
                        Wbs parent) {

                if (parent == null) {
                        return;
                }

                Wbs current = parent;

                while (current != null) {

                        if (current.getId().equals(wbs.getId())) {

                                throw new IllegalArgumentException(
                                                "WBS 계층 구조에 순환 참조가 발생합니다.");
                        }

                        current = current.getParent();
                }
        }

        /**
         * 하위 WBS Level 재계산
         *
         * @param parent 현재 WBS
         */
        private void updateChildrenLevel(
                        Wbs parent) {

                List<Wbs> children = wbsRepository.findByParentIdOrderBySortOrderAsc(
                                parent.getId());

                for (Wbs child : children) {

                        int childLevel = parent.getLevel() + 1;

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
         * @param first  첫 번째 값
         * @param second 두 번째 값
         * @return 동일 여부
         */
        private boolean equalsNullable(
                        Long first,
                        Long second) {

                if (first == null) {
                        return second == null;
                }

                return first.equals(second);
        }

        /**
         * 검색 요청 검증
         *
         * @param request WBS 검색 요청
         */
        private void validateSearchRequest(
                        WbsSearchRequest request) {

                if (request == null) {

                        throw new IllegalArgumentException(
                                        "WBS search request is required.");
                }
        }

        /**
         * Pageable 생성
         *
         * @param request WBS 검색 요청
         * @return Pageable
         */
        private Pageable createPageable(
                        WbsSearchRequest request) {

                return SearchPageableFactory.create(
                                request.getPage(),
                                request.getSize(),
                                request.getSortBy(),
                                request.getDirection(),
                                "sortOrder",
                                Sort.Direction.ASC,
                                ALLOWED_SORT_FIELDS);
        }
}
