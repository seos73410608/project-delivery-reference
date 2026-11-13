package com.seos.cmdb.server.repository;

import com.seos.cmdb.server.entity.ServerDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServerDetailRepository extends JpaRepository<ServerDetail, Long> {

    /**
     * CI ID로 서버 상세정보 조회
     */
    Optional<ServerDetail> findByConfigurationItemId(Long ciId);
}
