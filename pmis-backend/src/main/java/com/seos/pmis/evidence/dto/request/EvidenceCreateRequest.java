package com.seos.pmis.evidence.dto.request;

import com.seos.pmis.evidence.enums.EvidenceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Evidence 생성 요청 DTO
 *
 * Evidence Requirement에 실제 증적을 등록할 때 사용한다.
 *
 * 지원 방식:
 * 1. 수동 증적 등록
 *    - fileName, filePath를 사용한다.
 *
 * 2. Automation Runtime 연동 증적 등록
 *    - sourceFileId, executionUuid, sourceJobId를 사용한다.
 *
 * Runtime 연동 필드는 기존 수동 등록과의 호환성을 위해
 * 선택 사항으로 관리한다.
 *
 * Backend 관리 필드:
 * - id
 * - projectId
 * - requirementId
 * - evidenceKey
 * - verificationStatus
 * - verifiedBy
 * - verifiedAt
 * - createdAt
 * - updatedAt
 *
 * 위 필드는 Client에서 전달받지 않는다.
 */
@Getter
@NoArgsConstructor
public class EvidenceCreateRequest {

    /**
     * WBS ID
     *
     * Evidence가 특정 WBS에 연결되는 경우 사용한다.
     * 선택 사항이다.
     */
    private Long wbsId;

    /**
     * Evidence 유형
     */
    @NotNull(message = "Evidence 유형은 필수입니다.")
    private EvidenceType evidenceType;

    /**
     * Evidence 제목
     */
    @NotBlank(message = "Evidence 제목은 필수입니다.")
    @Size(
            max = 200,
            message = "Evidence 제목은 200자 이하여야 합니다."
    )
    private String title;

    /**
     * Evidence 설명
     */
    private String description;

    /**
     * 증적 파일명
     *
     * 수동 등록 증적의 파일명 또는
     * Runtime 파일 메타데이터의 표시용 파일명이다.
     *
     * 선택 사항이다.
     */
    @Size(
            max = 255,
            message = "파일명은 255자 이하여야 합니다."
    )
    private String fileName;

    /**
     * 증적 파일 경로
     *
     * 기존 수동 등록 방식과의 호환성을 위해 유지한다.
     *
     * Runtime 연동 증적의 실제 파일 접근은
     * Runtime 콘텐츠 API를 통해 처리한다.
     */
    @Size(
            max = 1000,
            message = "파일 경로는 1000자 이하여야 합니다."
    )
    private String filePath;

    /**
     * Automation Runtime 파일 ID
     *
     * Runtime의 tb_output_file.file_id를 전달한다.
     *
     * 기존 수동 등록 증적에서는 null이다.
     *
     * Runtime 파일의 존재 여부 및 메타데이터 검증은
     * Service 계층에서 수행한다.
     */
    @Size(
            max = 64,
            message = "Runtime 파일 ID는 64자 이하여야 합니다."
    )
    private String sourceFileId;

    /**
     * Automation Runtime 실행 UUID
     *
     * Runtime 파일이 생성된 실행을 식별한다.
     *
     * 기존 수동 등록 증적에서는 null이다.
     */
    @Size(
            max = 64,
            message = "Runtime 실행 UUID는 64자 이하여야 합니다."
    )
    private String executionUuid;

    /**
     * Automation Runtime Job ID
     *
     * 해당 파일을 생성한 자동화 Job을 식별한다.
     *
     * 기존 수동 등록 증적에서는 null이다.
     */
    @Size(
            max = 64,
            message = "Runtime Job ID는 64자 이하여야 합니다."
    )
    private String sourceJobId;

    /**
     * 실제 증적 제출 일시
     *
     * Client에서 명시하지 않을 경우
     * Service에서 현재 시각을 사용할 수 있다.
     */
    private LocalDateTime submittedAt;
}