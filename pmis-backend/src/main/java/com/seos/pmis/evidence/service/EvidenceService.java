
package com.seos.pmis.evidence.service;

import com.seos.pmis.common.exception.BusinessException;
import com.seos.pmis.common.exception.code.CommonErrorCode;
import com.seos.pmis.common.util.SearchPageableFactory;
import com.seos.pmis.evidence.dto.request.EvidenceCreateRequest;
import com.seos.pmis.evidence.dto.request.EvidenceSearchRequest;
import com.seos.pmis.evidence.dto.request.EvidenceUpdateRequest;
import com.seos.pmis.evidence.dto.request.EvidenceVerificationRequest;
import com.seos.pmis.evidence.dto.response.EvidenceResponse;
import com.seos.pmis.evidence.entity.Evidence;
import com.seos.pmis.evidence.entity.EvidenceRequirement;
import com.seos.pmis.evidence.entity.EvidenceVersion;
import com.seos.pmis.evidence.entity.EvidenceVerificationHistory;
import com.seos.pmis.evidence.enums.VerificationStatus;
import com.seos.pmis.evidence.exception.code.EvidenceErrorCode;
import com.seos.pmis.evidence.mapper.EvidenceMapper;
import com.seos.pmis.evidence.repository.EvidenceRepository;
import com.seos.pmis.evidence.repository.EvidenceRequirementRepository;
import com.seos.pmis.evidence.repository.EvidenceVersionRepository;
import com.seos.pmis.evidence.repository.EvidenceVerificationHistoryRepository;
import com.seos.pmis.evidence.repository.InspectionItemRepository;
import com.seos.pmis.evidence.specification.EvidenceSpecification;
import com.seos.pmis.integration.runtime.client.RuntimeApiClient;
import com.seos.pmis.integration.runtime.dto.RuntimeOutputFileResponse;
import com.seos.pmis.project.entity.Project;
import com.seos.pmis.project.repository.ProjectRepository;
import com.seos.pmis.user.entity.User;
import com.seos.pmis.user.repository.UserRepository;
import com.seos.pmis.wbs.entity.Wbs;
import com.seos.pmis.wbs.repository.WbsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EvidenceService {

    private final EvidenceRepository evidenceRepository;
    private final EvidenceRequirementRepository evidenceRequirementRepository;
    private final EvidenceVersionRepository evidenceVersionRepository;
    private final EvidenceVerificationHistoryRepository
            evidenceVerificationHistoryRepository;
    private final InspectionItemRepository inspectionItemRepository;

    private final ProjectRepository projectRepository;
    private final WbsRepository wbsRepository;
    private final UserRepository userRepository;

    private final EvidenceMapper evidenceMapper;
    private final EvidenceRequirementService evidenceRequirementService;
    private final RuntimeApiClient runtimeApiClient;

    // =========================================================================
    // 조회
    // =========================================================================

    public List<EvidenceResponse> findAllByProjectId(Long projectId) {
        validateProjectExists(projectId);

        return evidenceRepository.findAllByProjectId(projectId)
                .stream()
                .map(evidenceMapper::toResponse)
                .toList();
    }

    public List<EvidenceResponse> findAllByRequirementId(Long requirementId) {
        EvidenceRequirement requirement = getRequirement(requirementId);

        return evidenceRepository.findAllByRequirementId(requirement.getId())
                .stream()
                .map(evidenceMapper::toResponse)
                .toList();
    }

    public List<EvidenceResponse> findAllByProjectIdAndWbsId(
            Long projectId,
            Long wbsId
    ) {
        validateProjectExists(projectId);

        Wbs wbs = getWbs(wbsId);
        validateWbsBelongsToProject(wbs, projectId);

        return evidenceRepository
                .findAllByProjectIdAndWbsId(projectId, wbsId)
                .stream()
                .map(evidenceMapper::toResponse)
                .toList();
    }

    public EvidenceResponse findById(Long id) {
        return evidenceMapper.toResponse(getEvidence(id));
    }

    public Page<EvidenceResponse> search(EvidenceSearchRequest request) {
        Pageable pageable = SearchPageableFactory.create(
                request.getPage(),
                request.getSize(),
                request.getSortBy(),
                request.getDirection()
        );

        var specification = EvidenceSpecification
                .projectId(request.getProjectId())
                .and(EvidenceSpecification.requirementId(
                        request.getRequirementId()
                ))
                .and(EvidenceSpecification.wbsId(request.getWbsId()))
                .and(EvidenceSpecification.keyword(request.getKeyword()))
                .and(EvidenceSpecification.evidenceType(
                        request.getEvidenceType()
                ))
                .and(EvidenceSpecification.verificationStatus(
                        request.getVerificationStatus()
                ));

        return evidenceRepository.findAll(specification, pageable)
                .map(evidenceMapper::toResponse);
    }

    // =========================================================================
    // 생성
    // =========================================================================

    /**
     * Evidence 생성과 최초 버전 생성은 동일 트랜잭션에서 처리한다.
     */
    @Transactional
    public EvidenceResponse create(
            Long requirementId,
            EvidenceCreateRequest request
    ) {
        EvidenceRequirement requirement = getRequirement(requirementId);
        Project project = requirement.getProject();

        Wbs wbs = getOptionalWbs(request.getWbsId());

        if (wbs != null) {
            validateWbsBelongsToProject(wbs, project.getId());
        }

        User submittedBy = getCurrentUser();

        RuntimeOutputFileResponse runtimeFile = null;

        if (hasText(request.getSourceFileId())) {
            runtimeFile = runtimeApiClient.getFileMetadata(
                    request.getSourceFileId()
            );

            validateRuntimeFile(runtimeFile, request);
        }

        String evidenceKey = generateEvidenceKey(project.getId());

        Evidence evidence = evidenceMapper.toEntity(
                request,
                project,
                requirement,
                wbs,
                submittedBy,
                evidenceKey
        );

        if (runtimeFile != null) {
            evidence.linkRuntimeFile(
                    runtimeFile.getFileId(),
                    runtimeFile.getExecutionUuid(),
                    runtimeFile.getJobId()
            );
        }

        Evidence saved = evidenceRepository.save(evidence);

        EvidenceVersion initialVersion = EvidenceVersion.createInitial(
                saved,
                submittedBy,
                saved.getSubmittedAt()
        );

        evidenceVersionRepository.save(initialVersion);

        evidenceRequirementService.refreshStatus(requirement.getId());

        return evidenceMapper.toResponse(saved);
    }

    private void validateRuntimeFile(
            RuntimeOutputFileResponse runtimeFile,
            EvidenceCreateRequest request
    ) {
        if (runtimeFile == null
                || !hasText(runtimeFile.getFileId())
                || !hasText(runtimeFile.getExecutionUuid())
                || !hasText(runtimeFile.getJobId())) {
            throw new BusinessException(
                    EvidenceErrorCode.RUNTIME_FILE_METADATA_INVALID
            );
        }

        if (!Objects.equals(
                request.getSourceFileId(),
                runtimeFile.getFileId()
        )) {
            throw new BusinessException(
                    EvidenceErrorCode.RUNTIME_FILE_ID_MISMATCH
            );
        }

        if (hasText(request.getExecutionUuid())
                && !Objects.equals(
                        request.getExecutionUuid(),
                        runtimeFile.getExecutionUuid()
                )) {
            throw new BusinessException(
                    EvidenceErrorCode.RUNTIME_FILE_REFERENCE_MISMATCH
            );
        }

        if (hasText(request.getSourceJobId())
                && !Objects.equals(
                        request.getSourceJobId(),
                        runtimeFile.getJobId()
                )) {
            throw new BusinessException(
                    EvidenceErrorCode.RUNTIME_FILE_REFERENCE_MISMATCH
            );
        }
    }

    // =========================================================================
    // 수정
    // =========================================================================

    @Transactional
    public EvidenceResponse update(
            Long id,
            EvidenceUpdateRequest request
    ) {
        Evidence evidence = getEvidence(id);

        Wbs wbs = getOptionalWbs(request.getWbsId());

        if (wbs != null) {
            validateWbsBelongsToProject(
                    wbs,
                    evidence.getProject().getId()
            );
        }

        evidenceMapper.updateEntity(evidence, request, wbs);

        return evidenceMapper.toResponse(evidence);
    }

    // =========================================================================
    // 삭제
    // =========================================================================

    @Transactional
    public void delete(Long id) {
        Evidence evidence = getEvidence(id);

        if (inspectionItemRepository.existsByEvidenceId(evidence.getId())) {
            throw new BusinessException(
                    EvidenceErrorCode.EVIDENCE_DELETE_NOT_ALLOWED
            );
        }

        /*
         * 버전 및 검증 이력이 존재하는 경우 삭제 정책을 별도로
         * 결정해야 한다. 현재는 기존 삭제 동작을 유지한다.
         */
        Long requirementId = evidence.getRequirement().getId();

        evidenceRepository.delete(evidence);

        evidenceRequirementService.refreshStatus(requirementId);
    }

    // =========================================================================
    // Verification
    // =========================================================================

    /**
     * 기존 Evidence 검증 API를 유지하면서 현재 버전과
     * 검증 이력을 함께 갱신한다.
     */
    @Transactional
    public EvidenceResponse verify(
            Long id,
            EvidenceVerificationRequest request
    ) {
        Evidence evidence = getEvidence(id);

        VerificationStatus status = request.getStatus();
        String remark = request.getRemark();

        if (status == null || status == VerificationStatus.PENDING) {
            throw new BusinessException(
                    EvidenceErrorCode.EVIDENCE_VERIFICATION_NOT_ALLOWED
            );
        }

        if (status == VerificationStatus.REJECTED
                && !hasText(remark)) {
            throw new BusinessException(
                    EvidenceErrorCode.EVIDENCE_VERIFICATION_REMARK_REQUIRED
            );
        }

        /*
         * 아직 버전 데이터가 없는 기존 Evidence는 검증 시점에
         * 버전 1을 생성해 기존 데이터를 보존한다.
         */
        EvidenceVersion version = evidenceVersionRepository
                .findTopByEvidenceIdOrderByVersionNumberDesc(evidence.getId())
                .orElseGet(() -> evidenceVersionRepository.save(
                        EvidenceVersion.createInitial(
                                evidence,
                                evidence.getSubmittedBy(),
                                evidence.getSubmittedAt()
                        )
                ));

        User verifiedBy = getCurrentUser();
        LocalDateTime verifiedAt = LocalDateTime.now();

        version.verify(status, verifiedBy, verifiedAt, remark);

        evidence.verify(status, verifiedBy, verifiedAt, remark);

        EvidenceVerificationHistory history =
                EvidenceVerificationHistory.create(
                        version,
                        status,
                        verifiedBy,
                        verifiedAt,
                        remark
                );

        evidenceVerificationHistoryRepository.save(history);

        return evidenceMapper.toResponse(evidence);
    }

    // =========================================================================
    // 내부 조회 및 검증
    // =========================================================================

    private Evidence getEvidence(Long id) {
        return evidenceRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        EvidenceErrorCode.EVIDENCE_NOT_FOUND
                ));
    }

    private EvidenceRequirement getRequirement(Long requirementId) {
        return evidenceRequirementRepository.findById(requirementId)
                .orElseThrow(() -> new BusinessException(
                        EvidenceErrorCode.EVIDENCE_REQUIREMENT_NOT_FOUND
                ));
    }

    private void validateProjectExists(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new BusinessException(
                    EvidenceErrorCode.PROJECT_NOT_FOUND
            );
        }
    }

    private Wbs getWbs(Long wbsId) {
        return wbsRepository.findById(wbsId)
                .orElseThrow(() -> new BusinessException(
                        EvidenceErrorCode.WBS_NOT_FOUND
                ));
    }

    private Wbs getOptionalWbs(Long wbsId) {
        return wbsId == null ? null : getWbs(wbsId);
    }

    private void validateWbsBelongsToProject(
            Wbs wbs,
            Long projectId
    ) {
        if (!wbs.getProject().getId().equals(projectId)) {
            throw new BusinessException(
                    EvidenceErrorCode.WBS_PROJECT_MISMATCH
            );
        }
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || !hasText(authentication.getName())) {
            throw new BusinessException(
                    CommonErrorCode.UNAUTHORIZED
            );
        }

        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new BusinessException(
                        CommonErrorCode.USER_NOT_FOUND
                ));
    }

    // =========================================================================
    // Evidence Key 생성
    // =========================================================================

    private String generateEvidenceKey(Long projectId) {
        List<Evidence> evidences =
                evidenceRepository.findAllByProjectId(projectId);

        int nextNumber = evidences.stream()
                .map(Evidence::getEvidenceKey)
                .filter(Objects::nonNull)
                .filter(key -> key.startsWith("EVD-"))
                .map(key -> key.substring(4))
                .mapToInt(this::parseKeyNumber)
                .max()
                .orElse(0) + 1;

        return String.format("EVD-%03d", nextNumber);
    }

    private int parseKeyNumber(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            return 0;
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}