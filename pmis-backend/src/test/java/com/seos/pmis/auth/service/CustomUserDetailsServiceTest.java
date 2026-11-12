
package com.seos.pmis.auth.service;

import com.seos.common.exception.BusinessException;
import com.seos.pmis.user.entity.User;
import com.seos.pmis.user.entity.UserRole;
import com.seos.pmis.user.repository.UserRepository;
import com.seos.pmis.auth.security.principal.UserPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Test
    void loadUserByUsername_활성사용자는_enabled_true() {

        User user = createUser(true);

        given(userRepository.findByUsername("test-user"))
                .willReturn(Optional.of(user));

        UserPrincipal principal =
                (UserPrincipal) userDetailsService.loadUserByUsername("test-user");

        assertThat(principal.isEnabled()).isTrue();
        assertThat(principal.getUsername()).isEqualTo("test-user");
        assertThat(principal.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_USER");
    }

    @Test
    void loadUserByUsername_비활성사용자는_enabled_false() {

        User user = createUser(false);

        given(userRepository.findByUsername("test-user"))
                .willReturn(Optional.of(user));

        UserPrincipal principal =
                (UserPrincipal) userDetailsService.loadUserByUsername("test-user");

        assertThat(principal.isEnabled()).isFalse();
    }

    @Test
    void loadUserByUsername_사용자가없으면_예외발생() {

        given(userRepository.findByUsername("unknown"))
                .willReturn(Optional.empty());

        assertThatThrownBy(() ->
                userDetailsService.loadUserByUsername("unknown")
        ).isInstanceOf(BusinessException.class);
    }

    private User createUser(boolean enabled) {

        return User.builder()
                .id(1L)
                .username("test-user")
                .password("encoded-password")
                .name("테스트 사용자")
                .email("test@example.com")
                .role(UserRole.ROLE_USER)
                .enabled(enabled)
                .build();
    }
}
