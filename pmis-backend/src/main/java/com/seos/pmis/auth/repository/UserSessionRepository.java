package com.seos.pmis.auth.repository;

import com.seos.pmis.auth.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserSessionRepository extends JpaRepository<UserSession, Long> {

    Optional<UserSession> findBySessionId(UUID sessionId);

    List<UserSession> findAllByUserId(Long userId);

    long countByUserId(Long userId);

    boolean existsBySessionId(UUID sessionId);
}