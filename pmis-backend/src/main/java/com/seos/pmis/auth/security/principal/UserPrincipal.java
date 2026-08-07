package com.seos.pmis.auth.security.principal;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.List;

@Getter
@Builder
public class UserPrincipal implements UserDetails {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 사용자 PK
     */
    private Long userId;

    /**
     * 로그인 ID
     */
    private String username;

    /**
     * 암호화된 비밀번호
     */
    private String password;

    /**
     * ROLE_ADMIN
     * ROLE_USER
     */
    private String role;

    /**
     * 계정 활성 여부
     */
    @Builder.Default
    private boolean enabled = true;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    /**
     * 계정 만료 여부
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 계정 잠김 여부
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 비밀번호 만료 여부
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 계정 활성 여부
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }
}