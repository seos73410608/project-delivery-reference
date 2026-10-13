package com.seos.pmis.evidence.dto.response;

import com.seos.pmis.evidence.enums.EvidenceType;
import com.seos.pmis.evidence.enums.VerificationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Evidence 응답 DTO
 *
 * Evidence 조회 결과를 Client에게 전달할 때 사용한다.
 *
 * 포함 정보:
 * - Evidence 기본 정보
 * - Project / Requirement / WBS 정보
 * - 제출자 정보
 * - Verification 정보
 * - Automation Runtime 연동 정보
 * - 생성/수정 일시
 *
 * 연관 Entity 자체는 Client에게 전달하지 않고
 * 필요한 식별자와 표시 정보만 전달한다.
 */
@Getter
@Builder
public class EvidenceResponse {

    /**
     * Evidence ID
     */
    private Long id;

    /**
     * 프로젝트 ID
     */
    private Long projectId;

    /**
     * Requirement ID
     */
    private Long requirementId;

    /**
     * WBS ID
     *
     * 특정 WBS에 연결되지 않은 경우 null일 수 있다.
     */
    private Long wbsId;

    /**
     * Evidence Key
     *
     * 예:
     * EVD-001
     * EVD-002
     */
    private String evidenceKey;

    /**
     * Evidence 유형
     */
    private EvidenceType evidenceType;

    /**
     * Evidence 제목
     */
    private String title;

    /**
     * Evidence 설명
     */
    private String description;

    /**
     * 증적 파일명
     */
    private String fileName;

    /**
     * 증적 파일 경로
     *
     * 기존 수동 등록 방식과의 호환성을 위해 유지한다.
     * Runtime 내부 파일 경로를 Client에 직접 노출하지 않도록 주의한다.
     */
    private String filePath;

    /**
     * Automation Runtime 파일 ID
     *
     * Runtime의 tb_output_file.file_id를 식별한다.
     *
     * 수동 등록 증적에서는 null이다.
     */
    private String sourceFileId;

    /**
     * Automation Runtime 실행 UUID
     *
     * 해당 파일을 생성한 Runtime 실행을 식별한다.
     *
     * 수동 등록 증적에서는 null이다.
     */
    private String executionUuid;

    /**
     * Automation Runtime Job ID
     *
     * 해당 파일을 생성한 자동화 Job을 식별한다.
     *
     * 수동 등록 증적에서는 null이다.
     */
    private String sourceJobId;

    /**
     * 제출자 User ID
     */
    private Long submittedById;

    /**
     * 제출자 이름
     *
     * 화면에서 제출자 정보를 표시할 때 사용한다.
     */
    private String submittedByName;

    /**
     * 실제 증적 제출 일시
     */
    private LocalDateTime submittedAt;

    /**
     * Verification 상태
     *
     * PENDING
     * APPROVED
     * REJECTED
     */
    private VerificationStatus verificationStatus;

    /**
     * 검증자 User ID
     *
     * 아직 검증되지 않은 경우 null이다.
     */
    private Long verifiedById;

    /**
     * 검증자 이름
     *
     * 아직 검증되지 않은 경우 null이다.
     */
    private String verifiedByName;

    /**
     * 검증 완료 일시
     *
     * 아직 검증되지 않은 경우 null이다.
     */
    private LocalDateTime verifiedAt;

    /**
     * 검증 의견
     */
    private String verificationRemark;

    /**
     * 생성 일시
     */
    private LocalDateTime createdAt;

    /**
     * 수정 일시
     */
    private LocalDateTime updatedAt;
}