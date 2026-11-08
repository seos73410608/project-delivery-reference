
package com.seos.pmis.evidence.service;

import com.seos.common.exception.BusinessException;
import com.seos.common.exception.code.CommonErrorCode;
import com.seos.common.search.SearchPageableFactory;
import com.seos.pmis.evidence.dto.request.EvidenceCreateRequest;
import com.seos.pmis.evidence.dto.request.EvidenceSearchRequest;
import com.seos.pmis.evidence.dto.request.EvidenceUpdateRequest;
import com.seos.pmis.evidence.dto.request.EvidenceVerificationRequest;
import com.seos.pmis.evidence.dto.request.EvidenceVersionCreateRequest;
import com.seos.pmis.evidence.dto.request.EvidenceVersionDeleteRequest;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EvidenceService {

        private static final long MAX_EVIDENCE_FILE_SIZE = 50L * 1024 * 1024;

        private static final String ROLE_ADMIN = "ROLE_ADMIN";
        private static final String ROLE_PM = "ROLE_PM";

        private final EvidenceRepository evidenceRepository;
        private final EvidenceRequirementRepository evidenceRequirementRepository;
        private final EvidenceVersionRepository evidenceVersionRepository;
        private final EvidenceVerificationHistoryRepository evidenceVerificationHistoryRepository;
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

        public List<EvidenceResponse> findAllByRequirementId(
                        Long requirementId) {
                EvidenceRequirement requirement = getRequirement(requirementId);

                return evidenceRepository
                                .findAllByRequirementId(requirement.getId())
                                .stream()
                                .map(evidenceMapper::toResponse)
                                .toList();
        }

        public List<EvidenceResponse> findAllByProjectIdAndWbsId(
                        Long projectId,
                        Long wbsId) {
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
                                                "updatedAt"));

                var specification = EvidenceSpecification
                                .projectId(request.getProjectId())
                                .and(EvidenceSpecification.requirementId(
                                                request.getRequirementId()))
                                .and(EvidenceSpecification.wbsId(request.getWbsId()))
                                .and(EvidenceSpecification.keyword(request.getKeyword()))
                                .and(EvidenceSpecification.evidenceType(
                                                request.getEvidenceType()))
                                .and(EvidenceSpecification.verificationStatus(
                                                request.getVerificationStatus()));

                return evidenceRepository.findAll(specification, pageable)
                                .map(evidenceMapper::toResponse);
        }

        // =========================================================================
        // 버전 조회
        // =========================================================================

        /**
         * 삭제되지 않은 Evidence 버전 목록을 최신순으로 조회한다.
         */
        public List<EvidenceVersionResponse> findAllVersions(
                        Long evidenceId) {
                getEvidence(evidenceId);

                return evidenceVersionRepository
                                .findAllByEvidenceIdAndDeletedFalseOrderByVersionNumberDesc(
                                                evidenceId)
                                .stream()
                                .map(evidenceMapper::toVersionResponse)
                                .toList();
        }

        /**
         * 삭제되지 않은 특정 Evidence 버전을 조회한다.
         */
        public EvidenceVersionResponse findVersion(
                        Long evidenceId,
                        Integer versionNumber) {
                getEvidence(evidenceId);
                validateVersionNumber(versionNumber);

                EvidenceVersion version = getActiveVersion(
                                evidenceId,
                                versionNumber);

                return evidenceMapper.toVersionResponse(version);
        }

        /**
         * 특정 Evidence 버전의 Automation Runtime 파일 메타데이터 조회
         */
        public RuntimePmisFileResponse findVersionFileMetadata(
                        Long evidenceId,
                        Integer versionNumber) {
                getEvidence(evidenceId);
                validateVersionNumber(versionNumber);

                EvidenceVersion version = getActiveVersion(
                                evidenceId,
                                versionNumber);

                String sourceFileId = version.getSourceFileId();

                if (!hasText(sourceFileId)) {
                        throw new BusinessException(
                                        EvidenceErrorCode.RUNTIME_FILE_METADATA_INVALID);
                }

                try {
                        RuntimePmisFileResponse runtimeFile = runtimeApiClient.getPmisFileMetadata(sourceFileId);

                        validatePmisFileMetadata(runtimeFile);

                        if (!Objects.equals(
                                        sourceFileId,
                                        runtimeFile.getFileId())) {
                                throw new BusinessException(
                                                EvidenceErrorCode.RUNTIME_FILE_METADATA_INVALID);
                        }

                        return runtimeFile;

                } catch (RestClientException exception) {
                        throw new BusinessException(
                                        EvidenceErrorCode.RUNTIME_API_UNAVAILABLE);
                }
        }

        /**
         * 특정 Evidence 버전의 파일 바이너리 다운로드
         */
        public byte[] downloadVersionFileContent(
                        Long evidenceId,
                        Integer versionNumber) {
                getEvidence(evidenceId);
                validateVersionNumber(versionNumber);

                EvidenceVersion version = getActiveVersion(
                                evidenceId,
                                versionNumber);

                String sourceFileId = version.getSourceFileId();

                if (!hasText(sourceFileId)) {
                        throw new BusinessException(
                                        EvidenceErrorCode.RUNTIME_FILE_METADATA_INVALID);
                }

                try {
                        byte[] fileContent = runtimeApiClient.downloadPmisFileContent(sourceFileId);

                        if (fileContent == null || fileContent.length == 0) {
                                throw new BusinessException(
                                                EvidenceErrorCode.RUNTIME_FILE_METADATA_INVALID);
                        }

                        return fileContent;

                } catch (RestClientException exception) {
                        throw new BusinessException(
                                        EvidenceErrorCode.RUNTIME_API_UNAVAILABLE);
                }
        }

        /**
         * 특정 Evidence 버전의 검증 이력을 최신순으로 조회한다.
         *
         * 삭제된 버전의 검증 이력은 일반 조회 API에서 노출하지 않는다.
         */
        public List<EvidenceVerificationHistoryResponse> findVersionVerificationHistory(
                        Long evidenceId,
                        Integer versionNumber) {
                getEvidence(evidenceId);
                validateVersionNumber(versionNumber);

                EvidenceVersion version = getActiveVersion(
                                evidenceId,
                                versionNumber);

                return evidenceVerificationHistoryRepository
                                .findAllByEvidenceVersionIdOrderByVerifiedAtDescIdDesc(
                                                version.getId())
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
                        EvidenceCreateRequest request) {
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
                                        request.getSourceFileId());

                        validateRuntimeFile(
                                        runtimeFile,
                                        request.getSourceFileId(),
                                        request.getExecutionUuid(),
                                        request.getSourceJobId());
                }

                String evidenceKey = generateEvidenceKey(project.getId());

                Evidence evidence = evidenceMapper.toEntity(
                                request,
                                project,
                                requirement,
                                wbs,
                                submittedBy,
                                evidenceKey);

                if (runtimeFile != null) {
                        evidence.linkRuntimeFile(
                                        runtimeFile.getFileId(),
                                        runtimeFile.getExecutionUuid(),
                                        runtimeFile.getJobId());
                }

                Evidence saved = evidenceRepository.save(evidence);

                EvidenceVersion initialVersion = EvidenceVersion.createInitial(
                                saved,
                                submittedBy,
                                saved.getSubmittedAt());

                evidenceVersionRepository.save(initialVersion);

                evidenceRequirementService.refreshStatus(requirement.getId());

                return evidenceMapper.toResponse(saved);
        }

        /**
         * 기존 Evidence에 새 버전을 생성한다.
         *
         * 버전 번호는 삭제된 버전을 포함한 전체 이력을 기준으로 계산한다.
         */
        @Transactional
        public EvidenceVersionResponse createVersion(
                        Long evidenceId,
                        EvidenceVersionCreateRequest request) {

                // 존재 여부를 먼저 확인한다.
                Evidence evidence = getEvidence(evidenceId);

                User submittedBy = getCurrentUser();

                RuntimeOutputFileResponse runtimeFile = null;

                if (hasText(request.getSourceFileId())) {
                        runtimeFile = runtimeApiClient.getFileMetadata(
                                        request.getSourceFileId());

                        validateRuntimeFile(
                                        runtimeFile,
                                        request.getSourceFileId(),
                                        request.getExecutionUuid(),
                                        request.getSourceJobId());
                }

                // 버전 상태를 변경하기 전에 부모 Evidence 행을 잠근다.
                evidence = getEvidenceForUpdate(evidenceId);

                // 잠금 획득 후 활성 버전 존재 여부를 확인한다.
                validateActiveVersionsExist(evidenceId);

                // 잠금 내부에서 다음 버전 번호를 계산한다.
                int nextVersionNumber = getNextVersionNumber(evidenceId);

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
                                submittedAt);

                EvidenceVersion savedVersion = evidenceVersionRepository.save(version);

                evidence.synchronizeLatestVersion(savedVersion);

                return evidenceMapper.toVersionResponse(savedVersion);
        }

        // =========================================================================
        // 일반 Evidence 파일 업로드
        // =========================================================================

        /**
         * 일반 Evidence 파일을 Automation Runtime에 업로드한 후
         * 반환된 file_id를 EvidenceVersion.sourceFileId에 저장한다.
         *
         * Runtime 파일은 PMIS에서 물리 삭제하지 않는다.
         */
        @Transactional
        public EvidenceVersionResponse uploadVersion(
                        Long evidenceId,
                        MultipartFile file,
                        EvidenceVersionUploadRequest request) {

                // Evidence 존재 여부 확인
                getEvidence(evidenceId);

                User submittedBy = getCurrentUser();

                validateEvidenceUploadFile(file);

                String originalFilename = file.getOriginalFilename();
                MediaType mediaType = resolveMediaType(file.getContentType());

                RuntimePmisFileResponse runtimeFile;

                try {
                        runtimeFile = runtimeApiClient.uploadPmisFile(
                                        originalFilename,
                                        mediaType,
                                        file.getBytes());
                } catch (IOException | RestClientException exception) {
                        log.error(
                                        "Runtime 파일 업로드 실패. evidenceId={}, fileName={}",
                                        evidenceId,
                                        originalFilename,
                                        exception);

                        throw new BusinessException(
                                        EvidenceErrorCode.RUNTIME_API_UNAVAILABLE);
                }

                validatePmisFileMetadata(runtimeFile);

                // Runtime 업로드 이후 DB 변경 직전에 잠금 획득
                Evidence evidence = getEvidenceForUpdate(evidenceId);

                // 잠금 획득 후 활성 버전 존재 여부 재확인
                validateActiveVersionsExist(evidenceId);

                int nextVersionNumber = getNextVersionNumber(evidenceId);

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
                                submittedAt);

                EvidenceVersion savedVersion = evidenceVersionRepository.save(version);

                evidence.synchronizeLatestVersion(savedVersion);

                return evidenceMapper.toVersionResponse(savedVersion);
        }

        private void validateEvidenceUploadFile(MultipartFile file) {
                if (file == null || file.isEmpty()) {
                        throw new BusinessException(
                                        EvidenceErrorCode.EVIDENCE_FILE_EMPTY);
                }

                if (file.getSize() > MAX_EVIDENCE_FILE_SIZE) {
                        throw new BusinessException(
                                        EvidenceErrorCode.EVIDENCE_FILE_SIZE_EXCEEDED);
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
                        RuntimePmisFileResponse runtimeFile) {
                if (runtimeFile == null
                                || !hasText(runtimeFile.getFileId())
                                || !hasText(runtimeFile.getOriginalFilename())
                                || !hasText(runtimeFile.getSha256())) {
                        throw new BusinessException(
                                        EvidenceErrorCode.RUNTIME_FILE_METADATA_INVALID);
                }
        }

        // =========================================================================
        // Runtime 파일 검증
        // =========================================================================

        private void validateRuntimeFile(
                        RuntimeOutputFileResponse runtimeFile,
                        EvidenceCreateRequest request) {
                validateRuntimeFile(
                                runtimeFile,
                                request.getSourceFileId(),
                                request.getExecutionUuid(),
                                request.getSourceJobId());
        }

        private void validateRuntimeFile(
                        RuntimeOutputFileResponse runtimeFile,
                        String sourceFileId,
                        String executionUuid,
                        String sourceJobId) {
                if (runtimeFile == null
                                || !hasText(runtimeFile.getFileId())
                                || !hasText(runtimeFile.getExecutionUuid())
                                || !hasText(runtimeFile.getJobId())) {
                        throw new BusinessException(
                                        EvidenceErrorCode.RUNTIME_FILE_METADATA_INVALID);
                }

                if (!Objects.equals(sourceFileId, runtimeFile.getFileId())) {
                        throw new BusinessException(
                                        EvidenceErrorCode.RUNTIME_FILE_ID_MISMATCH);
                }

                if (hasText(executionUuid)
                                && !Objects.equals(
                                                executionUuid,
                                                runtimeFile.getExecutionUuid())) {
                        throw new BusinessException(
                                        EvidenceErrorCode.RUNTIME_FILE_REFERENCE_MISMATCH);
                }

                if (hasText(sourceJobId)
                                && !Objects.equals(
                                                sourceJobId,
                                                runtimeFile.getJobId())) {
                        throw new BusinessException(
                                        EvidenceErrorCode.RUNTIME_FILE_REFERENCE_MISMATCH);
                }
        }

        // =========================================================================
        // 수정
        // =========================================================================

        @Transactional
        public EvidenceResponse update(
                        Long id,
                        EvidenceUpdateRequest request) {
                Evidence evidence = getEvidence(id);

                Wbs wbs = getOptionalWbs(request.getWbsId());

                if (wbs != null) {
                        validateWbsBelongsToProject(
                                        wbs,
                                        evidence.getProject().getId());
                }

                evidenceMapper.updateEntity(evidence, wbs);

                return evidenceMapper.toResponse(evidence);
        }

        // =========================================================================
        // 삭제
        // =========================================================================

        /**
         * Evidence 버전을 논리 삭제한다.
         *
         * PENDING / REJECTED:
         * - 제출자 또는 PM 이상
         *
         * VERIFIED:
         * - ADMIN만 가능
         *
         * 공통:
         * - 삭제 사유 필수
         * - 마지막 활성 버전 삭제 금지
         * - 최신 활성 버전 삭제 시 직전 활성 버전 복원
         * - 검증 이력 및 Runtime 파일 보존
         */
        @Transactional
        public void deleteVersion(
                        Long evidenceId,
                        Integer versionNumber,
                        EvidenceVersionDeleteRequest request) {

                validateVersionNumber(versionNumber);

                if (request == null || !hasText(request.getReason())) {
                        throw new BusinessException(
                                        EvidenceErrorCode.EVIDENCE_VERSION_DELETION_REASON_REQUIRED);
                }

                // 삭제 작업 시작 시 부모 Evidence 행 잠금
                Evidence evidence = getEvidenceForUpdate(evidenceId);

                EvidenceVersion version = getActiveVersion(
                                evidenceId,
                                versionNumber);

                User currentUser = getCurrentUser();

                validateVersionDeletePermission(version, currentUser);

                List<EvidenceVersion> activeVersions = evidenceVersionRepository
                                .findAllByEvidenceIdAndDeletedFalseOrderByVersionNumberDesc(
                                                evidenceId);

                // 마지막 활성 버전은 삭제할 수 없다.
                if (activeVersions.size() <= 1) {
                        throw new BusinessException(
                                        EvidenceErrorCode.EVIDENCE_LAST_ACTIVE_VERSION_DELETE_NOT_ALLOWED);
                }

                EvidenceVersion latestVersion = activeVersions.get(0);

                boolean deletingLatestVersion = latestVersion.getId().equals(version.getId());

                EvidenceVersion previousVersion = null;

                if (deletingLatestVersion) {
                        previousVersion = activeVersions.stream()
                                        .filter(active -> !active.getId().equals(version.getId()))
                                        .findFirst()
                                        .orElseThrow(() -> new BusinessException(
                                                        EvidenceErrorCode.EVIDENCE_LAST_ACTIVE_VERSION_DELETE_NOT_ALLOWED));
                }

                // DB 논리 삭제만 수행한다. Runtime 파일은 삭제하지 않는다.
                version.softDelete(
                                currentUser,
                                LocalDateTime.now(),
                                request.getReason());

                // 최신 버전 삭제 시 직전 활성 버전의 정보를 복원한다.
                if (deletingLatestVersion) {
                        evidence.restoreFromVersion(previousVersion);
                }

                evidenceRequirementService.refreshStatus(
                                evidence.getRequirement().getId());
        }

        /**
         * 기존 Evidence 전체 삭제.
         *
         * 기존 정책을 유지하며 이번 버전 논리 삭제 정책과는 별개다.
         */
        @Transactional
        public void delete(Long id) {
                Evidence evidence = getEvidenceForUpdate(id);

                if (inspectionItemRepository.existsByEvidenceId(evidence.getId())) {
                        throw new BusinessException(
                                        EvidenceErrorCode.EVIDENCE_DELETE_NOT_ALLOWED);
                }

                Long requirementId = evidence.getRequirement().getId();

                evidenceRepository.delete(evidence);

                evidenceRequirementService.refreshStatus(requirementId);
        }

        /**
         * 버전 상태별 삭제 권한을 검증한다.
         */
        private void validateVersionDeletePermission(
                        EvidenceVersion version,
                        User currentUser) {
                Authentication authentication = SecurityContextHolder
                                .getContext()
                                .getAuthentication();

                if (authentication == null
                                || !authentication.isAuthenticated()) {
                        throw new BusinessException(
                                        CommonErrorCode.UNAUTHORIZED);
                }

                boolean isAdmin = hasAuthority(
                                authentication,
                                ROLE_ADMIN);

                boolean isPm = hasAuthority(
                                authentication,
                                ROLE_PM);

                VerificationStatus status = version.getVerificationStatus();

                if (status == VerificationStatus.APPROVED) {
                        if (!isAdmin) {
                                throw new BusinessException(
                                                EvidenceErrorCode.EVIDENCE_VERSION_DELETE_NOT_ALLOWED);
                        }

                        return;
                }

                if (status == VerificationStatus.PENDING
                                || status == VerificationStatus.REJECTED) {

                        boolean isSubmitter = version.getSubmittedBy() != null
                                        && Objects.equals(
                                                        version.getSubmittedBy().getId(),
                                                        currentUser.getId());

                        if (!isSubmitter && !isPm && !isAdmin) {
                                throw new BusinessException(
                                                EvidenceErrorCode.EVIDENCE_VERSION_DELETE_NOT_ALLOWED);
                        }

                        return;
                }

                throw new BusinessException(
                                EvidenceErrorCode.EVIDENCE_VERSION_DELETE_NOT_ALLOWED);
        }

        private boolean hasAuthority(
                        Authentication authentication,
                        String authority) {
                return authentication.getAuthorities()
                                .stream()
                                .anyMatch(grantedAuthority -> authority.equals(
                                                grantedAuthority.getAuthority()));
        }

        // =========================================================================
        // Verification
        // =========================================================================

        /**
         * 기존 Evidence 검증 API를 유지하면서 현재 활성 버전과
         * 검증 이력을 함께 갱신한다.
         */
        @Transactional
        public EvidenceResponse verify(
                        Long id,
                        EvidenceVerificationRequest request) {

                Evidence evidence = getEvidenceForUpdate(id);

                VerificationStatus status = request.getStatus();
                String remark = request.getRemark();

                validateVerificationRequest(status, remark);

                List<EvidenceVersion> allVersions = evidenceVersionRepository
                                .findAllByEvidenceIdOrderByVersionNumberDesc(
                                                evidence.getId());

                EvidenceVersion version;

                if (allVersions.isEmpty()) {
                        version = evidenceVersionRepository.save(
                                        EvidenceVersion.createInitial(
                                                        evidence,
                                                        evidence.getSubmittedBy(),
                                                        evidence.getSubmittedAt()));
                } else {
                        version = evidenceVersionRepository
                                        .findTopByEvidenceIdAndDeletedFalseOrderByVersionNumberDesc(
                                                        evidence.getId())
                                        .orElseThrow(() -> new BusinessException(
                                                        EvidenceErrorCode.EVIDENCE_NOT_FOUND));
                }

                User verifiedBy = getCurrentUser();
                LocalDateTime verifiedAt = LocalDateTime.now();

                version.verify(
                                status,
                                verifiedBy,
                                verifiedAt,
                                remark);

                evidence.verify(
                                status,
                                verifiedBy,
                                verifiedAt,
                                remark);

                EvidenceVerificationHistory history = EvidenceVerificationHistory.create(
                                version,
                                status,
                                verifiedBy,
                                verifiedAt,
                                remark);

                evidenceVerificationHistoryRepository.save(history);

                return evidenceMapper.toResponse(evidence);
        }

        /**
         * 특정 Evidence Version 검증 처리.
         *
         * 삭제되지 않은 버전만 검증할 수 있다.
         * 지정한 버전이 최신 활성 버전인 경우에만
         * 부모 Evidence의 대표 검증 상태를 갱신한다.
         */
        @Transactional
        public EvidenceResponse verifyVersion(
                        Long evidenceId,
                        Integer versionNumber,
                        EvidenceVerificationRequest request) {
                validateVersionNumber(versionNumber);

                Evidence evidence = getEvidence(evidenceId);

                EvidenceVersion version = getActiveVersion(
                                evidenceId,
                                versionNumber);

                VerificationStatus status = request.getStatus();
                String remark = request.getRemark();

                validateVerificationRequest(status, remark);

                User verifiedBy = getCurrentUser();
                LocalDateTime verifiedAt = LocalDateTime.now();

                version.verify(
                                status,
                                verifiedBy,
                                verifiedAt,
                                remark);

                EvidenceVersion latestVersion = evidenceVersionRepository
                                .findTopByEvidenceIdAndDeletedFalseOrderByVersionNumberDesc(
                                                evidenceId)
                                .orElseThrow(() -> new BusinessException(
                                                EvidenceErrorCode.EVIDENCE_NOT_FOUND));

                boolean isLatestVersion = version.getId().equals(latestVersion.getId());

                if (isLatestVersion) {
                        evidence.verify(
                                        status,
                                        verifiedBy,
                                        verifiedAt,
                                        remark);
                }

                EvidenceVerificationHistory history = EvidenceVerificationHistory.create(
                                version,
                                status,
                                verifiedBy,
                                verifiedAt,
                                remark);

                evidenceVerificationHistoryRepository.save(history);

                return evidenceMapper.toResponse(evidence);
        }

        private void validateVerificationRequest(
                        VerificationStatus status,
                        String remark) {
                if (status == null || status == VerificationStatus.PENDING) {
                        throw new BusinessException(
                                        EvidenceErrorCode.EVIDENCE_VERIFICATION_NOT_ALLOWED);
                }

                if (status == VerificationStatus.REJECTED
                                && !hasText(remark)) {
                        throw new BusinessException(
                                        EvidenceErrorCode.EVIDENCE_VERIFICATION_REMARK_REQUIRED);
                }
        }

        // =========================================================================
        // 내부 조회 및 검증
        // =========================================================================

        private Evidence getEvidence(Long id) {
                return evidenceRepository.findById(id)
                                .orElseThrow(() -> new BusinessException(
                                                EvidenceErrorCode.EVIDENCE_NOT_FOUND));
        }
        
        /**
         * Evidence 행을 비관적 쓰기 잠금으로 조회한다.
         *
         * 버전 생성, 삭제, 검증 등 상태 변경 작업에서 사용한다.
         *
         * 반드시 트랜잭션 내부에서 호출해야 한다.
         *
         * @param id Evidence ID
         * @return 잠금이 적용된 Evidence
         */
        private Evidence getEvidenceForUpdate(Long id) {
                return evidenceRepository.findByIdForUpdate(id)
                                .orElseThrow(() -> new BusinessException(
                                                EvidenceErrorCode.EVIDENCE_NOT_FOUND));
        }

        private EvidenceVersion getActiveVersion(
                        Long evidenceId,
                        Integer versionNumber) {
                return evidenceVersionRepository
                                .findByEvidenceIdAndVersionNumberAndDeletedFalse(
                                                evidenceId,
                                                versionNumber)
                                .orElseThrow(() -> new BusinessException(
                                                EvidenceErrorCode.EVIDENCE_NOT_FOUND));
        }

        private void validateVersionNumber(Integer versionNumber) {
                if (versionNumber == null || versionNumber < 1) {
                        throw new BusinessException(
                                        EvidenceErrorCode.EVIDENCE_VERSION_NUMBER_INVALID);
                }
        }

        /**
         * 삭제된 버전이 존재하더라도 버전 번호는 재사용하지 않는다.
         */
        private int getNextVersionNumber(Long evidenceId) {
                return evidenceVersionRepository
                                .findTopByEvidenceIdOrderByVersionNumberDesc(evidenceId)
                                .map(version -> version.getVersionNumber() + 1)
                                .orElse(1);
        }

        /**
         * 기존 버전이 모두 삭제된 비정상 상태에서는
         * 새 버전을 추가하지 않도록 방어한다.
         *
         * 버전이 전혀 없는 레거시 Evidence는 버전 1 생성이 가능하다.
         */
        private void validateActiveVersionsExist(Long evidenceId) {
                boolean hasActiveVersion = evidenceVersionRepository
                                .existsByEvidenceIdAndDeletedFalse(evidenceId);

                boolean hasAnyVersion = evidenceVersionRepository.existsByEvidenceId(evidenceId);

                if (!hasActiveVersion && hasAnyVersion) {
                        throw new BusinessException(
                                        EvidenceErrorCode.EVIDENCE_LAST_ACTIVE_VERSION_DELETE_NOT_ALLOWED);
                }
        }

        private EvidenceRequirement getRequirement(Long requirementId) {
                return evidenceRequirementRepository.findById(requirementId)
                                .orElseThrow(() -> new BusinessException(
                                                EvidenceErrorCode.EVIDENCE_REQUIREMENT_NOT_FOUND));
        }

        private void validateProjectExists(Long projectId) {
                if (!projectRepository.existsById(projectId)) {
                        throw new BusinessException(
                                        EvidenceErrorCode.PROJECT_NOT_FOUND);
                }
        }

        private Wbs getWbs(Long wbsId) {
                return wbsRepository.findById(wbsId)
                                .orElseThrow(() -> new BusinessException(
                                                EvidenceErrorCode.WBS_NOT_FOUND));
        }

        private Wbs getOptionalWbs(Long wbsId) {
                return wbsId == null ? null : getWbs(wbsId);
        }

        private void validateWbsBelongsToProject(
                        Wbs wbs,
                        Long projectId) {
                if (!wbs.getProject().getId().equals(projectId)) {
                        throw new BusinessException(
                                        EvidenceErrorCode.WBS_PROJECT_MISMATCH);
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
                                        CommonErrorCode.UNAUTHORIZED);
                }

                return userRepository.findByUsername(authentication.getName())
                                .orElseThrow(() -> new BusinessException(
                                                CommonErrorCode.USER_NOT_FOUND));
        }

        // =========================================================================
        // Evidence Key 생성
        // =========================================================================

        private String generateEvidenceKey(Long projectId) {
                List<Evidence> evidences = evidenceRepository.findAllByProjectId(projectId);

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
