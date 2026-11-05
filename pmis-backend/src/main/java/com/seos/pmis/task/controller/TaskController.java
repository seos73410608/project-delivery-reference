
package com.seos.pmis.task.controller;

import com.seos.pmis.task.dto.request.TaskCreateRequest;
import com.seos.pmis.task.dto.request.TaskSearchRequest;
import com.seos.pmis.task.dto.request.TaskStatusChangeRequest;
import com.seos.pmis.task.dto.request.TaskUpdateRequest;
import com.seos.pmis.task.dto.response.TaskResponse;
import com.seos.pmis.task.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Task Controller
 *
 * Task Domain의 REST API를 담당한다.
 *
 * 주요 책임:
 * - Task 단건 조회
 * - Project별 Task 조회
 * - WBS별 Task 조회
 * - Schedule별 Task 조회
 * - 담당자별 Task 조회
 * - Task 검색
 * - 기간 조건 Task 검색
 * - Task 생성
 * - Task 수정
 * - Task 상태 변경
 * - Task 삭제
 *
 * API의 비즈니스 로직은 TaskService에 위임한다.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(
        name = "Task",
        description = "Task Domain API"
)
public class TaskController {

    private final TaskService taskService;


    /**
     * Task 단건 조회
     *
     * GET /api/tasks/{id}
     *
     * @param id Task ID
     * @return Task Response
     */
    @GetMapping("/tasks/{id}")
    @Operation(
            summary = "Task 단건 조회",
            description = "Task ID를 기준으로 Task를 조회한다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Task 조회 성공",
                    content = @Content(
                            schema = @Schema(
                                    implementation = TaskResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Task를 찾을 수 없음"
            )
    })
    public ResponseEntity<TaskResponse> findById(
            @Parameter(
                    description = "Task ID",
                    required = true,
                    in = ParameterIn.PATH
            )
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                taskService.findById(id)
        );
    }


    /**
     * Project별 Task 조회
     *
     * GET /api/projects/{projectId}/tasks
     *
     * @param projectId Project ID
     * @return Task 목록
     */
    @GetMapping("/projects/{projectId}/tasks")
    @Operation(
            summary = "Project별 Task 조회",
            description = "특정 Project에 속한 Task를 sortOrder 오름차순으로 조회한다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Task 목록 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project를 찾을 수 없음"
            )
    })
    public ResponseEntity<List<TaskResponse>> findByProjectId(
            @Parameter(
                    description = "Project ID",
                    required = true,
                    in = ParameterIn.PATH
            )
            @PathVariable Long projectId
    ) {

        return ResponseEntity.ok(
                taskService.findByProjectId(projectId)
        );
    }


    /**
     * WBS별 Task 조회
     *
     * GET /api/wbs/{wbsId}/tasks
     *
     * @param wbsId WBS ID
     * @return Task 목록
     */
    @GetMapping("/wbs/{wbsId}/tasks")
    @Operation(
            summary = "WBS별 Task 조회",
            description = "특정 WBS에 연결된 Task를 sortOrder 오름차순으로 조회한다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Task 목록 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "WBS를 찾을 수 없음"
            )
    })
    public ResponseEntity<List<TaskResponse>> findByWbsId(
            @Parameter(
                    description = "WBS ID",
                    required = true,
                    in = ParameterIn.PATH
            )
            @PathVariable Long wbsId
    ) {

        return ResponseEntity.ok(
                taskService.findByWbsId(wbsId)
        );
    }


    /**
     * Schedule별 Task 조회
     *
     * GET /api/schedules/{scheduleId}/tasks
     *
     * @param scheduleId Schedule ID
     * @return Task 목록
     */
    @GetMapping("/schedules/{scheduleId}/tasks")
    @Operation(
            summary = "Schedule별 Task 조회",
            description = "특정 Schedule에 연결된 Task를 sortOrder 오름차순으로 조회한다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Task 목록 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Schedule을 찾을 수 없음"
            )
    })
    public ResponseEntity<List<TaskResponse>> findByScheduleId(
            @Parameter(
                    description = "Schedule ID",
                    required = true,
                    in = ParameterIn.PATH
            )
            @PathVariable Long scheduleId
    ) {

        return ResponseEntity.ok(
                taskService.findByScheduleId(scheduleId)
        );
    }


    /**
     * 담당자별 Task 조회
     *
     * GET /api/users/{assigneeId}/tasks
     *
     * @param assigneeId 담당자 User ID
     * @return 담당 Task 목록
     */
    @GetMapping("/users/{assigneeId}/tasks")
    @Operation(
            summary = "담당자별 Task 조회",
            description = "특정 사용자가 담당하는 Task를 시작일 오름차순으로 조회한다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "담당 Task 목록 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자를 찾을 수 없음"
            )
    })
    public ResponseEntity<List<TaskResponse>> findByAssigneeId(
            @Parameter(
                    description = "담당자 User ID",
                    required = true,
                    in = ParameterIn.PATH
            )
            @PathVariable Long assigneeId
    ) {

        return ResponseEntity.ok(
                taskService.findByAssigneeId(assigneeId)
        );
    }


    /**
     * Task 조건 검색
     *
     * GET /api/tasks
     *
     * Query Parameter:
     * - projectId
     * - wbsId
     * - scheduleId
     * - assigneeId
     * - keyword
     * - status
     * - priority
     * - startDate
     * - endDate
     * - sortBy
     * - direction
     * - page
     * - size
     *
     * @param request Task 검색 요청
     * @return Task Page
     */
    @GetMapping("/tasks")
    @Operation(
            summary = "Task 검색",
            description = """
                    Project, WBS, Schedule, 담당자, keyword,
                    status, priority 및 날짜 조건을 이용하여 Task를 검색한다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Task 검색 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 검색 조건"
            )
    })
    public ResponseEntity<Page<TaskResponse>> search(
            @ModelAttribute TaskSearchRequest request
    ) {

        return ResponseEntity.ok(
                taskService.search(request)
        );
    }


    /**
     * 기간 조건 Task 검색
     *
     * GET /api/tasks/period
     *
     * Query Parameter:
     * - projectId
     * - wbsId
     * - scheduleId
     * - assigneeId
     * - keyword
     * - status
     * - priority
     * - sortBy
     * - direction
     * - page
     * - size
     * - searchStart
     * - searchEnd
     *
     * 지정한 검색 기간과 Task 수행 기간이 겹치는
     * Task를 조회한다.
     *
     * @param request Task 검색 요청
     * @param searchStart 검색 시작일
     * @param searchEnd 검색 종료일
     * @return Task Page
     */
    @GetMapping("/tasks/period")
    @Operation(
            summary = "기간 조건 Task 검색",
            description = "검색 기간과 Task 수행 기간이 겹치는 Task를 조회한다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "기간 조건 Task 검색 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 검색 기간"
            )
    })
    public ResponseEntity<Page<TaskResponse>> searchByPeriod(
            @ModelAttribute TaskSearchRequest request,

            @Parameter(
                    description = "검색 시작일",
                    required = true,
                    example = "2026-08-01"
            )
            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate searchStart,

            @Parameter(
                    description = "검색 종료일",
                    required = true,
                    example = "2026-08-31"
            )
            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate searchEnd
    ) {

        return ResponseEntity.ok(
                taskService.searchByPeriod(
                        request,
                        searchStart,
                        searchEnd
                )
        );
    }


    /**
     * Task 생성
     *
     * POST /api/projects/{projectId}/tasks
     *
     * Project는 Path Variable을 통해 결정한다.
     *
     * @param projectId Project ID
     * @param request Task 생성 요청
     * @return 생성된 Task
     */
    @PostMapping("/projects/{projectId}/tasks")
    @Operation(
            summary = "Task 생성",
            description = "특정 Project에 Task를 생성한다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Task 생성 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 Task 생성 요청"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project, WBS, Schedule 또는 담당자를 찾을 수 없음"
            )
    })
    public ResponseEntity<TaskResponse> create(
            @Parameter(
                    description = "Project ID",
                    required = true,
                    in = ParameterIn.PATH
            )
            @PathVariable Long projectId,

            @Valid @RequestBody TaskCreateRequest request
    ) {

        TaskResponse response =
                taskService.create(
                        projectId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    /**
     * Task 수정
     *
     * PUT /api/tasks/{id}
     *
     * Project는 변경하지 않는다.
     *
     * WBS, Schedule 변경 시 Service에서
     * 기존 Project와의 소속 관계를 검증한다.
     *
     * @param id Task ID
     * @param request Task 수정 요청
     * @return 수정된 Task
     */
    @PutMapping("/tasks/{id}")
    @Operation(
            summary = "Task 수정",
            description = "Task 정보를 수정한다. Project는 변경하지 않는다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Task 수정 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 Task 수정 요청"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Task, WBS, Schedule 또는 담당자를 찾을 수 없음"
            )
    })
    public ResponseEntity<TaskResponse> update(
            @Parameter(
                    description = "Task ID",
                    required = true,
                    in = ParameterIn.PATH
            )
            @PathVariable Long id,

            @Valid @RequestBody TaskUpdateRequest request
    ) {

        return ResponseEntity.ok(
                taskService.update(
                        id,
                        request
                )
        );
    }


    /**
     * Task 상태 변경
     *
     * PATCH /api/tasks/{id}/status
     *
     * Request Body:
     * {
     *     "status": "IN_PROGRESS"
     * }
     *
     * @param id Task ID
     * @param request 상태 변경 요청
     * @return 변경된 Task
     */
    @PatchMapping("/tasks/{id}/status")
    @Operation(
            summary = "Task 상태 변경",
            description = """
                    Task의 상태를 변경한다.

                    COMPLETED로 변경하면 진척률은 100으로 설정된다.
                    CANCELLED 및 ON_HOLD로 변경하면 기존 진척률을 유지한다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Task 상태 변경 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 상태 변경 요청"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Task를 찾을 수 없음"
            )
    })
    public ResponseEntity<TaskResponse> changeStatus(
            @Parameter(
                    description = "Task ID",
                    required = true,
                    in = ParameterIn.PATH
            )
            @PathVariable Long id,

            @Valid @RequestBody TaskStatusChangeRequest request
    ) {

        return ResponseEntity.ok(
                taskService.changeStatus(
                        id,
                        request.getStatus()
                )
        );
    }


    /**
     * Task 삭제
     *
     * DELETE /api/tasks/{id}
     *
     * @param id Task ID
     * @return 응답
     */
    @DeleteMapping("/tasks/{id}")
    @Operation(
            summary = "Task 삭제",
            description = "Task를 삭제한다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Task 삭제 성공"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Task를 찾을 수 없음"
            )
    })
    public ResponseEntity<Void> delete(
            @Parameter(
                    description = "Task ID",
                    required = true,
                    in = ParameterIn.PATH
            )
            @PathVariable Long id
    ) {

        taskService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
