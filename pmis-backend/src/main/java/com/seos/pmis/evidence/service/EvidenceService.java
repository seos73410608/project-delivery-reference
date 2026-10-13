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
import com.seos.pmis.evidence.enums.VerificationStatus;
import com.seos.pmis.evidence.exception.code.EvidenceErrorCode;
import com.seos.pmis.evidence.mapper.EvidenceMapper;
import com.seos.pmis.evidence.repository.EvidenceRepository;
import com.seos.pmis.evidence.repository.EvidenceRequirementRepository;
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
    private final InspectionItemRepository inspectionItemRepository;

    private final ProjectRepository projectRepository;
    private final WbsRepository wbsRepository;
    private final UserRepository userRepository;

    private final EvidenceMapper evidenceMapper;
    private final EvidenceRequirementService evidenceRequirementService;

    /**
     * Automation Runtime API 클라이언트.
     */
    private final RuntimeApiClient runtimeApiClient;

    // =========================================================================
    // 조회
    // =========================================================================

    /**
     * Project 기준 Evidence 목록 조회
     */
    public List<EvidenceResponse> findAllByProjectId(Long projectId) {
        validateProjectExists(projectId);

        return evidenceRepository.findAllByProjectId(projectId)
                .stream()
                .map(evidenceMapper::toResponse)
                .toList();
    }

    /**
     * Evidence Requirement 기준 Evidence 목록 조회
     */
    public List<EvidenceResponse> findAllByRequirementId(Long requirementId) {
        EvidenceRequirement requirement = getRequirement(requirementId);

        return evidenceRepository.findAllByRequirementId(requirement.getId())
                .stream()
                .map(evidenceMapper::toResponse)
                .toList();
    }

    /**
     * Project + WBS 기준 Evidence 목록 조회
     */
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

    /**
     * Evidence 단건 조회
     */
    public EvidenceResponse findById(Long id) {
        return evidenceMapper.toResponse(getEvidence(id));
    }

    /**
     * Evidence 검색
     */
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
                .and(EvidenceSpecification.wbsId(
                        request.getWbsId()
                ))
                .and(EvidenceSpecification.keyword(
                        request.getKeyword()
                ))
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
     * Evidence 생성
     *
     * sourceFileId가 전달되면 Runtime 메타데이터를 조회하고
     * 검증된 Runtime 응답을 통해 lineage를 연결한다.
     *
     * sourceFileId가 없으면 기존 수동 등록 방식을 유지한다.
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

        /*
         * Runtime 식별자는 요청 DTO를 수정하지 않고,
         * 검증된 Runtime 응답을 기준으로 엔티티에 연결한다.
         */
        if (runtimeFile != null) {
            evidence.linkRuntimeFile(
                    runtimeFile.getFileId(),
                    runtimeFile.getExecutionUuid(),
                    runtimeFile.getJobId()
            );
        }

        Evidence saved = evidenceRepository.save(evidence);

        evidenceRequirementService.refreshStatus(requirement.getId());

        return evidenceMapper.toResponse(saved);
    }

    /**
     * Runtime 메타데이터 검증
     */
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

        // 요청한 파일 ID와 Runtime 응답의 파일 ID를 비교한다.
        if (!Objects.equals(
                request.getSourceFileId(),
                runtimeFile.getFileId()
        )) {
            throw new BusinessException(
                    EvidenceErrorCode.RUNTIME_FILE_ID_MISMATCH
            );
        }

        // 실행 UUID가 요청에 포함된 경우에만 비교한다.
        if (hasText(request.getExecutionUuid())
                && !Objects.equals(
                        request.getExecutionUuid(),
                        runtimeFile.getExecutionUuid()
                )) {
            throw new BusinessException(
                    EvidenceErrorCode.RUNTIME_FILE_REFERENCE_MISMATCH
            );
        }

        // Job ID가 요청에 포함된 경우에만 비교한다.
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

    /**
     * Evidence 수정
     *
     * 일반 수정 요청으로 Runtime lineage를 변경하지 않는다.
     */
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

    /**
     * Inspection Item에서 사용 중인 Evidence는 삭제할 수 없다.
     */
    @Transactional
    public void delete(Long id) {
        Evidence evidence = getEvidence(id);

        if (inspectionItemRepository.existsByEvidenceId(evidence.getId())) {
            throw new BusinessException(
                    EvidenceErrorCode.EVIDENCE_DELETE_NOT_ALLOWED
            );
        }

        Long requirementId = evidence.getRequirement().getId();

        evidenceRepository.delete(evidence);

        evidenceRequirementService.refreshStatus(requirementId);
    }

    // =========================================================================
    // Verification
    // =========================================================================

    /**
     * Evidence 검증
     *
     * 허용 결과: APPROVED, REJECTED
     * REJECTED인 경우 반려 사유가 필수다.
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

        User verifiedBy = getCurrentUser();

        evidence.verify(
                status,
                verifiedBy,
                LocalDateTime.now(),
                remark
        );

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