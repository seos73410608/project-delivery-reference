package com.seos.pmis.auth.entity;

public enum SessionEndReason {

    USER_LOGOUT,
    USER_SELECTED,
    ADMIN_REVOKED,
    TOKEN_EXPIRED,
    SECURITY_REVOKED
}