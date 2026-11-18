package com.seos.pmis.config.redis;

import com.seos.pmis.auth.loginattempt.LoginAttemptProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
@EnableConfigurationProperties({
        RedisProperties.class,
        LoginAttemptProperties.class
})
@ConditionalOnProperty(
        prefix = "pmis.redis",
        name = "enabled",
        havingValue = "true"
)
public class RedisConfig {

    @Bean
    public RedisConnectionFactory redisConnectionFactory(RedisProperties properties) {
        RedisStandaloneConfiguration configuration =
                new RedisStandaloneConfiguration(
                        properties.getHost(),
                        properties.getPort()
                );

        configuration.setDatabase(properties.getDatabase());

        return new LettuceConnectionFactory(configuration);
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(
            RedisConnectionFactory redisConnectionFactory
    ) {
        return new StringRedisTemplate(redisConnectionFactory);
    }
}
