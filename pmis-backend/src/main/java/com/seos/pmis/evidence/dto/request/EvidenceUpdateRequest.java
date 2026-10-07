package com.seos.pmis.evidence.dto.request;

import com.seos.pmis.evidence.enums.EvidenceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Evidence 수정 요청 DTO
 *
 * 기존 Evidence의 정보를 수정할 때 사용한다.
 *
 * Client 수정 가능 필드:
 * - wbsId
 * - evidenceType
 * - title
 * - description
 * - fileName
 * - filePath
 * - submittedAt
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
 * 위 Backend 관리 필드는 Client에서 전달받지 않는다.
 */
@Getter
@NoArgsConstructor
public class EvidenceUpdateRequest {

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
     * V1에서는 실제 파일 업로드를 처리하지 않고
     * 파일명 정보만 관리한다.
     */
    @Size(
            max = 255,
            message = "파일명은 255자 이하여야 합니다."
    )
    private String fileName;

    /**
     * 증적 파일 경로
     *
     * V1에서는 실제 파일 저장소를 구현하지 않고
     * 파일 경로 정보만 관리한다.
     */
    @Size(
            max = 1000,
            message = "파일 경로는 1000자 이하여야 합니다."
    )
    private String filePath;

    /**
     * 실제 증적 제출 일시
     */
    private LocalDateTime submittedAt;
}