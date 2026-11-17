
package com.seos.cmdb.ci.repository;

import com.seos.cmdb.ci.entity.CiAttributeDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface CiAttributeDefinitionRepository
        extends JpaRepository<CiAttributeDefinition, Long> {

    /**
     * 공통 및 지정된 CI 유형 계층의 활성 속성 정의 조회
     */
    @Query("""
            SELECT definition
            FROM CiAttributeDefinition definition
            LEFT JOIN FETCH definition.parent
            LEFT JOIN FETCH definition.ciType
            WHERE definition.enabled = true
              AND (
                  definition.ciType IS NULL
                  OR definition.ciType.id IN :ciTypeIds
              )
            ORDER BY definition.displayOrder ASC, definition.id ASC
            """)
    List<CiAttributeDefinition> findEffectiveDefinitions(
            @Param("ciTypeIds") Collection<Long> ciTypeIds
    );
}
