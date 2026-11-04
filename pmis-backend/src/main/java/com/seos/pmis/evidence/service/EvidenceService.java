package com.seos.pmis.evidence.service;

import com.seos.pmis.common.exception.BusinessException;
import com.seos.pmis.common.exception.code.CommonErrorCode;
import com.seos.pmis.common.search.SearchPageableFactory;
import com.seos.pmis.evidence.dto.request.EvidenceCreateRequest;
import com.seos.pmis.evidence.dto.request.EvidenceSearchRequest;
import com.seos.pmis.evidence.dto.request.EvidenceUpdateRequest;
import com.seos.pmis.evidence.dto.request.EvidenceVerificationRequest;
import com.seos.pmis.evidence.dto.request.EvidenceVersionCreateRequest;
import com.seos.pmis.evidence.dto.request.EvidenceVersionUploadRequest;
import com.seos.pmis.evidence.dto.response.EvidenceResponse;
import com.seos.pmis.evidence.dto.response.EvidenceVersionResponse;
import com.seos.pmis.evidence.dto.response.EvidenceVerificationHistoryResponse;
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
import com.seos.pmis.integration.runtime.dto.RuntimePmisFileResponse;
import com.seos.pmis.project.entity.Project;
import com.seos.pmis.project.repository.ProjectRepository;
import com.seos.pmis.user.entity.User;
import com.seos.pmis.user.repository.UserRepository;
import com.seos.pmis.wbs.entity.Wbs;
import com.seos.pmis.wbs.repository.WbsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.client.RestClientException;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EvidenceService {

    private static final long MAX_EVIDENCE_FILE_SIZE = 50L * 1024 * 1024;

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
                request.getDirection() != null
                        ? request.getDirection().name()
                        : null,
                "submittedAt",
                Sort.Direction.DESC,
                Set.of(
                        "id",
                        "evidenceKey",
                        "evidenceType",
                        "title",
                        "submittedAt",
                        "verificationStatus",
                        "createdAt",
                        "updatedAt"
                )
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
    // 버전 조회
    // =========================================================================

    /**
     * 특정 Evidence의 버전 목록을 최신 버전부터 조회한다.
     */
    public List<EvidenceVersionResponse> findAllVersions(Long evidenceId) {
        getEvidence(evidenceId);

        return evidenceVersionRepository
                .findAllByEvidenceIdOrderByVersionNumberDesc(evidenceId)
                .stream()
                .map(evidenceMapper::toVersionResponse)
                .toList();
    }

    /**
     * 특정 Evidence의 특정 버전을 조회한다.
     */
    public EvidenceVersionResponse findVersion(
            Long evidenceId,
            Integer versionNumber
    ) {
        getEvidence(evidenceId);

        if (versionNumber == null || versionNumber < 1) {
            throw new BusinessException(
                    EvidenceErrorCode.EVIDENCE_NOT_FOUND
            );
        }

        EvidenceVersion version = evidenceVersionRepository
                .findByEvidenceIdAndVersionNumber(
                        evidenceId,
                        versionNumber
                )
                .orElseThrow(() -> new BusinessException(
                        EvidenceErrorCode.EVIDENCE_NOT_FOUND
                ));

        return evidenceMapper.toVersionResponse(version);
    }

    /**
     * 특정 Evidence 버전의 Automation Runtime 파일 메타데이터 조회
     *
     * 일반 Evidence 파일 업로드로 저장된 sourceFileId를 사용한다.
     * 기존 Job output 파일 조회 API와는 별도로 처리한다.
     *
     * GET /api/evidence/{id}/versions/{versionNumber}/file/metadata
     */
    public RuntimePmisFileResponse findVersionFileMetadata(
            Long evidenceId,
            Integer versionNumber
    ) {
        // 1. Evidence 존재 여부 확인
        getEvidence(evidenceId);

        // 2. 버전 번호 검증
        if (versionNumber == null || versionNumber < 1) {
            throw new BusinessException(
                    EvidenceErrorCode.EVIDENCE_NOT_FOUND
            );
        }

        // 3. 해당 Evidence에 속한 버전 조회
        EvidenceVersion version = evidenceVersionRepository
                .findByEvidenceIdAndVersionNumber(
                        evidenceId,
                        versionNumber
                )
                .orElseThrow(() -> new BusinessException(
                        EvidenceErrorCode.EVIDENCE_NOT_FOUND
                ));

        // 4. Runtime 파일 ID 확인
        String sourceFileId = version.getSourceFileId();

        if (!hasText(sourceFileId)) {
            throw new BusinessException(
                    EvidenceErrorCode.RUNTIME_FILE_METADATA_INVALID
            );
        }

        // 5. Automation Runtime 일반 파일 메타데이터 조회
        try {
            RuntimePmisFileResponse runtimeFile =
                    runtimeApiClient.getPmisFileMetadata(sourceFileId);

            // 6. 응답 검증
            validatePmisFileMetadata(runtimeFile);

            if (!Objects.equals(
                    sourceFileId,
                    runtimeFile.getFileId()
            )) {
                throw new BusinessException(
                        EvidenceErrorCode.RUNTIME_FILE_METADATA_INVALID
                );
            }

            return runtimeFile;

        } catch (RestClientException exception) {
            throw new BusinessException(
                    EvidenceErrorCode.RUNTIME_API_UNAVAILABLE
            );
        }
    }
    /**
     * 특정 Evidence 버전의 파일 바이너리 다운로드
     *
     * 일반 PMIS 파일 API를 사용하며 기존 Job Output 파일 API와 분리한다.
     *
     * GET /api/evidence/{id}/versions/{versionNumber}/file
     */
    public byte[] downloadVersionFileContent(
            Long evidenceId,
            Integer versionNumber
    ) {
        // 1. Evidence 존재 여부 확인
        getEvidence(evidenceId);

        // 2. 버전 번호 검증
        if (versionNumber == null || versionNumber < 1) {
            throw new BusinessException(
                    EvidenceErrorCode.EVIDENCE_NOT_FOUND
            );
        }

        // 3. 해당 Evidence에 속한 버전 조회
        EvidenceVersion version = evidenceVersionRepository
                .findByEvidenceIdAndVersionNumber(
                        evidenceId,
                        versionNumber
                )
                .orElseThrow(() -> new BusinessException(
                        EvidenceErrorCode.EVIDENCE_NOT_FOUND
                ));

        // 4. Runtime 파일 ID 확인
        String sourceFileId = version.getSourceFileId();

        if (!hasText(sourceFileId)) {
            throw new BusinessException(
                    EvidenceErrorCode.RUNTIME_FILE_METADATA_INVALID
            );
        }

        // 5. Runtime 일반 파일 바이너리 다운로드
        try {
            byte[] fileContent =
                    runtimeApiClient.downloadPmisFileContent(sourceFileId);

            if (fileContent == null || fileContent.length == 0) {
                throw new BusinessException(
                        EvidenceErrorCode.RUNTIME_FILE_METADATA_INVALID
                );
            }

            return fileContent;

        } catch (RestClientException exception) {
            throw new BusinessException(
                    EvidenceErrorCode.RUNTIME_API_UNAVAILABLE
            );
        }
    }

    /**
     * 특정 Evidence 버전의 검증 이력을 최신순으로 조회한다.
     *
     * GET /api/evidence/{id}/versions/{versionNumber}/verification-history
     *
     * 검증 이력이 없으면 빈 목록을 반환한다.
     */
    public List<EvidenceVerificationHistoryResponse>
    findVersionVerificationHistory(
            Long evidenceId,
            Integer versionNumber
    ) {
        // Evidence 존재 여부 확인
        getEvidence(evidenceId);

        // 버전 번호 검증
        if (versionNumber == null || versionNumber < 1) {
            throw new BusinessException(
                    EvidenceErrorCode.EVIDENCE_NOT_FOUND
            );
        }

        // 해당 Evidence의 특정 버전 조회
        EvidenceVersion version = evidenceVersionRepository
                .findByEvidenceIdAndVersionNumber(
                        evidenceId,
                        versionNumber
                )
                .orElseThrow(() -> new BusinessException(
                        EvidenceErrorCode.EVIDENCE_NOT_FOUND
                ));

        // 해당 버전의 검증 이력을 최신순으로 반환
        return evidenceVerificationHistoryRepository
                .findAllByEvidenceVersionIdOrderByVerifiedAtDescIdDesc(
                        version.getId()
                )
                .stream()
                .map(evidenceMapper::toVerificationHistoryResponse)
                .toList();
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

            validateRuntimeFile(
                    runtimeFile,
                    request.getSourceFileId(),
                    request.getExecutionUuid(),
                    request.getSourceJobId()
            );
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

    /**
     * 기존 Evidence에 새 버전을 생성한다.
     *
     * Evidence의 논리 ID와 evidenceKey는 유지하고,
     * 버전별 제출 정보는 EvidenceVersion에 별도로 저장한다.
     */
    @Transactional
    public EvidenceVersionResponse createVersion(
            Long evidenceId,
            EvidenceVersionCreateRequest request
    ) {
        Evidence evidence = getEvidence(evidenceId);

        User submittedBy = getCurrentUser();

        RuntimeOutputFileResponse runtimeFile = null;

        if (hasText(request.getSourceFileId())) {
            runtimeFile = runtimeApiClient.getFileMetadata(
                    request.getSourceFileId()
            );

            validateRuntimeFile(
                    runtimeFile,
                    request.getSourceFileId(),
                    request.getExecutionUuid(),
                    request.getSourceJobId()
            );
        }

        int nextVersionNumber = evidenceVersionRepository
                .findTopByEvidenceIdOrderByVersionNumberDesc(evidenceId)
                .map(version -> version.getVersionNumber() + 1)
                .orElse(1);

        LocalDateTime submittedAt = LocalDateTime.now();

        EvidenceVersion version = EvidenceVersion.createNewVersion(
                evidence,
                nextVersionNumber,
                request.getEvidenceType(),
                request.getTitle(),
                request.getDescription(),
                request.getFileName(),
                request.getFilePath(),
                runtimeFile != null
                        ? runtimeFile.getFileId()
                        : request.getSourceFileId(),
                runtimeFile != null
                        ? runtimeFile.getExecutionUuid()
                        : request.getExecutionUuid(),
                runtimeFile != null
                        ? runtimeFile.getJobId()
                        : request.getSourceJobId(),
                submittedBy,
                submittedAt
        );

        EvidenceVersion savedVersion =
                evidenceVersionRepository.save(version);

        return evidenceMapper.toVersionResponse(savedVersion);
    }

    // =========================================================================
    // 일반 Evidence 파일 업로드
    // =========================================================================

    /**
     * 일반 Evidence 파일을 Automation Runtime에 업로드한 후
     * 반환된 file_id를 EvidenceVersion.sourceFileId에 저장한다.
     *
     * Runtime에는 바이너리 파일을 저장하고 PMIS DB에는 파일 참조와
     * 버전별 업무 메타데이터만 저장한다.
     */
    @Transactional
    public EvidenceVersionResponse uploadVersion(
            Long evidenceId,
            MultipartFile file,
            EvidenceVersionUploadRequest request
    ) {
        Evidence evidence = getEvidence(evidenceId);
        User submittedBy = getCurrentUser();

        validateEvidenceUploadFile(file);

        String originalFilename = file.getOriginalFilename();
        MediaType mediaType = resolveMediaType(file.getContentType());

        RuntimePmisFileResponse runtimeFile;

        try {
            runtimeFile = runtimeApiClient.uploadPmisFile(
                    originalFilename,
                    mediaType,
                    file.getBytes()
            );
        } catch (IOException | RestClientException exception) {
            throw new BusinessException(
                    EvidenceErrorCode.RUNTIME_API_UNAVAILABLE
            );
        }

        validatePmisFileMetadata(runtimeFile);

        int nextVersionNumber = evidenceVersionRepository
                .findTopByEvidenceIdOrderByVersionNumberDesc(evidenceId)
                .map(version -> version.getVersionNumber() + 1)
                .orElse(1);

        LocalDateTime submittedAt = LocalDateTime.now();

        EvidenceVersion version = EvidenceVersion.createNewVersion(
                evidence,
                nextVersionNumber,
                request.getEvidenceType(),
                request.getTitle(),
                request.getDescription(),
                runtimeFile.getOriginalFilename(),
                null,
                runtimeFile.getFileId(),
                null,
                null,
                submittedBy,
                submittedAt
        );

        EvidenceVersion savedVersion =
                evidenceVersionRepository.save(version);

        return evidenceMapper.toVersionResponse(savedVersion);
    }

    private void validateEvidenceUploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(
                    EvidenceErrorCode.EVIDENCE_FILE_EMPTY
            );
        }

        if (file.getSize() > MAX_EVIDENCE_FILE_SIZE) {
            throw new BusinessException(
                    EvidenceErrorCode.EVIDENCE_FILE_SIZE_EXCEEDED
            );
        }
    }

    private MediaType resolveMediaType(String contentType) {
        if (!hasText(contentType)) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }

        try {
            return MediaType.parseMediaType(contentType);
        } catch (IllegalArgumentException exception) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    private void validatePmisFileMetadata(
            RuntimePmisFileResponse runtimeFile
    ) {
        if (runtimeFile == null
                || !hasText(runtimeFile.getFileId())
                || !hasText(runtimeFile.getOriginalFilename())
                || !hasText(runtimeFile.getSha256())) {
            throw new BusinessException(
                    EvidenceErrorCode.RUNTIME_FILE_METADATA_INVALID
            );
        }
    }

    // =========================================================================
    // Runtime 파일 검증
    // =========================================================================

    private void validateRuntimeFile(
            RuntimeOutputFileResponse runtimeFile,
            EvidenceCreateRequest request
    ) {
        validateRuntimeFile(
                runtimeFile,
                request.getSourceFileId(),
                request.getExecutionUuid(),
                request.getSourceJobId()
        );
    }

    private void validateRuntimeFile(
            RuntimeOutputFileResponse runtimeFile,
            String sourceFileId,
            String executionUuid,
            String sourceJobId
    ) {
        if (runtimeFile == null
                || !hasText(runtimeFile.getFileId())
                || !hasText(runtimeFile.getExecutionUuid())
                || !hasText(runtimeFile.getJobId())) {
            throw new BusinessException(
                    EvidenceErrorCode.RUNTIME_FILE_METADATA_INVALID
            );
        }

        if (!Objects.equals(sourceFileId, runtimeFile.getFileId())) {
            throw new BusinessException(
                    EvidenceErrorCode.RUNTIME_FILE_ID_MISMATCH
            );
        }

        if (hasText(executionUuid)
                && !Objects.equals(
                        executionUuid,
                        runtimeFile.getExecutionUuid()
                )) {
            throw new BusinessException(
                    EvidenceErrorCode.RUNTIME_FILE_REFERENCE_MISMATCH
            );
        }

        if (hasText(sourceJobId)
                && !Objects.equals(sourceJobId, runtimeFile.getJobId())) {
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
         * 버전 및 검증 이력 보존 정책은 별도 결정이 필요하다.
         * 현재는 기존 삭제 동작을 유지한다.
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
         * 버전 데이터가 없는 기존 Evidence는 검증 시점에
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

    /**
     * 특정 Evidence Version 검증 처리
     *
     * 정책:
     * 1. 지정한 Evidence Version만 검증한다.
     * 2. Verification History는 지정한 Version에 연결한다.
     * 3. 지정한 Version이 최신 버전인 경우에만
     *    부모 Evidence의 대표 검증 상태를 갱신한다.
     * 4. 과거 버전 검증은 부모 Evidence에 영향을 주지 않는다.
     *
     * @param evidenceId Evidence ID
     * @param versionNumber 검증 대상 Version 번호
     * @param request 검증 요청
     * @return Evidence 응답
     */
    @Transactional
    public EvidenceResponse verifyVersion(
            Long evidenceId,
            Integer versionNumber,
            EvidenceVerificationRequest request
    ) {
        // 1. Version 번호 검증
        if (versionNumber == null || versionNumber < 1) {
             throw new BusinessException(
                     EvidenceErrorCode.EVIDENCE_VERSION_NUMBER_INVALID
             );
        }

        // 2. Evidence 조회
        Evidence evidence = getEvidence(evidenceId);

        // 3. 지정한 Evidence Version 조회
        EvidenceVersion version = evidenceVersionRepository
                .findByEvidenceIdAndVersionNumber(evidenceId, versionNumber)
                .orElseThrow(() -> new BusinessException(
                        EvidenceErrorCode.EVIDENCE_NOT_FOUND
                ));

        // 4. Verification 요청 검증
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

        // 5. 검증자 및 검증 일시
        User verifiedBy = getCurrentUser();
        LocalDateTime verifiedAt = LocalDateTime.now();

        // 6. 지정한 Version의 검증 상태 갱신
        version.verify(
                status,
                verifiedBy,
                verifiedAt,
                remark
        );

        // 7. 최신 Version 여부 확인
        EvidenceVersion latestVersion = evidenceVersionRepository
                .findTopByEvidenceIdOrderByVersionNumberDesc(evidenceId)
                .orElseThrow(() -> new BusinessException(
                         EvidenceErrorCode.EVIDENCE_NOT_FOUND
                ));

        boolean isLatestVersion =
                version.getVersionNumber().equals(
                       latestVersion.getVersionNumber()
                );

        // 8. 최신 Version인 경우에만 부모 Evidence 갱신
        if (isLatestVersion) {
             evidence.verify(
                    status,
                    verifiedBy,
                    verifiedAt,
                    remark
             );
        }

        // 9. 지정한 Version에 Verification History 생성
        EvidenceVerificationHistory history =
                EvidenceVerificationHistory.create(
                        version,
                        status,
                        verifiedBy,
                        verifiedAt,
                        remark
                );

        evidenceVerificationHistoryRepository.save(history);

        // 10. Evidence 응답 반환
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
