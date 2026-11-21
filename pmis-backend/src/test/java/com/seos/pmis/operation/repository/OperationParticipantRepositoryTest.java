
package com.seos.pmis.operation.repository;

import com.seos.pmis.company.entity.Company;
import com.seos.pmis.company.entity.Organization;
import com.seos.pmis.company.entity.OrganizationType;
import com.seos.pmis.company.repository.CompanyRepository;
import com.seos.pmis.company.repository.OrganizationRepository;
import com.seos.pmis.config.jpa.JpaConfig;
import com.seos.pmis.operation.entity.Operation;
import com.seos.pmis.operation.entity.OperationParticipant;
import com.seos.pmis.operation.entity.OperationParticipantRole;
import com.seos.pmis.operation.entity.OperationStatus;
import com.seos.pmis.operation.entity.OperationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaConfig.class)
class OperationParticipantRepositoryTest {

    @Autowired
    private OperationParticipantRepository participantRepository;

    @Autowired
    private OperationRepository operationRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    private Operation operation;
    private Company company;
    private Organization organization;

    @BeforeEach
    void setUp() {
        operation = operationRepository.saveAndFlush(
                Operation.builder()
                        .operationCode("TEST-PART-OPR-001")
                        .operationName("Test Operation")
                        .operationType(OperationType.APPLICATION_SERVICE)
                        .status(OperationStatus.ACTIVE)
                        .build()
        );

        company = companyRepository.saveAndFlush(
                Company.builder()
                        .companyCode("TEST-PART-COMP-001")
                        .companyName("Test Company")
                        .build()
        );

        organization = organizationRepository.saveAndFlush(
                Organization.builder()
                        .company(company)
                        .organizationCode("TEST-PART-ORG-001")
                        .organizationName("Test Organization")
                        .organizationType(OrganizationType.DEPARTMENT)
                        .build()
        );
    }

    private OperationParticipant createParticipant(
            Operation operation,
            Company company,
            Organization organization,
            OperationParticipantRole role,
            boolean primary
    ) {
        return OperationParticipant.builder()
                .operation(operation)
                .company(company)
                .organization(organization)
                .participantRole(role)
                .primary(primary)
                .effectiveFrom(LocalDate.of(2026, 1, 1))
                .build();
    }

    @Test
    void findByOperationId_returnsParticipantsForOperation() {
        participantRepository.saveAndFlush(
                createParticipant(
                        operation, company, organization,
                        OperationParticipantRole.OPERATOR, true
                )
        );

        List<OperationParticipant> result =
                participantRepository.findByOperationId(operation.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getParticipantRole())
                .isEqualTo(OperationParticipantRole.OPERATOR);
    }

    @Test
    void findByCompanyId_returnsParticipantsForCompany() {
        participantRepository.saveAndFlush(
                createParticipant(
                        operation, company, organization,
                        OperationParticipantRole.OPERATOR, true
                )
        );

        List<OperationParticipant> result =
                participantRepository.findByCompanyId(company.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCompany().getId())
                .isEqualTo(company.getId());
    }

    @Test
    void findByOperationIdAndParticipantRole_returnsMatchingRole() {
        participantRepository.saveAndFlush(
                createParticipant(
                        operation, company, organization,
                        OperationParticipantRole.OPERATOR, true
                )
        );

        List<OperationParticipant> result =
                participantRepository.findByOperationIdAndParticipantRole(
                        operation.getId(),
                        OperationParticipantRole.OPERATOR
                );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getParticipantRole())
                .isEqualTo(OperationParticipantRole.OPERATOR);
    }

    @Test
    void findByOperationIdAndEffectiveToIsNull_returnsActiveParticipants() {
        participantRepository.saveAndFlush(
                createParticipant(
                        operation, company, organization,
                        OperationParticipantRole.OPERATOR, true
                )
        );

        List<OperationParticipant> result =
                participantRepository.findByOperationIdAndEffectiveToIsNull(
                        operation.getId()
                );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEffectiveTo()).isNull();
    }

    @Test
    void existsByOperationIdAndCompanyIdAndOrganizationIdAndParticipantRoleAndEffectiveToIsNull_returnsTrue() {
        participantRepository.saveAndFlush(
                createParticipant(
                        operation, company, organization,
                        OperationParticipantRole.OPERATOR, true
                )
        );

        boolean result = participantRepository
                .existsByOperationIdAndCompanyIdAndOrganizationIdAndParticipantRoleAndEffectiveToIsNull(
                        operation.getId(),
                        company.getId(),
                        organization.getId(),
                        OperationParticipantRole.OPERATOR
                );

        assertThat(result).isTrue();
    }

    @Test
    void existsByOperationIdAndParticipantRoleAndPrimaryTrueAndEffectiveToIsNull_returnsTrue() {
        participantRepository.saveAndFlush(
                createParticipant(
                        operation, company, organization,
                        OperationParticipantRole.OPERATOR, true
                )
        );

        boolean result = participantRepository
                .existsByOperationIdAndParticipantRoleAndPrimaryTrueAndEffectiveToIsNull(
                        operation.getId(),
                        OperationParticipantRole.OPERATOR
                );

        assertThat(result).isTrue();
    }
}
