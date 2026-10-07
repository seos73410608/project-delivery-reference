package com.seos.pmis.evidence.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Inspection 수정 요청 DTO
 *
 * 기존 Inspection의 기본 정보를 수정할 때 사용한다.
 *
 * Client 수정 가능 필드:
 * - title
 * - description
 * - inspectionDate
 * - inspectorName
 * - resultRemark
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
 * Workflow 관련 필드는 별도의 상태 변경 API를 통해 관리한다.
 */
@Getter
@NoArgsConstructor
public class InspectionUpdateRequest {

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
     * 진행 중이거나 종료된 Inspection의
     * 결과 관련 메모를 수정할 때 사용한다.
     */
    private String resultRemark;
}