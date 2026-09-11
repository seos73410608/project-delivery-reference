package com.seos.pmis.issue.controller;

import com.seos.pmis.issue.dto.request.IssueCreateRequest;
import com.seos.pmis.issue.dto.request.IssueSearchRequest;
import com.seos.pmis.issue.dto.request.IssueStatusUpdateRequest;
import com.seos.pmis.issue.dto.request.IssueUpdateRequest;
import com.seos.pmis.issue.dto.response.IssueResponse;
import com.seos.pmis.issue.service.IssueService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Issue Controller
 *
 * Issue Management REST API를 제공한다.
 *
 * 주요 기능:
 *
 * - Issue 단건 조회
 * - Project별 Issue 조회
 * - Issue 검색
 * - Issue 생성
 * - Issue 수정
 * - Issue 상태 변경
 * - Issue 삭제
 *
 * Controller는 HTTP 요청과 응답을 처리하고,
 * 실제 비즈니스 로직은 IssueService에 위임한다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class IssueController {

    private final IssueService issueService;


    /**
     * Issue 단건 조회
     *
     * GET /api/issues/{id}
     *
     * @param id Issue ID
     * @return Issue 상세 정보
     */
    @GetMapping("/issues/{id}")
    public ResponseEntity<IssueResponse> getIssue(
            @PathVariable Long id
    ) {

        IssueResponse response =
                issueService.findById(id);

        return ResponseEntity.ok(response);
    }


    /**
     * Project별 Issue 목록 조회
     *
     * GET /api/projects/{projectId}/issues
     *
     * 기본 정렬:
     *
     * sortOrder ASC
     *
     * @param projectId Project ID
     * @return Issue 목록
     */
    @GetMapping("/projects/{projectId}/issues")
    public ResponseEntity<List<IssueResponse>> getProjectIssues(
            @PathVariable Long projectId
    ) {

        List<IssueResponse> response =
                issueService.findByProjectId(projectId);

        return ResponseEntity.ok(response);
    }


    /**
     * Issue 검색
     *
     * GET /api/issues
     *
     * 지원 검색 조건:
     *
     * - projectId
     * - keyword
     * - status
     * - priority
     * - assigneeId
     * - page
     * - size
     * - sortBy
     * - direction
     *
     * 예:
     *
     * GET /api/issues?
     * projectId=1&
     * status=OPEN&
     * priority=HIGH&
     * page=0&
     * size=20
     *
     * @param request Issue 검색 요청
     * @return Issue Page
     */
    @GetMapping("/issues")
    public ResponseEntity<Page<IssueResponse>> searchIssues(
            @ModelAttribute IssueSearchRequest request
    ) {

        Page<IssueResponse> response =
                issueService.search(request);

        return ResponseEntity.ok(response);
    }


    /**
     * Issue 생성
     *
     * POST /api/projects/{projectId}/issues
     *
     * Issue는 반드시 특정 Project에
     * 소속되어야 한다.
     *
     * @param projectId Project ID
     * @param request Issue 생성 요청
     * @return 생성된 Issue
     */
    @PostMapping("/projects/{projectId}/issues")
    public ResponseEntity<IssueResponse> createIssue(
            @PathVariable Long projectId,

            @RequestBody IssueCreateRequest request
    ) {

        IssueResponse response =
                issueService.create(
                        projectId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    /**
     * Issue 수정
     *
     * PUT /api/issues/{id}
     *
     * Project는 변경하지 않는다.
     *
     * Issue Key와 Reporter는
     * Backend 정책에 따라 관리한다.
     *
     * @param id Issue ID
     * @param request Issue 수정 요청
     * @return 수정된 Issue
     */
    @PutMapping("/issues/{id}")
    public ResponseEntity<IssueResponse> updateIssue(
            @PathVariable Long id,

            @RequestBody IssueUpdateRequest request
    ) {

        IssueResponse response =
                issueService.update(
                        id,
                        request
                );

        return ResponseEntity.ok(response);
    }


    /**
     * Issue 상태 변경
     *
     * PATCH /api/issues/{id}/status
     *
     * 상태 전이 규칙은
     * IssueService에서 검증한다.
     *
     * 예:
     *
     * OPEN
     *   ↓
     * IN_PROGRESS
     *   ↓
     * RESOLVED
     *   ↓
     * CLOSED
     *
     * @param id Issue ID
     * @param request 상태 변경 요청
     * @return 상태가 변경된 Issue
     */
    @PatchMapping("/issues/{id}/status")
    public ResponseEntity<IssueResponse> changeIssueStatus(
            @PathVariable Long id,

            @RequestBody IssueStatusUpdateRequest request
    ) {

        IssueResponse response =
                issueService.changeStatus(
                        id,
                        request
                );

        return ResponseEntity.ok(response);
    }


    /**
     * Issue 삭제
     *
     * DELETE /api/issues/{id}
     *
     * @param id Issue ID
     * @return No Content
     */
    @DeleteMapping("/issues/{id}")
    public ResponseEntity<Void> deleteIssue(
            @PathVariable Long id
    ) {

        issueService.delete(id);

        return ResponseEntity.noContent()
                .build();
    }
}