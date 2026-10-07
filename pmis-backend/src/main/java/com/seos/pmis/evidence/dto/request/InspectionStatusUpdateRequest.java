package com.seos.pmis.evidence.dto.request;

import com.seos.pmis.evidence.enums.InspectionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Inspection 상태 변경 요청 DTO
 *
 * Inspection Workflow 상태를 변경할 때 사용한다.
 *
 * Workflow:
 * - PLANNED
 * - IN_PROGRESS
 * - PASSED
 * - FAILED
 * - CLOSED
 *
 * 상태 변경에 필요한 정보만 Client에서 전달받고,
 * 실제 상태 전이 검증 및 시작/완료 일시 기록은
 * Service에서 처리한다.
 *
 * Backend 관리 필드:
 * - id
 * - projectId
 * - startedAt
 * - completedAt
 * - createdAt
 * - updatedAt
 */
@Getter
@NoArgsConstructor
public class InspectionStatusUpdateRequest {

    /**
     * 변경할 Inspection 상태
     */
    @NotNull(message = "Inspection 상태는 필수입니다.")
    private InspectionStatus status;

    /**
     * 상태 변경에 대한 결과 비고
     *
     * 특히 PASSED / FAILED 상태 변경 시
     * 결과 또는 사유를 기록하는 데 사용한다.
     */
    private String resultRemark;
}