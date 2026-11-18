package com.seos.pmis.auth.repository;

import com.seos.pmis.auth.entity.UserSecuritySetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSecuritySettingRepository
        extends JpaRepository<UserSecuritySetting, Long> {

    Optional<UserSecuritySetting> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}