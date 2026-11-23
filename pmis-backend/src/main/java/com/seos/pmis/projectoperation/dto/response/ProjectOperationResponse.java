
package com.seos.pmis.projectoperation.dto.response;

import com.seos.pmis.operation.entity.OperationStatus;
import com.seos.pmis.operation.entity.OperationType;
import com.seos.pmis.projectoperation.entity.ProjectOperationRelationshipType;
import com.seos.pmis.projectoperation.entity.ProjectOperationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class ProjectOperationResponse {

    /**
     * 프로젝트 운영 관계 ID
     */
    private Long id;

    /**
     * 관계 유형 및 상태
     */
    private ProjectOperationRelationshipType relationshipType;
    private ProjectOperationStatus status;

    /**
     * 프로젝트 정보
     */
    private Long projectId;
    private String projectCode;
    private String projectName;
    private String customerName;
    private String projectManager;

    /**
     * 운영 서비스 정보
     */
    private Long operationId;
    private String operationCode;
    private String operationName;
    private OperationType operationType;
    private OperationStatus operationStatus;

    /**
     * 관계 적용 기간
     */
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;

    /**
     * 관계 설명
     */
    private String description;

    /**
     * 생성 및 수정 일시
     */
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
