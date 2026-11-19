package com.seos.pmis.auth.loginattempt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RedisLoginAttemptStoreTest {

    private static final String KEY_PREFIX = "pmis:auth:login-attempt:";

    private StringRedisTemplate redisTemplate;
    private ValueOperations<String, String> valueOperations;
    private ObjectMapper objectMapper;
    private LoginAttemptProperties properties;
    private RedisLoginAttemptStore store;

    @BeforeEach
    void setUp() {
        redisTemplate = mock(StringRedisTemplate.class);
        valueOperations = mock(ValueOperations.class);
        objectMapper = new ObjectMapper().findAndRegisterModules();

        properties = new LoginAttemptProperties();
        properties.setTtlSeconds(300);

        given(redisTemplate.opsForValue()).willReturn(valueOperations);

        store = new RedisLoginAttemptStore(
                redisTemplate,
                objectMapper,
                properties
        );
    }

    @Test
    void save_로그인시도정보를_JSON으로_변환하고_TTL과함께저장한다() throws Exception {
        LoginAttempt loginAttempt = createLoginAttempt();

        String expectedKey = KEY_PREFIX + loginAttempt.getLoginAttemptId();
        String expectedValue = objectMapper.writeValueAsString(loginAttempt);

        store.save(loginAttempt);

        verify(valueOperations).set(
                expectedKey,
                expectedValue,
                Duration.ofSeconds(300)
        );
    }

    @Test
    void findById_저장된_JSON을_LoginAttempt로_변환한다() throws Exception {
        LoginAttempt expected = createLoginAttempt();

        String key = KEY_PREFIX + expected.getLoginAttemptId();
        String json = objectMapper.writeValueAsString(expected);

        given(valueOperations.get(key)).willReturn(json);

        Optional<LoginAttempt> result =
                store.findById(expected.getLoginAttemptId());

        assertThat(result).isPresent();
        assertThat(result.get().getLoginAttemptId())
                .isEqualTo(expected.getLoginAttemptId());
        assertThat(result.get().getUserId())
                .isEqualTo(expected.getUserId());
        assertThat(result.get().getUsername())
                .isEqualTo(expected.getUsername());
        assertThat(result.get().getCreatedAt())
                .isEqualTo(expected.getCreatedAt());
        assertThat(result.get().getExpiresAt())
                .isEqualTo(expected.getExpiresAt());
    }

    @Test
    void findById_저장된정보가없으면_빈Optional을반환한다() {
        String loginAttemptId = "missing-attempt";
        String key = KEY_PREFIX + loginAttemptId;

        given(valueOperations.get(key)).willReturn(null);

        Optional<LoginAttempt> result = store.findById(loginAttemptId);

        assertThat(result).isEmpty();
    }

    @Test
    void consume_로그인시도정보를_원자적으로조회하고삭제한다() throws Exception {
        LoginAttempt expected = createLoginAttempt();

        String key = KEY_PREFIX + expected.getLoginAttemptId();
        String json = objectMapper.writeValueAsString(expected);

        given(valueOperations.getAndDelete(key)).willReturn(json);

        Optional<LoginAttempt> result =
                store.consume(expected.getLoginAttemptId());

        assertThat(result).isPresent();
        assertThat(result.get().getLoginAttemptId())
                .isEqualTo(expected.getLoginAttemptId());

        verify(valueOperations).getAndDelete(key);
    }

    @Test
    void consume_저장된정보가없으면_빈Optional을반환한다() {
        String loginAttemptId = "missing-attempt";
        String key = KEY_PREFIX + loginAttemptId;

        given(valueOperations.getAndDelete(key)).willReturn(null);

        Optional<LoginAttempt> result = store.consume(loginAttemptId);

        assertThat(result).isEmpty();
    }

    @Test
    void delete_로그인시도정보의_Redis키를삭제한다() {
        String loginAttemptId = "test-attempt";
        String key = KEY_PREFIX + loginAttemptId;

        store.delete(loginAttemptId);

        verify(redisTemplate).delete(key);
    }

    private LoginAttempt createLoginAttempt() {
        LocalDateTime now = LocalDateTime.of(2026, 11, 19, 6, 0);

        return LoginAttempt.builder()
                .loginAttemptId("test-attempt-001")
                .userId(1L)
                .username("test-user")
                .createdAt(now)
                .expiresAt(now.plusMinutes(5))
                .build();
    }
}