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

    /*
     * Evidence 생성/삭제 후
     * 연결된 Evidence Requirement의 상태를
     * 다시 계산하기 위해 사용한다.
     */
    private final EvidenceRequirementService evidenceRequirementService;

    /**
     * =========================================================================
     * 조회
     * =========================================================================
     */

    /**
     * Project 기준 Evidence 목록 조회
     */
    public List<EvidenceResponse> findAllByProjectId(
            Long projectId
    ) {
        validateProjectExists(projectId);

        return evidenceRepository
                .findAllByProjectId(projectId)
                .stream()
                .map(evidenceMapper::toResponse)
                .toList();
    }

    /**
     * Evidence Requirement 기준 Evidence 목록 조회
     */
    public List<EvidenceResponse> findAllByRequirementId(
            Long requirementId
    ) {
        EvidenceRequirement requirement =
                getRequirement(requirementId);

        return evidenceRepository
                .findAllByRequirementId(requirement.getId())
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

        validateWbsBelongsToProject(
                wbs,
                projectId
        );

        return evidenceRepository
                .findAllByProjectIdAndWbsId(
                        projectId,
                        wbsId
                )
                .stream()
                .map(evidenceMapper::toResponse)
                .toList();
    }

    /**
     * Evidence 단건 조회
     */
    public EvidenceResponse findById(
            Long id
    ) {
        Evidence evidence = getEvidence(id);

        return evidenceMapper.toResponse(evidence);
    }

    /**
     * Evidence 검색
     */
    public Page<EvidenceResponse> search(
            EvidenceSearchRequest request
    ) {
        Pageable pageable =
                SearchPageableFactory.create(
                        request.getPage(),
                        request.getSize(),
                        request.getSortBy(),
                        request.getDirection()
                );

        var specification =
                EvidenceSpecification
                        .projectId(request.getProjectId())
                        .and(
                                EvidenceSpecification.requirementId(
                                        request.getRequirementId()
                                )
                        )
                        .and(
                                EvidenceSpecification.wbsId(
                                        request.getWbsId()
                                )
                        )
                        .and(
                                EvidenceSpecification.keyword(
                                        request.getKeyword()
                                )
                        )
                        .and(
                                EvidenceSpecification.evidenceType(
                                        request.getEvidenceType()
                                )
                        )
                        .and(
                                EvidenceSpecification.verificationStatus(
                                        request.getVerificationStatus()
                                )
                        );

        return evidenceRepository
                .findAll(
                        specification,
                        pageable
                )
                .map(evidenceMapper::toResponse);
    }

    /**
     * =========================================================================
     * 생성
     * =========================================================================
     */

    /**
     * Evidence 생성
     *
     * Requirement를 기준으로 Project를 결정한다.
     * 현재 로그인 사용자를 submittedBy로 기록한다.
     *
     * Evidence 생성 후 연결된 Requirement의 상태를
     * 다시 계산한다.
     */
    @Transactional
    public EvidenceResponse create(
            Long requirementId,
            EvidenceCreateRequest request
    ) {
        EvidenceRequirement requirement =
                getRequirement(requirementId);

        Project project =
                requirement.getProject();

        Wbs wbs =
                getOptionalWbs(
                        request.getWbsId()
                );

        if (wbs != null) {
            validateWbsBelongsToProject(
                    wbs,
                    project.getId()
            );
        }

        User submittedBy =
                getCurrentUser();

        String evidenceKey =
                generateEvidenceKey(
                        project.getId()
                );

        Evidence evidence =
                evidenceMapper.toEntity(
                        request,
                        project,
                        requirement,
                        wbs,
                        submittedBy,
                        evidenceKey
                );

        Evidence saved =
                evidenceRepository.save(evidence);

        /*
         * Evidence 생성으로 인해
         * Requirement 상태가 변경될 수 있으므로
         * 상태를 다시 계산한다.
         *
         * 예:
         * PENDING → PRESENT
         */
        evidenceRequirementService.refreshStatus(
                requirement.getId()
        );

        return evidenceMapper.toResponse(saved);
    }

    /**
     * =========================================================================
     * 수정
     * =========================================================================
     */

    /**
     * Evidence 수정
     */
    @Transactional
    public EvidenceResponse update(
            Long id,
            EvidenceUpdateRequest request
    ) {
        Evidence evidence =
                getEvidence(id);

        Wbs wbs =
                getOptionalWbs(
                        request.getWbsId()
                );

        if (wbs != null) {
            validateWbsBelongsToProject(
                    wbs,
                    evidence.getProject().getId()
            );
        }

        evidenceMapper.updateEntity(
                evidence,
                request,
                wbs
        );

        return evidenceMapper.toResponse(evidence);
    }

    /**
     * =========================================================================
     * 삭제
     * =========================================================================
     */

    /**
     * Evidence 삭제
     *
     * Inspection Item에서 사용 중인 Evidence는 삭제할 수 없다.
     *
     * Evidence 삭제 후 연결된 Requirement의 상태를
     * 다시 계산한다.
     */
    @Transactional
    public void delete(
            Long id
    ) {
        Evidence evidence =
                getEvidence(id);

        if (inspectionItemRepository.existsByEvidenceId(
                evidence.getId()
        )) {
            throw new BusinessException(
                    EvidenceErrorCode.EVIDENCE_DELETE_NOT_ALLOWED
            );
        }

        /*
         * Evidence 삭제 후에도
         * Requirement 상태를 다시 계산해야 하므로
         * Requirement ID를 먼저 확보한다.
         */
        Long requirementId =
                evidence.getRequirement().getId();

        evidenceRepository.delete(evidence);

        /*
         * Evidence 삭제로 인해
         * Requirement 상태가 변경될 수 있다.
         *
         * 예:
         * PRESENT → PENDING
         * PRESENT → MISSING
         */
        evidenceRequirementService.refreshStatus(
                requirementId
        );
    }

    /**
     * =========================================================================
     * Verification
     * =========================================================================
     */

    /**
     * Evidence Verification
     *
     * Client에서 전달받은 Verification 요청을 기준으로
     * Evidence의 검증 상태를 변경한다.
     *
     * Client 전달 필드:
     * - status
     * - remark
     *
     * Backend 관리 필드:
     * - verifiedBy
     * - verifiedAt
     *
     * PENDING은 Verification 결과로 지정할 수 없다.
     * 실제 검증 결과는 APPROVED 또는 REJECTED만 허용한다.
     *
     * REJECTED인 경우 반려 사유를 필수로 요구한다.
     */
    @Transactional
    public EvidenceResponse verify(
            Long id,
            EvidenceVerificationRequest request
    ) {
        Evidence evidence =
                getEvidence(id);

        VerificationStatus status =
                request.getStatus();

        String remark =
                request.getRemark();

        if (status == null) {
            throw new BusinessException(
                    EvidenceErrorCode
                            .EVIDENCE_VERIFICATION_NOT_ALLOWED
            );
        }

        if (status == VerificationStatus.PENDING) {
            throw new BusinessException(
                    EvidenceErrorCode
                            .EVIDENCE_VERIFICATION_NOT_ALLOWED
            );
        }

        if (status == VerificationStatus.REJECTED
                && (remark == null || remark.isBlank())) {

            throw new BusinessException(
                    EvidenceErrorCode
                            .EVIDENCE_VERIFICATION_REMARK_REQUIRED
            );
        }

        User verifiedBy =
                getCurrentUser();

        evidence.verify(
                status,
                verifiedBy,
                LocalDateTime.now(),
                remark
        );

        return evidenceMapper.toResponse(evidence);
    }

    /**
     * =========================================================================
     * 내부 조회 / 검증
     * =========================================================================
     */

    /**
     * Evidence 조회
     */
    private Evidence getEvidence(
            Long id
    ) {
        return evidenceRepository
                .findById(id)
                .orElseThrow(
                        () -> new BusinessException(
                                EvidenceErrorCode.EVIDENCE_NOT_FOUND
                        )
                );
    }

    /**
     * Evidence Requirement 조회
     */
    private EvidenceRequirement getRequirement(
            Long requirementId
    ) {
        return evidenceRequirementRepository
                .findById(requirementId)
                .orElseThrow(
                        () -> new BusinessException(
                                EvidenceErrorCode
                                        .EVIDENCE_REQUIREMENT_NOT_FOUND
                        )
                );
    }

    /**
     * Project 존재 여부 검증
     */
    private void validateProjectExists(
            Long projectId
    ) {
        if (!projectRepository.existsById(projectId)) {
            throw new BusinessException(
                    EvidenceErrorCode.PROJECT_NOT_FOUND
            );
        }
    }

    /**
     * WBS 조회
     */
    private Wbs getWbs(
            Long wbsId
    ) {
        return wbsRepository
                .findById(wbsId)
                .orElseThrow(
                        () -> new BusinessException(
                                EvidenceErrorCode.WBS_NOT_FOUND
                        )
                );
    }

    /**
     * Optional WBS 조회
     */
    private Wbs getOptionalWbs(
            Long wbsId
    ) {
        if (wbsId == null) {
            return null;
        }

        return getWbs(wbsId);
    }

    /**
     * WBS가 Project에 속하는지 검증
     */
    private void validateWbsBelongsToProject(
            Wbs wbs,
            Long projectId
    ) {
        if (!wbs.getProject()
                .getId()
                .equals(projectId)) {

            throw new BusinessException(
                    EvidenceErrorCode.WBS_PROJECT_MISMATCH
            );
        }
    }

    /**
     * =========================================================================
     * 현재 로그인 사용자
     * =========================================================================
     */

    /**
     * 현재 인증된 사용자 조회
     *
     * JWT 인증이 정상적으로 완료된 경우
     * Authentication.getName()에는 username이 들어온다.
     */
    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null
                || authentication.getName().isBlank()) {

            throw new BusinessException(
                    CommonErrorCode.UNAUTHORIZED
            );
        }

        String username =
                authentication.getName();

        return userRepository
                .findByUsername(username)
                .orElseThrow(
                        () -> new BusinessException(
                                CommonErrorCode.USER_NOT_FOUND
                        )
                );
    }

    /**
     * =========================================================================
     * Evidence Key 생성
     * =========================================================================
     */

    /**
     * Project별 Evidence Key 생성
     *
     * EVD-001
     * EVD-002
     * EVD-003
     * ...
     */
    private String generateEvidenceKey(
            Long projectId
    ) {
        List<Evidence> evidences =
                evidenceRepository
                        .findAllByProjectId(projectId);

        int nextNumber =
                evidences
                        .stream()
                        .map(Evidence::getEvidenceKey)
                        .filter(key -> key != null)
                        .filter(key -> key.startsWith("EVD-"))
                        .map(key -> key.substring(4))
                        .mapToInt(this::parseKeyNumber)
                        .max()
                        .orElse(0)
                        + 1;

        return String.format(
                "EVD-%03d",
                nextNumber
        );
    }

    /**
     * Evidence Key 숫자 부분 파싱
     */
    private int parseKeyNumber(
            String value
    ) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            return 0;
        }
    }
}