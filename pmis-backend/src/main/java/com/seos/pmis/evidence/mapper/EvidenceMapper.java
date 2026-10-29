package com.seos.pmis.evidence.mapper;

import com.seos.pmis.evidence.dto.request.EvidenceCreateRequest;
import com.seos.pmis.evidence.dto.request.EvidenceUpdateRequest;
import com.seos.pmis.evidence.dto.response.EvidenceResponse;
import com.seos.pmis.evidence.entity.Evidence;
import com.seos.pmis.evidence.entity.EvidenceRequirement;
import com.seos.pmis.evidence.enums.VerificationStatus;
import com.seos.pmis.project.entity.Project;
import com.seos.pmis.user.entity.User;
import com.seos.pmis.wbs.entity.Wbs;
import com.seos.pmis.evidence.dto.response.EvidenceVersionResponse;
import com.seos.pmis.evidence.entity.EvidenceVersion;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Evidence Mapper
 *
 * Evidence의 Entity ↔ DTO 변환을 담당한다.
 *
 * Mapper의 책임:
 * - CreateRequest → Entity
 * - Entity → Response
 * - UpdateRequest → Entity update
 *
 * Mapper에서는 비즈니스 로직을 처리하지 않는다.
 *
 * Runtime 연결 정보는 Service에서 검증한 후 Entity에 설정한다.
 */
@Component
public class EvidenceMapper {

    /**
     * Evidence 생성 Request를 Entity로 변환한다.
     *
     * project, requirement, wbs, submittedBy는
     * Service에서 조회하여 전달받는다.
     *
     * evidenceKey 역시 Service에서 생성하여 전달받는다.
     *
     * Runtime 연결 정보는 이 메서드에서 설정하지 않는다.
     * Runtime 파일 메타데이터 검증 후 Service에서 연결한다.
     *
     * Verification 상태는 신규 Evidence이므로
     * PENDING으로 초기화한다.
     */
    public Evidence toEntity(
            EvidenceCreateRequest request,
            Project project,
            EvidenceRequirement requirement,
            Wbs wbs,
            User submittedBy,
            String evidenceKey
    ) {
        LocalDateTime submittedAt =
                request.getSubmittedAt() != null
                        ? request.getSubmittedAt()
                        : LocalDateTime.now();

        return Evidence.builder()
                .project(project)
                .requirement(requirement)
                .wbs(wbs)
                .evidenceKey(evidenceKey)
                .evidenceType(request.getEvidenceType())
                .title(request.getTitle())
                .description(request.getDescription())
                .fileName(request.getFileName())
                .filePath(request.getFilePath())
                .submittedBy(submittedBy)
                .submittedAt(submittedAt)
                .verificationStatus(VerificationStatus.PENDING)
                .build();
    }

    /**
     * Evidence Entity를 Response DTO로 변환한다.
     *
     * Runtime 연결 정보도 응답에 포함한다.
     */
    public EvidenceResponse toResponse(Evidence entity) {
        return EvidenceResponse.builder()
                .id(entity.getId())
                .projectId(entity.getProject().getId())
                .requirementId(entity.getRequirement().getId())
                .wbsId(
                        entity.getWbs() != null
                                ? entity.getWbs().getId()
                                : null
                )
                .evidenceKey(entity.getEvidenceKey())
                .evidenceType(entity.getEvidenceType())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .fileName(entity.getFileName())
                .filePath(entity.getFilePath())
                .sourceFileId(entity.getSourceFileId())
                .executionUuid(entity.getExecutionUuid())
                .sourceJobId(entity.getSourceJobId())
                .submittedById(
                        entity.getSubmittedBy() != null
                                ? entity.getSubmittedBy().getId()
                                : null
                )
                .submittedByName(
                        entity.getSubmittedBy() != null
                                ? entity.getSubmittedBy().getName()
                                : null
                )
                .submittedAt(entity.getSubmittedAt())
                .verificationStatus(entity.getVerificationStatus())
                .verifiedById(
                        entity.getVerifiedBy() != null
                                ? entity.getVerifiedBy().getId()
                                : null
                )
                .verifiedByName(
                        entity.getVerifiedBy() != null
                                ? entity.getVerifiedBy().getName()
                                : null
                )
                .verifiedAt(entity.getVerifiedAt())
                .verificationRemark(entity.getVerificationRemark())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * Evidence 수정 Request를 Entity에 반영한다.
     *
     * Runtime 연결 정보와 Verification 관련 필드는 변경하지 않는다.
     *
     * Verification 상태 변경은 별도의 Verification API에서 처리한다.
     */
    public void updateEntity(
            Evidence entity,
            EvidenceUpdateRequest request,
            Wbs wbs
    ) {
        entity.update(
                wbs,
                request.getEvidenceType(),
                request.getTitle(),
                request.getDescription(),
                request.getFileName(),
                request.getFilePath()
        );
    }

    public EvidenceVersionResponse toVersionResponse(
            EvidenceVersion entity
    ) {
        return EvidenceVersionResponse.builder()
                .id(entity.getId())
                .evidenceId(entity.getEvidence().getId())
                .versionNumber(entity.getVersionNumber())
                .evidenceType(entity.getEvidenceType())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .fileName(entity.getFileName())
                .filePath(entity.getFilePath())
                .sourceFileId(entity.getSourceFileId())
                .executionUuid(entity.getExecutionUuid())
                .sourceJobId(entity.getSourceJobId())
                .submittedById(
                        entity.getSubmittedBy() == null
                                ? null
                                : entity.getSubmittedBy().getId()
                )
                .submittedAt(entity.getSubmittedAt())
                .verificationStatus(entity.getVerificationStatus())
                .verifiedById(
                        entity.getVerifiedBy() == null
                                ? null
                                : entity.getVerifiedBy().getId()
                )
                .verifiedAt(entity.getVerifiedAt())
                .verificationRemark(entity.getVerificationRemark())
                .build();
    }    
}