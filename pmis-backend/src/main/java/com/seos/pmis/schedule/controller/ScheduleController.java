package com.seos.pmis.schedule.controller;

import com.seos.pmis.schedule.dto.request.ScheduleCreateRequest;
import com.seos.pmis.schedule.dto.request.ScheduleSearchRequest;
import com.seos.pmis.schedule.dto.request.ScheduleUpdateRequest;
import com.seos.pmis.schedule.dto.response.ScheduleResponse;
import com.seos.pmis.schedule.entity.ScheduleStatus;
import com.seos.pmis.schedule.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Schedule Controller
 *
 * Schedule Domain의 REST API를 담당한다.
 *
 * 주요 책임:
 * - Schedule 단건 조회
 * - Project별 Schedule 조회
 * - WBS별 Schedule 조회
 * - Schedule 검색
 * - 기간 조건 Schedule 검색
 * - Schedule 생성
 * - Schedule 수정
 * - Schedule 상태 변경
 * - Schedule 정렬 순서 변경
 * - Schedule 삭제
 *
 * API의 비즈니스 로직은 ScheduleService에 위임한다.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(
        name = "Schedule",
        description = "Schedule Domain API"
)
public class ScheduleController {

    private final ScheduleService scheduleService;

    /**
     * Schedule 단건 조회
     *
     * GET /api/schedules/{id}
     *
     * @param id Schedule ID
     * @return Schedule Response
     */
    @GetMapping("/schedules/{id}")
    @Operation(
            summary = "Schedule 단건 조회",
            description = "Schedule ID를 기준으로 Schedule을 조회한다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Schedule 조회 성공",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ScheduleResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Schedule을 찾을 수 없음"
            )
    })
    public ResponseEntity<ScheduleResponse> findById(
            @Parameter(
                    description = "Schedule ID",
                    required = true,
                    in = ParameterIn.PATH
            )
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                scheduleService.findById(id)
        );
    }

    /**
     * Project별 Schedule 조회
     *
     * GET /api/projects/{projectId}/schedules
     *
     * @param projectId Project ID
     * @return Schedule 목록
     */
    @GetMapping("/projects/{projectId}/schedules")
    @Operation(
            summary = "Project별 Schedule 조회",
            description = "특정 Project에 속한 Schedule을 sortOrder 오름차순으로 조회한다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Schedule 목록 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project를 찾을 수 없음"
            )
    })
    public ResponseEntity<List<ScheduleResponse>> findByProjectId(
            @Parameter(
                    description = "Project ID",
                    required = true,
                    in = ParameterIn.PATH
            )
            @PathVariable Long projectId
    ) {

        return ResponseEntity.ok(
                scheduleService.findByProjectId(projectId)
        );
    }

    /**
     * WBS별 Schedule 조회
     *
     * GET /api/wbs/{wbsId}/schedules
     *
     * @param wbsId WBS ID
     * @return Schedule 목록
     */
    @GetMapping("/wbs/{wbsId}/schedules")
    @Operation(
            summary = "WBS별 Schedule 조회",
            description = "특정 WBS에 연결된 Schedule을 sortOrder 오름차순으로 조회한다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Schedule 목록 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "WBS를 찾을 수 없음"
            )
    })
    public ResponseEntity<List<ScheduleResponse>> findByWbsId(
            @Parameter(
                    description = "WBS ID",
                    required = true,
                    in = ParameterIn.PATH
            )
            @PathVariable Long wbsId
    ) {

        return ResponseEntity.ok(
                scheduleService.findByWbsId(wbsId)
        );
    }

    /**
     * Schedule 조건 검색
     *
     * GET /api/schedules
     *
     * Query Parameter:
     * - projectId
     * - wbsId
     * - keyword
     * - status
     * - sortBy
     * - direction
     * - page
     * - size
     *
     * @param request Schedule 검색 요청
     * @return Schedule Page
     */
    @GetMapping("/schedules")
    @Operation(
            summary = "Schedule 검색",
            description = "Project, WBS, keyword, status 및 정렬 조건을 이용하여 Schedule을 검색한다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Schedule 검색 성공"
            )
    })
    public ResponseEntity<Page<ScheduleResponse>> search(
            @ModelAttribute ScheduleSearchRequest request
    ) {

        return ResponseEntity.ok(
                scheduleService.search(request)
        );
    }

    /**
     * 기간 조건 Schedule 검색
     *
     * GET /api/schedules/period
     *
     * Query Parameter:
     * - projectId
     * - wbsId
     * - keyword
     * - status
     * - sortBy
     * - direction
     * - page
     * - size
     * - searchStart
     * - searchEnd
     *
     * 특정 검색 기간과 Schedule 기간이 겹치는
     * Schedule을 조회한다.
     *
     * @param request Schedule 검색 요청
     * @param searchStart 검색 시작일
     * @param searchEnd 검색 종료일
     * @return Schedule Page
     */
    @GetMapping("/schedules/period")
    @Operation(
            summary = "기간 조건 Schedule 검색",
            description = "검색 기간과 일정 기간이 겹치는 Schedule을 조회한다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "기간 조건 Schedule 검색 성공"
            )
    })
    public ResponseEntity<Page<ScheduleResponse>> searchByPeriod(
            @ModelAttribute ScheduleSearchRequest request,

            @Parameter(
                    description = "검색 시작일",
                    required = true,
                    example = "2026-08-01"
            )
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate searchStart,

            @Parameter(
                    description = "검색 종료일",
                    required = true,
                    example = "2026-08-31"
            )
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate searchEnd
    ) {

        return ResponseEntity.ok(
                scheduleService.searchByPeriod(
                        request,
                        searchStart,
                        searchEnd
                )
        );
    }

    /**
     * Schedule 생성
     *
     * POST /api/projects/{projectId}/schedules
     *
     * Project는 Path Variable을 통해 결정한다.
     *
     * @param projectId Project ID
     * @param request Schedule 생성 요청
     * @return 생성된 Schedule
     */
    @PostMapping("/projects/{projectId}/schedules")
    @Operation(
            summary = "Schedule 생성",
            description = "특정 Project에 Schedule을 생성한다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Schedule 생성 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 Schedule 생성 요청"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project 또는 WBS를 찾을 수 없음"
            )
    })
    public ResponseEntity<ScheduleResponse> create(
            @Parameter(
                    description = "Project ID",
                    required = true,
                    in = ParameterIn.PATH
            )
            @PathVariable Long projectId,

            @RequestBody ScheduleCreateRequest request
    ) {

        ScheduleResponse response =
                scheduleService.create(
                        projectId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Schedule 수정
     *
     * PUT /api/schedules/{id}
     *
     * Project는 변경하지 않는다.
     *
     * WBS 변경 시 Service에서
     * 기존 Project와 WBS의 소속 관계를 검증한다.
     *
     * @param id Schedule ID
     * @param request Schedule 수정 요청
     * @return 수정된 Schedule
     */
    @PutMapping("/schedules/{id}")
    @Operation(
            summary = "Schedule 수정",
            description = "Schedule 정보를 수정한다. Project는 변경하지 않는다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Schedule 수정 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 Schedule 수정 요청"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Schedule 또는 WBS를 찾을 수 없음"
            )
    })
    public ResponseEntity<ScheduleResponse> update(
            @Parameter(
                    description = "Schedule ID",
                    required = true,
                    in = ParameterIn.PATH
            )
            @PathVariable Long id,

            @RequestBody ScheduleUpdateRequest request
    ) {

        return ResponseEntity.ok(
                scheduleService.update(
                        id,
                        request
                )
        );
    }

    /**
     * Schedule 상태 변경
     *
     * PATCH /api/schedules/{id}/status
     *
     * @param id Schedule ID
     * @param status 변경할 Schedule Status
     * @return 변경된 Schedule
     */
    @PatchMapping("/schedules/{id}/status")
    @Operation(
            summary = "Schedule 상태 변경",
            description = "Schedule의 상태를 변경한다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Schedule 상태 변경 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 Status"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Schedule을 찾을 수 없음"
            )
    })
    public ResponseEntity<ScheduleResponse> changeStatus(
            @Parameter(
                    description = "Schedule ID",
                    required = true,
                    in = ParameterIn.PATH
            )
            @PathVariable Long id,

            @Parameter(
                    description = "변경할 Schedule 상태",
                    required = true,
                    example = "IN_PROGRESS"
            )
            @RequestParam ScheduleStatus status
    ) {

        return ResponseEntity.ok(
                scheduleService.changeStatus(
                        id,
                        status
                )
        );
    }

    /**
     * Schedule 정렬 순서 변경
     *
     * PATCH /api/schedules/{id}/sort-order
     *
     * @param id Schedule ID
     * @param sortOrder 변경할 정렬 순서
     * @return 변경된 Schedule
     */
    @PatchMapping("/schedules/{id}/sort-order")
    @Operation(
            summary = "Schedule 정렬 순서 변경",
            description = "Schedule의 표시 순서를 변경한다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Schedule 정렬 순서 변경 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 정렬 순서"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Schedule을 찾을 수 없음"
            )
    })
    public ResponseEntity<ScheduleResponse> changeSortOrder(
            @Parameter(
                    description = "Schedule ID",
                    required = true,
                    in = ParameterIn.PATH
            )
            @PathVariable Long id,

            @Parameter(
                    description = "변경할 정렬 순서",
                    required = true,
                    example = "2"
            )
            @RequestParam Integer sortOrder
    ) {

        return ResponseEntity.ok(
                scheduleService.changeSortOrder(
                        id,
                        sortOrder
                )
        );
    }

    /**
     * Schedule 삭제
     *
     * DELETE /api/schedules/{id}
     *
     * @param id Schedule ID
     */
    @DeleteMapping("/schedules/{id}")
    @Operation(
            summary = "Schedule 삭제",
            description = "Schedule을 삭제한다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Schedule 삭제 성공"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Schedule을 찾을 수 없음"
            )
    })
    public ResponseEntity<Void> delete(
            @Parameter(
                    description = "Schedule ID",
                    required = true,
                    in = ParameterIn.PATH
            )
            @PathVariable Long id
    ) {

        scheduleService.delete(id);

        return ResponseEntity.noContent().build();
    }
}