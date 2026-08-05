package com.seos.pmis.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {

    /**
     * 시스템 관리자
     */
    ROLE_ADMIN("관리자"),

    /**
     * 프로젝트 관리자(Project Manager)
     */
    ROLE_PM("프로젝트 관리자"),

    /**
     * 일반 사용자
     */
    ROLE_USER("일반 사용자");

    /**
     * 권한명
     */
    private final String description;
}