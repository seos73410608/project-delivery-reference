package com.seos.pmis.operation.repository;

import com.seos.pmis.operation.entity.Operation;
import com.seos.pmis.operation.entity.OperationStatus;
import com.seos.pmis.operation.entity.OperationType;
import org.junit.jupiter.api.Test;
import com.seos.pmis.config.jpa.JpaConfig;
import org.springframework.context.annotation.Import;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaConfig.class)
class OperationRepositoryTest {

    @Autowired
    private OperationRepository operationRepository;

    @Test
    void existsByOperationCode_returnsTrueWhenOperationExists() {
        // given
        Operation operation = Operation.builder()
                .operationCode("TEST-OPR-000001")
                .operationName("Test Operation")
                .operationType(OperationType.APPLICATION_SERVICE)
                .status(OperationStatus.PLANNED)
                .build();

        operationRepository.saveAndFlush(operation);

        // when
        boolean result = operationRepository
                .existsByOperationCode("TEST-OPR-000001");

        // then
        assertThat(result).isTrue();
    }

    @Test
    void existsByOperationCode_returnsFalseWhenOperationDoesNotExist() {
        // when
        boolean result = operationRepository
                .existsByOperationCode("NOT-EXIST");

        // then
        assertThat(result).isFalse();
    }

    @Test
    void findByOperationCode_returnsOperationWhenExists() {
        // given
        Operation operation = Operation.builder()
                .operationCode("TEST-OPR-000002")
                .operationName("Database Operation")
                .operationType(OperationType.DATABASE_SERVICE)
                .status(OperationStatus.ACTIVE)
                .build();

        operationRepository.saveAndFlush(operation);

        // when
        var result = operationRepository
                .findByOperationCode("TEST-OPR-000002");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getOperationName())
                .isEqualTo("Database Operation");
        assertThat(result.get().getOperationType())
                .isEqualTo(OperationType.DATABASE_SERVICE);
        assertThat(result.get().getStatus())
                .isEqualTo(OperationStatus.ACTIVE);
    }

    @Test
    void findByOperationCode_returnsEmptyWhenNotExists() {
        // when
        var result = operationRepository
                .findByOperationCode("NOT-EXIST");

        // then
        assertThat(result).isEmpty();
    }
}