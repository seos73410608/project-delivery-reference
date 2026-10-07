package com.seos.pmis.evidence.dto.request;

import com.seos.pmis.evidence.enums.VerificationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Evidence Verification 요청 DTO
 *
 * Evidence의 검증 상태를 변경할 때 사용한다.
 *
 * Verification 상태:
 * - PENDING
 * - APPROVED
 * - REJECTED
 *
 * Backend 관리 필드:
 * - evidenceId
 * - verifiedBy
 * - verifiedAt
 *
 * 위 필드는 Client에서 직접 전달받지 않는다.
 *
 * verifiedBy는 인증된 사용자 정보를 기반으로
 * Service에서 결정한다.
 *
 * verifiedAt 역시 Service에서 현재 시각으로 기록한다.
 */
@Getter
@NoArgsConstructor
public class EvidenceVerificationRequest {

    /**
     * Verification 상태
     *
     * APPROVED 또는 REJECTED로 변경할 수 있다.
     */
    @NotNull(message = "Verification 상태는 필수입니다.")
    private VerificationStatus status;

    /**
     * Verification 의견
     *
     * APPROVED:
     * 검증 완료 의견을 기록할 수 있다.
     *
     * REJECTED:
     * 반려 사유를 기록한다.
     */
    private String remark;
}