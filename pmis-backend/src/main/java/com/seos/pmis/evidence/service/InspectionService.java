package com.seos.pmis.evidence.service;

import com.seos.pmis.common.exception.BusinessException;
import com.seos.pmis.evidence.dto.request.InspectionCreateRequest;
import com.seos.pmis.evidence.dto.request.InspectionItemCreateRequest;
import com.seos.pmis.evidence.dto.request.InspectionItemResultUpdateRequest;
import com.seos.pmis.evidence.dto.request.InspectionItemUpdateRequest;
import com.seos.pmis.evidence.dto.request.InspectionStatusUpdateRequest;
import com.seos.pmis.evidence.dto.request.InspectionUpdateRequest;
import com.seos.pmis.evidence.dto.response.InspectionItemResponse;
import com.seos.pmis.evidence.dto.response.InspectionResponse;
import com.seos.pmis.evidence.entity.Evidence;
import com.seos.pmis.evidence.entity.EvidenceRequirement;
import com.seos.pmis.evidence.entity.Inspection;
import com.seos.pmis.evidence.entity.InspectionItem;
import com.seos.pmis.evidence.enums.InspectionItemResult;
import com.seos.pmis.evidence.enums.InspectionStatus;
import com.seos.pmis.evidence.exception.code.EvidenceErrorCode;
import com.seos.pmis.evidence.mapper.InspectionItemMapper;
import com.seos.pmis.evidence.repository.EvidenceRepository;
import com.seos.pmis.evidence.repository.EvidenceRequirementRepository;
import com.seos.pmis.evidence.repository.InspectionItemRepository;
import com.seos.pmis.evidence.repository.InspectionRepository;
import com.seos.pmis.project.entity.Project;
import com.seos.pmis.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InspectionService {

    private final InspectionRepository inspectionRepository;
    private final InspectionItemRepository inspectionItemRepository;

    private final EvidenceRequirementRepository evidenceRequirementRepository;
    private final EvidenceRepository evidenceRepository;
    private final ProjectRepository projectRepository;

    private final InspectionItemMapper inspectionItemMapper;

    /**
     * =========================================================================
     * Inspection 조회
     * =========================================================================
     */

    /**
     * Project 기준 Inspection 목록 조회
     */
    public List<InspectionResponse> findAllByProjectId(
            Long projectId
    ) {
        validateProjectExists(projectId);

        return inspectionRepository
                .findAllByProjectId(projectId)
                .stream()
                .map(this::toInspectionResponse)
                .toList();
    }

    /**
     * Project + Status 기준 Inspection 조회
     */
    public List<InspectionResponse> findAllByProjectIdAndStatus(
            Long projectId,
            InspectionStatus status
    ) {
        validateProjectExists(projectId);

        return inspectionRepository
                .findAllByProjectIdAndStatus(
                        projectId,
                        status
                )
                .stream()
                .map(this::toInspectionResponse)
                .toList();
    }

    /**
     * Inspection 단건 조회
     */
    public InspectionResponse findById(
            Long id
    ) {
        Inspection inspection =
                getInspection(id);

        return toInspectionResponse(inspection);
    }

    /**
     * Inspection Item 목록 조회
     */
    public List<InspectionItemResponse> findAllItems(
            Long inspectionId
    ) {
        getInspection(inspectionId);

        return inspectionItemRepository
                .findAllByInspectionIdOrderBySortOrderAsc(
                        inspectionId
                )
                .stream()
                .map(inspectionItemMapper::toResponse)
                .toList();
    }

    /**
     * Inspection Item 단건 조회
     */
    public InspectionItemResponse findItemById(
            Long itemId
    ) {
        InspectionItem item =
                getInspectionItem(itemId);

        return inspectionItemMapper.toResponse(item);
    }

    /**
     * =========================================================================
     * Inspection 생성
     * =========================================================================
     */

    /**
     * Inspection 생성
     */
    @Transactional
    public InspectionResponse create(
            Long projectId,
            InspectionCreateRequest request
    ) {
        Project project =
                getProject(projectId);

        Inspection inspection =
                Inspection.builder()
                        .project(project)
                        .title(request.getTitle())
                        .description(request.getDescription())
                        .inspectionDate(request.getInspectionDate())
                        .inspectorName(request.getInspectorName())
                        .resultRemark(request.getResultRemark())
                        .status(InspectionStatus.PLANNED)
                        .build();

        Inspection saved =
                inspectionRepository.save(inspection);

        return toInspectionResponse(saved);
    }

    /**
     * =========================================================================
     * Inspection 수정
     * =========================================================================
     */

    /**
     * Inspection 기본 정보 수정
     *
     * 상태 자체는 이 메서드에서 변경하지 않는다.
     */
    @Transactional
    public InspectionResponse update(
            Long id,
            InspectionUpdateRequest request
    ) {
        Inspection inspection =
                getInspection(id);

        inspection.update(
                request.getTitle(),
                request.getDescription(),
                request.getInspectionDate(),
                request.getInspectorName(),
                request.getResultRemark()
        );

        return toInspectionResponse(inspection);
    }

    /**
     * =========================================================================
     * Inspection 상태 변경
     * =========================================================================
     */

    /**
     * Inspection 상태 변경
     *
     * 허용되는 상태 전이:
     *
     * PLANNED
     *   → IN_PROGRESS
     *
     * IN_PROGRESS
     *   → PASSED
     *   → FAILED
     *
     * PASSED
     *   → CLOSED
     *
     * FAILED
     *   → CLOSED
     */
    @Transactional
    public InspectionResponse changeStatus(
            Long id,
            InspectionStatusUpdateRequest request
    ) {
        Inspection inspection =
                getInspection(id);

        InspectionStatus currentStatus =
                inspection.getStatus();

        InspectionStatus targetStatus =
                request.getStatus();

        validateStatusTransition(
                currentStatus,
                targetStatus
        );

        LocalDateTime now =
                LocalDateTime.now();

        switch (targetStatus) {

            case IN_PROGRESS:
                inspection.start(now);
                break;

            case PASSED:
                inspection.pass(
                        now,
                        request.getResultRemark()
                );
                break;

            case FAILED:
                inspection.fail(
                        now,
                        request.getResultRemark()
                );
                break;

            case CLOSED:
                inspection.close(now);
                break;

            case PLANNED:
                /*
                 * PLANNED는 신규 생성 시 기본 상태로만 사용한다.
                 * 기존 Inspection을 다시 PLANNED로 되돌리는 것은
                 * 허용하지 않는다.
                 */
                throw new BusinessException(
                        EvidenceErrorCode
                                .INSPECTION_INVALID_STATUS_TRANSITION
                );
        }

        return toInspectionResponse(inspection);
    }

    /**
     * =========================================================================
     * Inspection Item 생성
     * =========================================================================
     */

    /**
     * Inspection Item 생성
     *
     * Requirement는 반드시 해당 Inspection의 Project에 속해야 한다.
     *
     * Evidence가 지정된 경우:
     * - Evidence가 존재해야 한다.
     * - Evidence의 Project가 Inspection Project와 같아야 한다.
     * - Evidence가 지정된 Requirement에 속해야 한다.
     */
    @Transactional
    public InspectionItemResponse createItem(
            Long inspectionId,
            InspectionItemCreateRequest request
    ) {
        Inspection inspection =
                getInspection(inspectionId);

        EvidenceRequirement requirement =
                getRequirement(
                        request.getRequirementId()
                );

        validateRequirementProject(
                inspection,
                requirement
        );

        Evidence evidence =
                getOptionalEvidence(
                        request.getEvidenceId()
                );

        if (evidence != null) {
            validateEvidenceProject(
                    inspection,
                    evidence
            );

            validateEvidenceRequirement(
                    evidence,
                    requirement
            );
        }

        InspectionItem item =
                inspectionItemMapper.toEntity(
                        request,
                        inspection,
                        requirement,
                        evidence
                );

        InspectionItem saved =
                inspectionItemRepository.save(item);

        return inspectionItemMapper.toResponse(saved);
    }

    /**
     * =========================================================================
     * Inspection Item 수정
     * =========================================================================
     */

    /**
     * Inspection Item 수정
     */
    @Transactional
    public InspectionItemResponse updateItem(
            Long itemId,
            InspectionItemUpdateRequest request
    ) {
        InspectionItem item =
                getInspectionItem(itemId);

        Inspection inspection =
                item.getInspection();

        EvidenceRequirement requirement =
                getRequirement(
                        request.getRequirementId()
                );

        validateRequirementProject(
                inspection,
                requirement
        );

        Evidence evidence =
                getOptionalEvidence(
                        request.getEvidenceId()
                );

        if (evidence != null) {
            validateEvidenceProject(
                    inspection,
                    evidence
            );

            validateEvidenceRequirement(
                    evidence,
                    requirement
            );
        }

        inspectionItemMapper.updateEntity(
                item,
                request,
                requirement,
                evidence
        );

        return inspectionItemMapper.toResponse(item);
    }

    /**
     * =========================================================================
     * Inspection Item 결과 변경
     * =========================================================================
     */

    /**
     * Inspection Item 결과 변경
     *
     * PENDING
     *   → PASSED
     *   → FAILED
     *   → NOT_APPLICABLE
     *
     * PASSED / FAILED / NOT_APPLICABLE 상태에서
     * 다시 PENDING으로 되돌리는 것도 허용한다.
     */
    @Transactional
    public InspectionItemResponse changeItemResult(
            Long itemId,
            InspectionItemResultUpdateRequest request
    ) {
        InspectionItem item =
                getInspectionItem(itemId);

        InspectionItemResult currentResult =
                item.getResult();

        InspectionItemResult targetResult =
                request.getResult();

        validateItemResultTransition(
                currentResult,
                targetResult
        );

        item.changeResult(
                targetResult,
                request.getRemark()
        );

        return inspectionItemMapper.toResponse(item);
    }

    /**
     * =========================================================================
     * 내부 조회
     * =========================================================================
     */

    /**
     * Inspection 조회
     */
    private Inspection getInspection(
            Long id
    ) {
        return inspectionRepository
                .findById(id)
                .orElseThrow(
                        () -> new BusinessException(
                                EvidenceErrorCode
                                        .INSPECTION_NOT_FOUND
                        )
                );
    }

    /**
     * Inspection Item 조회
     */
    private InspectionItem getInspectionItem(
            Long itemId
    ) {
        return inspectionItemRepository
                .findById(itemId)
                .orElseThrow(
                        () -> new BusinessException(
                                EvidenceErrorCode
                                        .INSPECTION_ITEM_NOT_FOUND
                        )
                );
    }

    /**
     * Project 조회
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
     * Requirement 조회
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
     * Evidence 조회
     */
    private Evidence getEvidence(
            Long evidenceId
    ) {
        return evidenceRepository
                .findById(evidenceId)
                .orElseThrow(
                        () -> new BusinessException(
                                EvidenceErrorCode.EVIDENCE_NOT_FOUND
                        )
                );
    }

    /**
     * Optional Evidence 조회
     */
    private Evidence getOptionalEvidence(
            Long evidenceId
    ) {
        if (evidenceId == null) {
            return null;
        }

        return getEvidence(evidenceId);
    }

    /**
     * =========================================================================
     * Project / Evidence 관계 검증
     * =========================================================================
     */

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
     * Requirement가 Inspection의 Project에 속하는지 검증
     */
    private void validateRequirementProject(
            Inspection inspection,
            EvidenceRequirement requirement
    ) {
        Long inspectionProjectId =
                inspection.getProject().getId();

        Long requirementProjectId =
                requirement.getProject().getId();

        if (!inspectionProjectId.equals(
                requirementProjectId
        )) {
            throw new BusinessException(
                    EvidenceErrorCode
                            .INSPECTION_REQUIREMENT_PROJECT_MISMATCH
            );
        }
    }

    /**
     * Evidence가 Inspection의 Project에 속하는지 검증
     */
    private void validateEvidenceProject(
            Inspection inspection,
            Evidence evidence
    ) {
        Long inspectionProjectId =
                inspection.getProject().getId();

        Long evidenceProjectId =
                evidence.getProject().getId();

        if (!inspectionProjectId.equals(
                evidenceProjectId
        )) {
            throw new BusinessException(
                    EvidenceErrorCode
                            .INSPECTION_EVIDENCE_PROJECT_MISMATCH
            );
        }
    }

    /**
     * Evidence가 해당 Requirement에 속하는지 검증
     */
    private void validateEvidenceRequirement(
            Evidence evidence,
            EvidenceRequirement requirement
    ) {
        Long evidenceRequirementId =
                evidence.getRequirement().getId();

        Long requirementId =
                requirement.getId();

        if (!evidenceRequirementId.equals(
                requirementId
        )) {
            throw new BusinessException(
                    EvidenceErrorCode
                            .INSPECTION_EVIDENCE_REQUIREMENT_MISMATCH
            );
        }
    }

    /**
     * =========================================================================
     * 상태 전이 검증
     * =========================================================================
     */

    /**
     * Inspection 상태 전이 검증
     */
    private void validateStatusTransition(
            InspectionStatus currentStatus,
            InspectionStatus targetStatus
    ) {
        if (currentStatus == null
                || targetStatus == null) {

            throw new BusinessException(
                    EvidenceErrorCode
                            .INSPECTION_INVALID_STATUS_TRANSITION
            );
        }

        boolean valid =
                switch (currentStatus) {

                    case PLANNED ->
                            targetStatus
                                    == InspectionStatus.IN_PROGRESS;

                    case IN_PROGRESS ->
                            targetStatus
                                    == InspectionStatus.PASSED
                            || targetStatus
                                    == InspectionStatus.FAILED;

                    case PASSED ->
                            targetStatus
                                    == InspectionStatus.CLOSED;

                    case FAILED ->
                            targetStatus
                                    == InspectionStatus.CLOSED;

                    case CLOSED ->
                            false;
                };

        if (!valid) {
            throw new BusinessException(
                    EvidenceErrorCode
                            .INSPECTION_INVALID_STATUS_TRANSITION
            );
        }
    }

    /**
     * Inspection Item 결과 전이 검증
     */
    private void validateItemResultTransition(
            InspectionItemResult currentResult,
            InspectionItemResult targetResult
    ) {
        if (currentResult == null
                || targetResult == null) {

            throw new BusinessException(
                    EvidenceErrorCode
                            .INSPECTION_ITEM_INVALID_RESULT_TRANSITION
            );
        }

        if (currentResult == targetResult) {
            return;
        }

        boolean valid =
                switch (currentResult) {

                    case PENDING ->
                            targetResult
                                    == InspectionItemResult.PASSED
                            || targetResult
                                    == InspectionItemResult.FAILED
                            || targetResult
                                    == InspectionItemResult.NOT_APPLICABLE;

                    case PASSED,
                         FAILED,
                         NOT_APPLICABLE ->
                            targetResult
                                    == InspectionItemResult.PENDING;
                };

        if (!valid) {
            throw new BusinessException(
                    EvidenceErrorCode
                            .INSPECTION_ITEM_INVALID_RESULT_TRANSITION
            );
        }
    }

    /**
     * =========================================================================
     * Response 변환
     * =========================================================================
     */

    /**
     * Inspection Response 생성
     */
    private InspectionResponse toInspectionResponse(
            Inspection inspection
    ) {
        return InspectionResponse.builder()
                .id(inspection.getId())
                .projectId(
                        inspection.getProject().getId()
                )
                .title(inspection.getTitle())
                .description(inspection.getDescription())
                .inspectionDate(
                        inspection.getInspectionDate()
                )
                .startedAt(
                        inspection.getStartedAt()
                )
                .completedAt(
                        inspection.getCompletedAt()
                )
                .status(
                        inspection.getStatus()
                )
                .inspectorName(
                        inspection.getInspectorName()
                )
                .resultRemark(
                        inspection.getResultRemark()
                )
                .createdAt(
                        inspection.getCreatedAt()
                )
                .updatedAt(
                        inspection.getUpdatedAt()
                )
                .build();
    }
}