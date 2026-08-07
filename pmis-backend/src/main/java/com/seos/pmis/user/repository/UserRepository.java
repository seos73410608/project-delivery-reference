package com.seos.pmis.user.repository;

import com.seos.pmis.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 사용자명으로 조회
     */
    Optional<User> findByUsername(String username);

    /**
     * 이메일로 조회
     */
    Optional<User> findByEmail(String email);

    /**
     * 사용자명 존재 여부
     */
    boolean existsByUsername(String username);

    /**
     * 이메일 존재 여부
     */
    boolean existsByEmail(String email);

}