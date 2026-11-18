package com.seos.pmis.auth.loginattempt;

import java.util.Optional;

public interface LoginAttemptStore {

    void save(LoginAttempt loginAttempt);

    Optional<LoginAttempt> findById(String loginAttemptId);

    void delete(String loginAttemptId);
}
