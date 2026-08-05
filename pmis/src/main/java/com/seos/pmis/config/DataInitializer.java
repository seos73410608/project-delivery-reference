package com.seos.pmis.config;

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

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initData() {

        return args -> {

            createUser(
                    "admin",
                    "admin123",
                    "관리자",
                    "admin@pmis.com",
                    UserRole.ROLE_ADMIN
            );

            createUser(
                    "pm",
                    "pm1234",
                    "프로젝트 관리자",
                    "pm@pmis.com",
                    UserRole.ROLE_PM
            );

            createUser(
                    "user",
                    "user1234",
                    "일반 사용자",
                    "user@pmis.com",
                    UserRole.ROLE_USER
            );

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
     * 테스트 계정 생성
     */
    private void createUser(
            String username,
            String password,
            String name,
            String email,
            UserRole role
    ) {

        if (userRepository.findByUsername(username).isPresent()) {
            return;
        }

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .name(name)
                .email(email)
                .role(role)
                .enabled(true)
                .build();

        userRepository.save(user);
    }
}