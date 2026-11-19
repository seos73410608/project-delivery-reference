package com.seos.pmis.auth.loginattempt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@ConditionalOnProperty(
        prefix = "pmis.redis",
        name = "enabled",
        havingValue = "true"
)
public class RedisLoginAttemptStore implements LoginAttemptStore {

    private static final String KEY_PREFIX = "pmis:auth:login-attempt:";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final LoginAttemptProperties properties;

    public RedisLoginAttemptStore(
            StringRedisTemplate redisTemplate,
            ObjectMapper objectMapper,
            LoginAttemptProperties properties
    ) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    @Override
    public void save(LoginAttempt loginAttempt) {
        try {
            String key = KEY_PREFIX + loginAttempt.getLoginAttemptId();
            String value = objectMapper.writeValueAsString(loginAttempt);

            redisTemplate.opsForValue().set(
                    key,
                    value,
                    Duration.ofSeconds(properties.getTtlSeconds())
            );
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("로그인 임시 정보 직렬화에 실패했습니다.", e);
        }
    }

    @Override
    public Optional<LoginAttempt> findById(String loginAttemptId) {
        String key = KEY_PREFIX + loginAttemptId;
        String value = redisTemplate.opsForValue().get(key);

        if (value == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(
                    objectMapper.readValue(value, LoginAttempt.class)
            );
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("로그인 임시 정보 역직렬화에 실패했습니다.", e);
        }
    }

    @Override
    public void delete(String loginAttemptId) {
        String key = KEY_PREFIX + loginAttemptId;
        redisTemplate.delete(key);
    }

    @Override
    public Optional<LoginAttempt> consume(String loginAttemptId) {
        String key = KEY_PREFIX + loginAttemptId;

        // Redis GETDEL: 조회와 삭제를 하나의 원자적 명령으로 처리
        String value = redisTemplate.opsForValue().getAndDelete(key);

        if (value == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(
                    objectMapper.readValue(value, LoginAttempt.class)
            );
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("로그인 임시 정보 역직렬화에 실패했습니다.", e);
        }
    }
}
