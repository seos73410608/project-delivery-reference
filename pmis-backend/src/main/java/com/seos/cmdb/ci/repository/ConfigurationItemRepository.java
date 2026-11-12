package com.seos.cmdb.ci.repository;

import com.seos.cmdb.ci.entity.ConfigurationItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ConfigurationItemRepository
        extends JpaRepository<ConfigurationItem, Long> {

    /**
     * 특정 CI Type을 사용하는 CI 존재 여부
     */
    boolean existsByCiType_Id(Long ciTypeId);

    /**
     * 특정 CI Type을 사용하는 CI 개수
     */
    long countByCiType_Id(Long ciTypeId);

    /**
     * CI 코드 발급용 PostgreSQL Sequence
     */
    @Query(
            value = "SELECT nextval('public.configuration_item_code_seq')",
            nativeQuery = true
    )
    long nextCiCodeSequenceValue();
}
