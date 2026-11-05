
package com.seos.pmis.task.dto.request;

import com.seos.pmis.task.entity.TaskPriority;
import com.seos.pmis.task.entity.TaskStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Task 생성 요청 DTO
 *
 * Project는 URL Path의 projectId를 통해 결정하므로
 * Request Body에서는 전달하지 않는다.
 *
 * Backend 관리 값:
 * - projectId
 *
 * Client 입력 값:
 * - wbsId
 * - scheduleId
 * - assigneeId
 * - taskName
 * - description
 * - status
 * - progressRate
 * - priority
 * - startDate
 * - endDate
 * - sortOrder
 */
@Getter
@NoArgsConstructor
public class TaskCreateRequest {

    /**
     * 소속 WBS ID
     */
    @NotNull(message = "WBS ID는 필수입니다.")
    private Long wbsId;

    /**
     * 연결 Schedule ID
     *
     * 선택 입력
     */
    private Long scheduleId;

    /**
     * 담당자 User ID
     *
     * 선택 입력
     */
    private Long assigneeId;

    /**
     * 작업명
     */
    @NotBlank(message = "작업명은 필수입니다.")
    @Size(max = 200, message = "작업명은 200자를 초과할 수 없습니다.")
    private String taskName;

    /**
     * 작업 설명
     */
    private String description;

    /**
     * 작업 상태
     */
    private TaskStatus status;

    /**
     * 작업 진척률 (0~100)
     */
    @Min(value = 0, message = "진척률은 0 이상이어야 합니다.")
    @Max(value = 100, message = "진척률은 100 이하여야 합니다.")
    private Integer progressRate;

    /**
     * 작업 우선순위
     */
    private TaskPriority priority;

    /**
     * 작업 시작일
     */
    private LocalDate startDate;

    /**
     * 작업 종료일
     */
    private LocalDate endDate;

    /**
     * 동일 WBS 내 표시 순서
     */
    private Integer sortOrder;
}
