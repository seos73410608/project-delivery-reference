package com.seos.pmis.evidence.service;

import com.seos.pmis.common.exception.BusinessException;
import com.seos.pmis.common.util.SearchPageableFactory;
import com.seos.pmis.evidence.dto.request.EvidenceRequirementCreateRequest;
import com.seos.pmis.evidence.dto.request.EvidenceRequirementSearchRequest;
import com.seos.pmis.evidence.dto.request.EvidenceRequirementUpdateRequest;
import com.seos.pmis.evidence.dto.response.EvidenceRequirementResponse;
import com.seos.pmis.evidence.entity.EvidenceRequirement;
import com.seos.pmis.evidence.enums.RequirementStatus;
import com.seos.pmis.evidence.exception.code.EvidenceErrorCode;
import com.seos.pmis.evidence.mapper.EvidenceRequirementMapper;
import com.seos.pmis.evidence.repository.EvidenceRepository;
import com.seos.pmis.evidence.repository.EvidenceRequirementRepository;
import com.seos.pmis.evidence.specification.EvidenceRequirementSpecification;
import com.seos.pmis.project.entity.Project;
import com.seos.pmis.project.repository.ProjectRepository;
import com.seos.pmis.wbs.entity.Wbs;
import com.seos.pmis.wbs.repository.WbsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EvidenceRequirementService {

    private final EvidenceRequirementRepository evidenceRequirementRepository;
    private final EvidenceRepository evidenceRepository;

    private final ProjectRepository projectRepository;
    private final WbsRepository wbsRepository;

    private final EvidenceRequirementMapper evidenceRequirementMapper;

    /**
     * 프로젝트별 Evidence Requirement 목록 조회
     */
    public List<EvidenceRequirementResponse> findAllByProjectId(
            Long projectId
    ) {
        validateProjectExists(projectId);

        return evidenceRequirementRepository
                .findAllByProjectId(projectId)
                .stream()
                .map(evidenceRequirementMapper::toResponse)
                .toList();
    }

    /**
     * WBS별 Evidence Requirement 목록 조회
     */
    public List<EvidenceRequirementResponse> findAllByProjectIdAndWbsId(
            Long projectId,
            Long wbsId
    ) {
        validateProjectExists(projectId);

        Wbs wbs = getWbs(wbsId);

        validateWbsBelongsToProject(
                wbs,
                projectId
        );

        return evidenceRequirementRepository
                .findAllByProjectIdAndWbsId(
                        projectId,
                        wbsId
                )
                .stream()
                .map(evidenceRequirementMapper::toResponse)
                .toList();
    }

    /**
     * Evidence Requirement 단건 조회
     */
    public EvidenceRequirementResponse findById(
            Long id
    ) {
        EvidenceRequirement requirement =
                getRequirement(id);

        return evidenceRequirementMapper.toResponse(
                requirement
        );
    }

    /**
     * Evidence Requirement 검색
     */
    public Page<EvidenceRequirementResponse> search(
            EvidenceRequirementSearchRequest request
    ) {
        Pageable pageable =
                SearchPageableFactory.create(
                        request.getPage(),
                        request.getSize(),
                        request.getSortBy(),
                        request.getDirection()
                );

        var specification =
                EvidenceRequirementSpecification
                        .projectId(
                                request.getProjectId()
                        )
                        .and(
                                EvidenceRequirementSpecification.wbsId(
                                        request.getWbsId()
                                )
                        )
                        .and(
                                EvidenceRequirementSpecification.keyword(
                                        request.getKeyword()
                                )
                        )
                        .and(
                                EvidenceRequirementSpecification.requirementType(
                                        request.getRequirementType()
                                )
                        )
                        .and(
                                EvidenceRequirementSpecification.required(
                                        request.getRequired()
                                )
                        )
                        .and(
                                EvidenceRequirementSpecification.status(
                                        request.getStatus()
                                )
                        );

        return evidenceRequirementRepository
                .findAll(
                        specification,
                        pageable
                )
                .map(evidenceRequirementMapper::toResponse);
    }

    /**
     * Evidence Requirement 생성
     */
    @Transactional
    public EvidenceRequirementResponse create(
            Long projectId,
            EvidenceRequirementCreateRequest request
    ) {
        Project project =
                getProject(projectId);

        Wbs wbs =
                getOptionalWbs(
                        request.getWbsId()
                );

        if (wbs != null) {
            validateWbsBelongsToProject(
                    wbs,
                    projectId
            );
        }

        String requirementKey =
                generateRequirementKey(projectId);

        EvidenceRequirement requirement =
                evidenceRequirementMapper.toEntity(
                        request,
                        project,
                        wbs,
                        requirementKey
                );

        EvidenceRequirement saved =
                evidenceRequirementRepository.save(
                        requirement
                );

        /*
         * 생성 직후 Requirement 상태를
         * 실제 조건에 맞게 반영한다.
         *
         * required=false
         *     → NOT_REQUIRED
         *
         * required=true + Evidence 없음
         *     → PENDING 또는 MISSING
         */
        refreshStatus(saved);

        return evidenceRequirementMapper.toResponse(
                saved
        );
    }

    /**
     * Evidence Requirement 수정
     */
    @Transactional
    public EvidenceRequirementResponse update(
            Long id,
            EvidenceRequirementUpdateRequest request
    ) {
        EvidenceRequirement requirement =
                getRequirement(id);

        Wbs wbs =
                getOptionalWbs(
                        request.getWbsId()
                );

        if (wbs != null) {
            validateWbsBelongsToProject(
                    wbs,
                    requirement.getProject().getId()
            );
        }

        evidenceRequirementMapper.updateEntity(
                requirement,
                request,
                wbs
        );

        /*
         * 수정된 required / dueDate / Evidence 상태를
         * 기준으로 Requirement 상태를 다시 계산한다.
         */
        refreshStatus(requirement);

        return evidenceRequirementMapper.toResponse(
                requirement
        );
    }

    /**
     * Evidence Requirement 삭제
     *
     * 이미 Evidence가 등록된 Requirement는
     * V1에서는 삭제하지 않는다.
     */
    @Transactional
    public void delete(
            Long id
    ) {
        EvidenceRequirement requirement =
                getRequirement(id);

        /*
         * Requirement에 연결된 Evidence가 존재하면
         * Requirement를 삭제하지 않는다.
         */
        if (evidenceRepository.existsByRequirementId(
                requirement.getId()
        )) {
            throw new BusinessException(
                    EvidenceErrorCode
                            .EVIDENCE_REQUIREMENT_DELETE_NOT_ALLOWED
            );
        }

        evidenceRequirementRepository.delete(
                requirement
        );
    }

    /**
     * Evidence 등록 여부에 따른 Requirement 상태 갱신
     *
     * 상태 의미:
     *
     * NOT_REQUIRED
     *   → required=false
     *
     * MISSING
     *   → required=true
     *     + dueDate 경과
     *     + Evidence 없음
     *
     * PRESENT
     *   → Evidence 존재
     *
     * PENDING
     *   → Evidence 없음
     *     + 아직 기한 미도래
     */
    @Transactional
    public void refreshStatus(
            Long requirementId
    ) {
        EvidenceRequirement requirement =
                getRequirement(requirementId);

        refreshStatus(requirement);
    }

    /**
     * Evidence Requirement 상태 갱신
     */
    private void refreshStatus(
            EvidenceRequirement requirement
    ) {
        /*
         * 필수 증적이 아닌 경우
         * 다른 조건보다 우선하여 NOT_REQUIRED 처리한다.
         */
        if (!requirement.getRequired()) {
            requirement.changeStatus(
                    RequirementStatus.NOT_REQUIRED
            );
            return;
        }

        /*
         * 해당 Requirement에 Evidence가 하나라도 존재하면
         * PRESENT 상태로 변경한다.
         */
        boolean hasEvidence =
                !evidenceRepository
                        .findAllByRequirementId(
                                requirement.getId()
                        )
                        .isEmpty();

        if (hasEvidence) {
            requirement.changeStatus(
                    RequirementStatus.PRESENT
            );
            return;
        }

        /*
         * 필수 Requirement이고 Evidence가 없으며
         * Due Date가 지난 경우 MISSING 처리한다.
         */
        if (requirement.getDueDate() != null
                && requirement.getDueDate().isBefore(
                        LocalDate.now()
                )) {

            requirement.changeStatus(
                    RequirementStatus.MISSING
            );
            return;
        }

        /*
         * 필수 Requirement이지만
         * 아직 Evidence가 없고 Due Date가 지나지 않은 경우
         * PENDING 상태를 유지한다.
         */
        requirement.changeStatus(
                RequirementStatus.PENDING
        );
    }

    /**
     * Requirement Entity 조회
     */
    private EvidenceRequirement getRequirement(
            Long id
    ) {
        return evidenceRequirementRepository
                .findById(id)
                .orElseThrow(
                        () -> new BusinessException(
                                EvidenceErrorCode
                                        .EVIDENCE_REQUIREMENT_NOT_FOUND
                        )
                );
    }

    /**
     * Project Entity 조회
     */
    private Project getProject(
            Long projectId
    ) {
        return projectRepository
                .findById(projectId)
                .orElseThrow(
                        () -> new BusinessException(
                                EvidenceErrorCode.PROJECT_NOT_FOUND
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
     * WBS Entity 조회
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
     * 선택적 WBS 조회
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
     * WBS가 해당 Project에 속하는지 검증
     */
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

    /**
     * Requirement Key 생성
     *
     * 프로젝트 단위로
     * EVR-001 형태의 Key를 생성한다.
     */
    private String generateRequirementKey(
            Long projectId
    ) {
        List<EvidenceRequirement> requirements =
                evidenceRequirementRepository
                        .findAllByProjectId(projectId);

        int nextNumber =
                requirements.stream()
                        .map(
                                EvidenceRequirement
                                        ::getRequirementKey
                        )
                        .filter(key -> key != null)
                        .filter(
                                key -> key.startsWith("EVR-")
                        )
                        .map(
                                key -> key.substring(4)
                        )
                        .mapToInt(
                                this::parseKeyNumber
                        )
                        .max()
                        .orElse(0)
                        + 1;

        return String.format(
                "EVR-%03d",
                nextNumber
        );
    }

    /**
     * Requirement Key 숫자 부분 파싱
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