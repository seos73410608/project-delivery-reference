package com.seos.pmis.schedule.dto.request;

import com.seos.pmis.schedule.entity.ScheduleStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Schedule 생성 요청 DTO
 *
 * Project는 URL Path의 projectId를 통해 결정되므로
 * Request Body에서는 전달하지 않는다.
 *
 * Backend 관리 값:
 * - projectId
 *
 * Client 입력 값:
 * - wbsId
 * - scheduleName
 * - startDate
 * - endDate
 * - status
 * - sortOrder
 * - description
 */
@Getter
@NoArgsConstructor
public class ScheduleCreateRequest {

    /**
     * 연결할 WBS ID
     */
    private Long wbsId;

    /**
     * Schedule 명
     */
    private String scheduleName;

    /**
     * Schedule 시작일
     */
    private LocalDate startDate;

    /**
     * Schedule 종료일
     */
    private LocalDate endDate;

    /**
     * Schedule 상태
     */
    private ScheduleStatus status;

    /**
     * 동일 WBS 내 표시 순서
     */
    private Integer sortOrder;

    /**
     * Schedule 설명
     */
    private String description;
}