package com.seos.pmis.wbs.controller;

import com.seos.pmis.wbs.dto.request.WbsCreateRequest;
import com.seos.pmis.wbs.dto.request.WbsSearchRequest;
import com.seos.pmis.wbs.dto.request.WbsUpdateRequest;
import com.seos.pmis.wbs.dto.response.WbsResponse;
import com.seos.pmis.wbs.dto.response.WbsTreeResponse;
import com.seos.pmis.wbs.entity.WbsStatus;
import com.seos.pmis.wbs.service.WbsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * WBS Controller
 *
 * WBS 생성, 조회, 검색, 수정, 삭제 및
 * 계층 구조(Tree) 조회 API를 제공한다.
 *
 * 주요 API:
 *
 * - WBS 생성
 * - WBS 단건 조회
 * - WBS 검색
 * - 프로젝트별 WBS 조회
 * - 최상위 WBS 조회
 * - 하위 WBS 조회
 * - WBS Tree 조회
 * - WBS 수정
 * - WBS 상태 변경
 * - WBS 정렬 순서 변경
 * - WBS 삭제
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class WbsController {

    private final WbsService wbsService;

    /**
     * WBS 생성
     *
     * POST /api/projects/{projectId}/wbs
     *
     * @param projectId 프로젝트 ID
     * @param request WBS 생성 요청
     * @return 생성된 WBS
     */
    @PostMapping("/projects/{projectId}/wbs")
    public ResponseEntity<WbsResponse> create(
            @PathVariable Long projectId,
            @Valid @RequestBody WbsCreateRequest request
    ) {

        WbsResponse response =
                wbsService.create(
                        projectId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * WBS 단건 조회
     *
     * GET /api/wbs/{id}
     *
     * @param id WBS ID
     * @return WBS
     */
    @GetMapping("/wbs/{id}")
    public ResponseEntity<WbsResponse> findById(
            @PathVariable Long id
    ) {

        WbsResponse response =
                wbsService.findById(id);

        return ResponseEntity.ok(response);
    }

    /**
     * WBS 검색
     *
     * GET /api/wbs
     *
     * 검색 조건은 Request Parameter로 전달한다.
     *
     * 예:
     *
     * /api/wbs?projectId=1
     * /api/wbs?projectId=1&keyword=개발
     * /api/wbs?projectId=1&status=IN_PROGRESS
     * /api/wbs?projectId=1&page=0&size=20
     *
     * @param request WBS 검색 요청
     * @return WBS 페이지
     */
    @GetMapping("/wbs")
    public ResponseEntity<Page<WbsResponse>> search(
            @ModelAttribute WbsSearchRequest request
    ) {

        Page<WbsResponse> response =
                wbsService.search(request);

        return ResponseEntity.ok(response);
    }

    /**
     * 프로젝트별 WBS 조회
     *
     * GET /api/projects/{projectId}/wbs
     *
     * @param projectId 프로젝트 ID
     * @return 프로젝트 WBS 목록
     */
    @GetMapping("/projects/{projectId}/wbs")
    public ResponseEntity<List<WbsResponse>> findByProjectId(
            @PathVariable Long projectId
    ) {

        List<WbsResponse> response =
                wbsService.findByProjectId(projectId);

        return ResponseEntity.ok(response);
    }

    /**
     * 프로젝트의 최상위 WBS 조회
     *
     * GET /api/projects/{projectId}/wbs/root
     *
     * @param projectId 프로젝트 ID
     * @return 최상위 WBS 목록
     */
    @GetMapping("/projects/{projectId}/wbs/root")
    public ResponseEntity<List<WbsResponse>> findRootWbs(
            @PathVariable Long projectId
    ) {

        List<WbsResponse> response =
                wbsService.findRootWbs(projectId);

        return ResponseEntity.ok(response);
    }

    /**
     * 특정 WBS의 하위 WBS 조회
     *
     * GET /api/wbs/{parentId}/children
     *
     * @param parentId 부모 WBS ID
     * @return 하위 WBS 목록
     */
    @GetMapping("/wbs/{parentId}/children")
    public ResponseEntity<List<WbsResponse>> findChildren(
            @PathVariable Long parentId
    ) {

        List<WbsResponse> response =
                wbsService.findChildren(parentId);

        return ResponseEntity.ok(response);
    }

    /**
     * 프로젝트 WBS Tree 조회
     *
     * GET /api/projects/{projectId}/wbs/tree
     *
     * 프로젝트에 속한 전체 WBS를
     * 계층 구조로 반환한다.
     *
     * @param projectId 프로젝트 ID
     * @return WBS Tree
     */
    @GetMapping("/projects/{projectId}/wbs/tree")
    public ResponseEntity<List<WbsTreeResponse>> findTree(
            @PathVariable Long projectId
    ) {

        List<WbsTreeResponse> response =
                wbsService.findTree(projectId);

        return ResponseEntity.ok(response);
    }

    /**
     * WBS 수정
     *
     * PUT /api/wbs/{id}
     *
     * @param id WBS ID
     * @param request WBS 수정 요청
     * @return 수정된 WBS
     */
    @PutMapping("/wbs/{id}")
    public ResponseEntity<WbsResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody WbsUpdateRequest request
    ) {

        WbsResponse response =
                wbsService.update(
                        id,
                        request
                );

        return ResponseEntity.ok(response);
    }

    /**
     * WBS 상태 변경
     *
     * PATCH /api/wbs/{id}/status
     *
     * 예:
     *
     * /api/wbs/1/status?status=IN_PROGRESS
     *
     * @param id WBS ID
     * @param status 변경할 상태
     * @return 변경된 WBS
     */
    @PatchMapping("/wbs/{id}/status")
    public ResponseEntity<WbsResponse> changeStatus(
            @PathVariable Long id,
            @RequestParam WbsStatus status
    ) {

        WbsResponse response =
                wbsService.changeStatus(
                        id,
                        status
                );

        return ResponseEntity.ok(response);
    }

    /**
     * WBS 정렬 순서 변경
     *
     * PATCH /api/wbs/{id}/sort-order
     *
     * 예:
     *
     * /api/wbs/1/sort-order?sortOrder=2
     *
     * @param id WBS ID
     * @param sortOrder 변경할 정렬 순서
     * @return 변경된 WBS
     */
    @PatchMapping("/wbs/{id}/sort-order")
    public ResponseEntity<WbsResponse> changeSortOrder(
            @PathVariable Long id,
            @RequestParam Integer sortOrder
    ) {

        WbsResponse response =
                wbsService.changeSortOrder(
                        id,
                        sortOrder
                );

        return ResponseEntity.ok(response);
    }

    /**
     * WBS 삭제
     *
     * DELETE /api/wbs/{id}
     *
     * 하위 WBS가 존재하는 경우
     * Service에서 삭제를 차단한다.
     *
     * @param id WBS ID
     * @return 응답
     */
    @DeleteMapping("/wbs/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {

        wbsService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
