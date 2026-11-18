package com.seos.pmis.auth.loginattempt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "pmis.auth.login-attempt")
public class LoginAttemptProperties {

    private long ttlSeconds = 300;
}
