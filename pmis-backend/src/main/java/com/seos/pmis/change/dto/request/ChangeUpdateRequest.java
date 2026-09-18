package com.seos.pmis.change.dto.request;

import com.seos.pmis.change.entity.ChangeImpactLevel;
import com.seos.pmis.change.entity.ChangePriority;
import com.seos.pmis.change.entity.ChangeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class ChangeUpdateRequest {

    /**
     * 변경 제목
     */
    @NotBlank(message = "변경 제목은 필수입니다.")
    private String title;

    /**
     * 변경 설명
     */
    private String description;

    /**
     * 변경 우선순위
     */
    @NotNull(message = "변경 우선순위는 필수입니다.")
    private ChangePriority priority;

    /**
     * 변경 유형
     */
    @NotNull(message = "변경 유형은 필수입니다.")
    private ChangeType changeType;

    /**
     * 변경 영향도
     */
    @NotNull(message = "변경 영향도는 필수입니다.")
    private ChangeImpactLevel impactLevel;

    /**
     * 변경 요청자 ID
     */
    private Long requesterId;

    /**
     * 변경 담당자 ID
     */
    private Long assigneeId;

    /**
     * 변경 식별일
     */
    private LocalDate identifiedDate;

    /**
     * 변경 요청일
     */
    private LocalDate requestedDate;

    /**
     * 변경 완료 목표일
     */
    private LocalDate dueDate;

    /**
     * 영향도 분석
     */
    private String impactAnalysis;

    /**
     * 구현 계획
     */
    private String implementationPlan;

    /**
     * 정렬 순서
     */
    private Integer sortOrder;
}