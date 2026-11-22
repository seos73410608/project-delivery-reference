
package com.seos.pmis.operationscope.repository;

import com.seos.pmis.config.jpa.JpaConfig;
import com.seos.pmis.operation.entity.Operation;
import com.seos.pmis.operation.entity.OperationStatus;
import com.seos.pmis.operation.entity.OperationType;
import com.seos.pmis.operation.repository.OperationRepository;
import com.seos.pmis.operationscope.entity.OperationScope;
import com.seos.pmis.operationscope.entity.OperationScopeStatus;
import com.seos.pmis.requirement.entity.OperationalScope;
import com.seos.pmis.requirement.entity.OperationalScopeType;
import com.seos.pmis.requirement.repository.OperationalScopeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaConfig.class)
class OperationScopeRepositoryTest {

    @Autowired
    private OperationScopeRepository operationScopeRepository;

    @Autowired
    private OperationRepository operationRepository;

    @Autowired
    private OperationalScopeRepository operationalScopeRepository;

    @Test
    void operationScopeQueries_returnExpectedRelationships() {
        // given
        Operation operation1 = createOperation("TEST-OS-OP-000001");
        Operation operation2 = createOperation("TEST-OS-OP-000002");

        OperationalScope scope1 = createOperationalScope("TEST-OS-SCP-000001");
        OperationalScope scope2 = createOperationalScope("TEST-OS-SCP-000002");

        OperationScope relationship1 = createRelationship(
                operation1,
                scope1,
                OperationScopeStatus.ACTIVE);

        OperationScope relationship2 = createRelationship(
                operation1,
                scope2,
                OperationScopeStatus.PLANNED);

        OperationScope relationship3 = createRelationship(
                operation2,
                scope1,
                OperationScopeStatus.COMPLETED);

        operationScopeRepository.saveAllAndFlush(
                List.of(relationship1, relationship2, relationship3));

        // when & then
        assertThat(operationScopeRepository
                .findAllByOperation_Id(operation1.getId()))
                .hasSize(2);

        assertThat(operationScopeRepository
                .findAllByOperationalScope_Id(scope1.getId()))
                .hasSize(2);

        assertThat(operationScopeRepository
                .findByOperation_IdAndOperationalScope_IdAndStatus(
                        operation1.getId(),
                        scope1.getId(),
                        OperationScopeStatus.ACTIVE))
                .isPresent()
                .get()
                .extracting(OperationScope::getStatus)
                .isEqualTo(OperationScopeStatus.ACTIVE);

        assertThat(operationScopeRepository
                .existsByOperation_IdAndOperationalScope_IdAndStatus(
                        operation1.getId(),
                        scope2.getId(),
                        OperationScopeStatus.PLANNED))
                .isTrue();

        assertThat(operationScopeRepository
                .existsByOperation_IdAndOperationalScope_IdAndStatus(
                        operation2.getId(),
                        scope2.getId(),
                        OperationScopeStatus.ACTIVE))
                .isFalse();
    }

    @Test
    void operationScopeHistory_allowsNewRelationshipAfterClosedRelationship() {
        // given
        Operation operation = createOperation("TEST-OS-HIST-000001");
        OperationalScope scope = createOperationalScope("TEST-OS-HIST-SCP-000001");

        OperationScope completedRelationship = createRelationship(
                operation,
                scope,
                OperationScopeStatus.COMPLETED);

        OperationScope cancelledRelationship = createRelationship(
                operation,
                scope,
                OperationScopeStatus.CANCELLED);

        operationScopeRepository.saveAllAndFlush(
                List.of(completedRelationship, cancelledRelationship));

        // when
        OperationScope newRelationship = createRelationship(
                operation,
                scope,
                OperationScopeStatus.PLANNED);

        operationScopeRepository.saveAndFlush(newRelationship);

        // then
        List<OperationScope> relationships = operationScopeRepository.findAllByOperation_Id(operation.getId());

        assertThat(relationships).hasSize(3);
        assertThat(relationships)
                .extracting(OperationScope::getStatus)
                .containsExactlyInAnyOrder(
                        OperationScopeStatus.COMPLETED,
                        OperationScopeStatus.CANCELLED,
                        OperationScopeStatus.PLANNED);

        assertThat(newRelationship.getId())
                .isNotEqualTo(completedRelationship.getId())
                .isNotEqualTo(cancelledRelationship.getId());
    }

    @Test
    void operationScopeUniqueIndex_preventsDuplicateActiveRelationships() {
        // given
        Operation operation = createOperation("TEST-OS-DUP-000001");
        OperationalScope scope = createOperationalScope("TEST-OS-DUP-SCP-000001");

        operationScopeRepository.saveAndFlush(
                createRelationship(
                        operation,
                        scope,
                        OperationScopeStatus.PLANNED));

        // when & then
        assertThatThrownBy(() -> operationScopeRepository.saveAndFlush(
                createRelationship(
                        operation,
                        scope,
                        OperationScopeStatus.ACTIVE)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    private Operation createOperation(String operationCode) {
        return operationRepository.saveAndFlush(
                Operation.builder()
                        .operationCode(operationCode)
                        .operationName("테스트 운영 서비스")
                        .operationType(OperationType.APPLICATION_SERVICE)
                        .status(OperationStatus.ACTIVE)
                        .build());
    }

    private OperationalScope createOperationalScope(String scopeCode) {
        return operationalScopeRepository.saveAndFlush(
                OperationalScope.builder()
                        .scopeCode(scopeCode)
                        .scopeName("테스트 운영 범위")
                        .scopeType(OperationalScopeType.SYSTEM)
                        .build());
    }

    private OperationScope createRelationship(
            Operation operation,
            OperationalScope scope,
            OperationScopeStatus status) {
        return OperationScope.builder()
                .operation(operation)
                .operationalScope(scope)
                .status(status)
                .effectiveFrom(LocalDate.of(2026, 1, 1))
                .description("운영 범위 관계 테스트")
                .build();
    }
}
