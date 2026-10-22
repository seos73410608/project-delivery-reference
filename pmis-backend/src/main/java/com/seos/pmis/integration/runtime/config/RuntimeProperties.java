package com.seos.pmis.integration.runtime.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Automation Runtime API 설정
 *
 * application.yaml의 runtime.api 설정을 바인딩한다.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "runtime.api")
public class RuntimeProperties {

    /**
     * Automation Runtime API 기본 URL
     *
     * 예: http://127.0.0.1:8000
     */
    private String baseUrl;

    /**
     * Automation Runtime API 인증 키
     *
     * application.yaml의 runtime.api.api-key 설정을 바인딩한다.
     * Runtime API 호출 시 X-API-Key 헤더에 사용한다.
     */
    private String apiKey;
}