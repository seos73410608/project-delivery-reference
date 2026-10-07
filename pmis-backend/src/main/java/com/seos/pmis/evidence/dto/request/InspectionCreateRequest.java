package com.seos.pmis.evidence.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Inspection 생성 요청 DTO
 *
 * 프로젝트의 공식 점검/검수 일정을 생성할 때 사용한다.
 *
 * Backend 관리 필드:
 * - id
 * - projectId
 * - status
 * - startedAt
 * - completedAt
 * - createdAt
 * - updatedAt
 *
 * 위 필드는 Client에서 직접 전달받지 않는다.
 */
@Getter
@NoArgsConstructor
public class InspectionCreateRequest {

    /**
     * Inspection 제목
     */
    @NotBlank(message = "Inspection 제목은 필수입니다.")
    @Size(
            max = 200,
            message = "Inspection 제목은 200자 이하여야 합니다."
    )
    private String title;

    /**
     * Inspection 설명
     */
    private String description;

    /**
     * 예정된 Inspection 일자
     */
    private LocalDate inspectionDate;

    /**
     * 검사자 / 검수자 이름
     */
    @Size(
            max = 100,
            message = "검사자 이름은 100자 이하여야 합니다."
    )
    private String inspectorName;

    /**
     * Inspection 결과 비고
     *
     * 생성 시점에는 일반적으로 비어 있을 수 있다.
     */
    private String resultRemark;
}