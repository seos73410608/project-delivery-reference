
package com.seos.pmis.user.repository;

import com.seos.pmis.user.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 사용자명으로 조회
     */
    Optional<User> findByUsername(String username);

    /**
     * 사용자명으로 조회하면서 사용자 행 잠금
     *
     * 동시 로그인 시 세션 수 확인 및 생성 과정의
     * 경쟁 조건을 방지한다.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM User u WHERE u.username = :username")
    Optional<User> findByUsernameForUpdate(
            @Param("username") String username
    );

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
