
package com.seos.pmis.config;

import com.seos.pmis.auth.entity.UserSecuritySetting;
import com.seos.pmis.auth.repository.UserSecuritySettingRepository;
import com.seos.pmis.user.entity.User;
import com.seos.pmis.user.entity.UserRole;
import com.seos.pmis.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private static final int DEFAULT_MAX_SESSION_COUNT = 5;

    private final UserRepository userRepository;
    private final UserSecuritySettingRepository userSecuritySettingRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initData() {
        return args -> {
            User admin = createUser(
                    "admin",
                    "admin123",
                    "관리자",
                    "admin@pmis.com",
                    UserRole.ROLE_ADMIN
            );

            User pm = createUser(
                    "pm",
                    "pm1234",
                    "프로젝트 관리자",
                    "pm@pmis.com",
                    UserRole.ROLE_PM
            );

            User user = createUser(
                    "user",
                    "user1234",
                    "일반 사용자",
                    "user@pmis.com",
                    UserRole.ROLE_USER
            );

            ensureSecuritySetting(admin);
            ensureSecuritySetting(pm);
            ensureSecuritySetting(user);

            System.out.println("========================================");
            System.out.println(" PMIS Test Accounts");
            System.out.println("----------------------------------------");
            System.out.println("ADMIN : admin / admin123");
            System.out.println("PM    : pm    / pm1234");
            System.out.println("USER  : user  / user1234");
            System.out.println("========================================");
        };
    }

    /**
     * 테스트 계정 생성 또는 기존 계정 조회
     */
    private User createUser(
            String username,
            String password,
            String name,
            String email,
            UserRole role
    ) {
        return userRepository.findByUsername(username)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .username(username)
                            .password(passwordEncoder.encode(password))
                            .name(name)
                            .email(email)
                            .role(role)
                            .enabled(true)
                            .build();

                    return userRepository.save(newUser);
                });
    }

    /**
     * 테스트 계정의 사용자별 보안 설정 보장
     */
    private void ensureSecuritySetting(User user) {
        if (userSecuritySettingRepository.existsByUserId(user.getId())) {
            return;
        }

        UserSecuritySetting setting = UserSecuritySetting.builder()
                .user(user)
                .maxSessionCount(DEFAULT_MAX_SESSION_COUNT)
                .build();

        userSecuritySettingRepository.save(setting);
    }
}
