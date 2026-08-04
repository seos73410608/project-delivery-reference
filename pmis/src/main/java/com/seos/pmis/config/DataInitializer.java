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

            if (userRepository.findByUsername("admin").isPresent()) {
                return;
            }

            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .name("관리자")
                    .email("admin@pmis.com")
                    .role(UserRole.ROLE_ADMIN)
                    .enabled(true)
                    .build();

            userRepository.save(admin);

            System.out.println("====================================");
            System.out.println(" Test Admin Created");
            System.out.println(" username : admin");
            System.out.println(" password : admin123");
            System.out.println("====================================");
        };
    }
}