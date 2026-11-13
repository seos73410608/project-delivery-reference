package com.seos.cmdb.relationship.repository;

import com.seos.cmdb.relationship.entity.CiRelationshipType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CiRelationshipTypeRepository
        extends JpaRepository<CiRelationshipType, Long> {

    /**
     * 관계 유형 코드 조회
     */
    Optional<CiRelationshipType> findByCode(String code);

    /**
     * 관계 유형 코드 중복 확인
     */
    boolean existsByCode(String code);

    /**
     * 활성화된 관계 유형 조회
     */
    List<CiRelationshipType> findAllByEnabledTrueOrderBySortOrderAscNameAsc();

    /**
     * 전체 관계 유형 조회
     */
    List<CiRelationshipType> findAllByOrderBySortOrderAscNameAsc();
}