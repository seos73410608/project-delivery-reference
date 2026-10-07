package com.seos.pmis.evidence.dto.response;

import com.seos.pmis.evidence.enums.InspectionStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Inspection 응답 DTO
 *
 * Inspection 조회 결과를
 * Client에게 전달할 때 사용한다.
 *
 * 포함 정보:
 * - Inspection 기본 정보
 * - Project 정보
 * - Inspection Workflow 상태
 * - Inspection 시작/완료 정보
 * - 생성/수정 일시
 *
 * Project Entity 자체는 전달하지 않고
 * projectId만 전달한다.
 */
@Getter
@Builder
public class InspectionResponse {

    /**
     * Inspection ID
     */
    private Long id;

    /**
     * 프로젝트 ID
     */
    private Long projectId;

    /**
     * Inspection 제목
     */
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
     * Inspection 시작 일시
     */
    private LocalDateTime startedAt;

    /**
     * Inspection 완료 일시
     */
    private LocalDateTime completedAt;

    /**
     * Inspection 상태
     *
     * PLANNED
     * IN_PROGRESS
     * PASSED
     * FAILED
     * CLOSED
     */
    private InspectionStatus status;

    /**
     * 검사자 / 검수자 이름
     */
    private String inspectorName;

    /**
     * Inspection 결과 비고
     */
    private String resultRemark;

    /**
     * 생성 일시
     */
    private LocalDateTime createdAt;

    /**
     * 수정 일시
     */
    private LocalDateTime updatedAt;
}