package com.seos.pmis.auth.repository;

import com.seos.pmis.auth.entity.UserSessionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface UserSessionHistoryRepository
        extends JpaRepository<UserSessionHistory, Long> {

    List<UserSessionHistory> findAllByUserIdOrderByEndedAtDescIdDesc(Long userId);

    boolean existsBySessionId(UUID sessionId);

    List<UserSessionHistory> findAllByEndedAtBeforeOrderByEndedAtAscIdAsc(
            LocalDateTime cutoff
    );
}