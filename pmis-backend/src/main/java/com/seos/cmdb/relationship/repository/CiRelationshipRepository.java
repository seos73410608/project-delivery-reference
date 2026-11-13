package com.seos.cmdb.relationship.repository;

import com.seos.cmdb.relationship.entity.CiRelationship;
import com.seos.cmdb.relationship.entity.RelationshipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CiRelationshipRepository
        extends JpaRepository<CiRelationship, Long> {

    /**
     * 출발 CI, 도착 CI, 관계 유형으로 기존 관계 조회
     * 비활성 관계도 조회하여 재활성화할 수 있도록 한다.
     */
    Optional<CiRelationship> findBySourceCi_IdAndTargetCi_IdAndRelationshipType_Id(
            Long sourceCiId,
            Long targetCiId,
            Long relationshipTypeId
    );

    /**
     * 특정 관계 유형을 사용하는 관계 개수
     */
    long countByRelationshipType_Id(Long relationshipTypeId);

    /**
     * CI 기준 관계 조회
     *
     * includeOutgoing: 출발 CI 기준 조회
     * includeIncoming: 도착 CI 기준 조회
     * status: null이면 전체 상태
     * relationshipTypeId: null이면 전체 관계 유형
     */
    @Query("""
            SELECT r
            FROM CiRelationship r
            WHERE (
                (:includeOutgoing = true AND r.sourceCi.id = :ciId)
                OR
                (:includeIncoming = true AND r.targetCi.id = :ciId)
            )
            AND (:status IS NULL OR r.status = :status)
            AND (
                :relationshipTypeId IS NULL
                OR r.relationshipType.id = :relationshipTypeId
            )
            ORDER BY r.id DESC
            """)
    List<CiRelationship> findAllForCi(
            @Param("ciId") Long ciId,
            @Param("includeOutgoing") boolean includeOutgoing,
            @Param("includeIncoming") boolean includeIncoming,
            @Param("status") RelationshipStatus status,
            @Param("relationshipTypeId") Long relationshipTypeId
    );
}