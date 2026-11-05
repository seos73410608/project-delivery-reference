
package com.seos.pmis.task.dto.request;

import com.seos.pmis.task.entity.TaskPriority;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Task 수정 요청 DTO
 *
 * Project는 변경하지 않는다.
 *
 * WBS 변경이 필요한 경우
 * Service에서 기존 Project와 WBS의 소속 관계를
 * 검증한 후 Entity의 WBS를 변경한다.
 *
 * Schedule 및 담당자 변경도
 * Service에서 유효성과 소속 관계를 검증한다.
 */
@Getter
@NoArgsConstructor
public class TaskUpdateRequest {

    /**
     * 변경할 WBS ID
     */
    @NotNull(message = "WBS ID는 필수입니다.")
    private Long wbsId;

    /**
     * 변경할 Schedule ID
     *
     * null인 경우 연결 해제
     */
    private Long scheduleId;

    /**
     * 변경할 담당자 User ID
     *
     * null인 경우 담당자 해제
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
     * 작업 우선순위
     */
    @NotNull(message = "작업 우선순위는 필수입니다.")
    private TaskPriority priority;

    /**
     * 작업 진척률 (0~100)
     */
    @Min(value = 0, message = "진척률은 0 이상이어야 합니다.")
    @Max(value = 100, message = "진척률은 100 이하여야 합니다.")
    private Integer progressRate;

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
