package com.seos.cmdb.relationship.service;

import com.seos.cmdb.ci.entity.ConfigurationItem;
import com.seos.cmdb.ci.repository.ConfigurationItemRepository;
import com.seos.cmdb.relationship.dto.request.CreateCiRelationshipRequest;
import com.seos.cmdb.relationship.dto.request.UpdateCiRelationshipRequest;
import com.seos.cmdb.relationship.dto.response.CiRelationshipResponse;
import com.seos.cmdb.relationship.dto.response.CiRelationshipTypeResponse;
import com.seos.cmdb.relationship.entity.CiRelationship;
import com.seos.cmdb.relationship.entity.CiRelationshipType;
import com.seos.cmdb.relationship.entity.RelationshipDirection;
import com.seos.cmdb.relationship.entity.RelationshipStatus;
import com.seos.cmdb.relationship.exception.CiRelationshipErrorCode;
import com.seos.cmdb.relationship.mapper.CiRelationshipMapper;
import com.seos.cmdb.relationship.repository.CiRelationshipRepository;
import com.seos.cmdb.relationship.repository.CiRelationshipTypeRepository;
import com.seos.common.exception.BusinessException;
import com.seos.common.exception.code.CommonErrorCode;
import com.seos.pmis.auth.security.principal.UserPrincipal;
import com.seos.pmis.user.entity.User;
import com.seos.pmis.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CiRelationshipService {

    private final CiRelationshipRepository relationshipRepository;
    private final CiRelationshipTypeRepository relationshipTypeRepository;
    private final ConfigurationItemRepository configurationItemRepository;
    private final UserRepository userRepository;
    private final CiRelationshipMapper relationshipMapper;

    /**
     * CI 관계 생성
     *
     * 동일한 ACTIVE 관계는 기존 데이터를 반환하고,
     * INACTIVE 관계는 기존 ID를 유지하여 재활성화한다.
     */
    @Transactional
    public CiRelationshipResponse create(
            Long sourceCiId,
            CreateCiRelationshipRequest request
    ) {
        ConfigurationItem sourceCi = findCi(sourceCiId);
        ConfigurationItem targetCi = findCi(request.getTargetCiId());

        if (sourceCiId.equals(request.getTargetCiId())) {
            throw new BusinessException(
                    CiRelationshipErrorCode.RELATIONSHIP_SELF_REFERENCE
            );
        }

        CiRelationshipType relationshipType =
                findRelationshipType(request.getRelationshipTypeId());

        return relationshipRepository
                .findBySourceCi_IdAndTargetCi_IdAndRelationshipType_Id(
                        sourceCiId,
                        request.getTargetCiId(),
                        request.getRelationshipTypeId()
                )
                .map(existing -> {
                    if (existing.getStatus() == RelationshipStatus.ACTIVE) {
                        return relationshipMapper.toResponse(existing);
                    }

                    validateRelationshipTypeEnabled(relationshipType);

                    existing.reactivate();
                    existing.updateDescription(request.getDescription());

                    return relationshipMapper.toResponse(existing);
                })
                .orElseGet(() -> {
                    validateRelationshipTypeEnabled(relationshipType);

                    CiRelationship relationship = CiRelationship.builder()
                            .sourceCi(sourceCi)
                            .targetCi(targetCi)
                            .relationshipType(relationshipType)
                            .description(request.getDescription())
                            .build();

                    return relationshipMapper.toResponse(
                            relationshipRepository.save(relationship)
                    );
                });
    }

    /**
     * CI 관계 단건 조회
     */
    public CiRelationshipResponse getById(Long relationshipId) {
        return relationshipMapper.toResponse(
                findRelationship(relationshipId)
        );
    }

    /**
     * 특정 CI의 관계 목록 조회
     */
    public List<CiRelationshipResponse> getAllForCi(
            Long ciId,
            RelationshipDirection direction,
            RelationshipStatus status,
            Long relationshipTypeId
    ) {
        findCi(ciId);

        if (direction == null) {
            throw new BusinessException(
                    CiRelationshipErrorCode.RELATIONSHIP_INVALID_DIRECTION
            );
        }

        boolean includeOutgoing =
                direction == RelationshipDirection.OUTGOING
                        || direction == RelationshipDirection.ALL;

        boolean includeIncoming =
                direction == RelationshipDirection.INCOMING
                        || direction == RelationshipDirection.ALL;

        return relationshipRepository.findAllForCi(
                        ciId,
                        includeOutgoing,
                        includeIncoming,
                        status,
                        relationshipTypeId
                )
                .stream()
                .map(relationshipMapper::toResponse)
                .toList();
    }

    /**
     * CI 관계 설명 수정
     *
     * 관계 식별자(source, target, type)는 변경하지 않는다.
     */
    @Transactional
    public CiRelationshipResponse update(
            Long relationshipId,
            UpdateCiRelationshipRequest request
    ) {
        CiRelationship relationship = findRelationship(relationshipId);

        relationship.updateDescription(request.getDescription());

        return relationshipMapper.toResponse(relationship);
    }

    /**
     * CI 관계 비활성화
     */
    @Transactional
    public CiRelationshipResponse deactivate(
            Long relationshipId,
            String reason
    ) {
        CiRelationship relationship = findRelationship(relationshipId);
        User actor = getCurrentUser();

        relationship.deactivate(actor, reason);

        return relationshipMapper.toResponse(relationship);
    }

    /**
     * Relationship Type 목록 조회
     */
    public List<CiRelationshipTypeResponse> getRelationshipTypes(
            boolean includeDisabled
    ) {
        List<CiRelationshipType> relationshipTypes =
                includeDisabled
                        ? relationshipTypeRepository
                                .findAllByOrderBySortOrderAscNameAsc()
                        : relationshipTypeRepository
                                .findAllByEnabledTrueOrderBySortOrderAscNameAsc();

        return relationshipTypes.stream()
                .map(relationshipMapper::toTypeResponse)
                .toList();
    }

    private ConfigurationItem findCi(Long ciId) {
        return configurationItemRepository.findById(ciId)
                .orElseThrow(() ->
                        new BusinessException(
                                CiRelationshipErrorCode.RELATIONSHIP_CI_NOT_FOUND
                        )
                );
    }

    private CiRelationship findRelationship(Long relationshipId) {
        return relationshipRepository.findById(relationshipId)
                .orElseThrow(() ->
                        new BusinessException(
                                CiRelationshipErrorCode.RELATIONSHIP_NOT_FOUND
                        )
                );
    }

    private CiRelationshipType findRelationshipType(Long relationshipTypeId) {
        return relationshipTypeRepository.findById(relationshipTypeId)
                .orElseThrow(() ->
                        new BusinessException(
                                CiRelationshipErrorCode.RELATIONSHIP_TYPE_NOT_FOUND
                        )
                );
    }

    private void validateRelationshipTypeEnabled(
            CiRelationshipType relationshipType
    ) {
        if (!Boolean.TRUE.equals(relationshipType.getEnabled())) {
            throw new BusinessException(
                    CiRelationshipErrorCode.RELATIONSHIP_TYPE_DISABLED
            );
        }
    }

    private User getCurrentUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new BusinessException(CommonErrorCode.UNAUTHORIZED);
        }

        return userRepository.findById(principal.getUserId())
                .orElseThrow(() ->
                        new BusinessException(CommonErrorCode.USER_NOT_FOUND)
                );
    }
}