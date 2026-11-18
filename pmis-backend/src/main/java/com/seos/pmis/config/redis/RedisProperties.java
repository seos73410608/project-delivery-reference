package com.seos.pmis.config.redis;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "pmis.redis")
public class RedisProperties {

    private boolean enabled = false;

    private String host = "127.0.0.1";

    private int port = 6379;

    private int database = 1;
}
