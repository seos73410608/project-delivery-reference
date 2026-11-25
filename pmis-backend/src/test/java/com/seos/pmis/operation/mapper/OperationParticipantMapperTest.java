
package com.seos.pmis.operation.mapper;

import com.seos.pmis.company.entity.Company;
import com.seos.pmis.company.entity.Organization;
import com.seos.pmis.operation.dto.response.OperationParticipantResponse;
import com.seos.pmis.operation.entity.OperationParticipant;
import com.seos.pmis.operation.entity.OperationParticipantRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("OperationParticipantMapper 테스트")
class OperationParticipantMapperTest {

    private OperationParticipantMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new OperationParticipantMapper();
    }

    @Test
    @DisplayName("참여자 Entity를 회사 및 조직 정보가 포함된 Response DTO로 변환한다")
    void toResponse_withOrganization() {
        Company company = Company.builder()
                .id(10L)
                .companyCode("COMP-001")
                .companyName("운영 회사")
                .build();

        Organization organization = Organization.builder()
                .id(20L)
                .organizationCode("ORG-001")
                .organizationName("운영 조직")
                .build();

        OperationParticipant participant = OperationParticipant.builder()
                .id(100L)
                .company(company)
                .organization(organization)
                .participantRole(OperationParticipantRole.OPERATOR)
                .primary(true)
                .build();

        OperationParticipantResponse response =
                mapper.toResponse(participant);

        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getRole())
                .isEqualTo(OperationParticipantRole.OPERATOR);
        assertThat(response.isPrimary()).isTrue();

        assertThat(response.getCompany()).isNotNull();
        assertThat(response.getCompany().getId()).isEqualTo(10L);
        assertThat(response.getCompany().getCompanyCode())
                .isEqualTo("COMP-001");
        assertThat(response.getCompany().getCompanyName())
                .isEqualTo("운영 회사");

        assertThat(response.getOrganization()).isNotNull();
        assertThat(response.getOrganization().getId()).isEqualTo(20L);
        assertThat(response.getOrganization().getOrganizationCode())
                .isEqualTo("ORG-001");
        assertThat(response.getOrganization().getOrganizationName())
                .isEqualTo("운영 조직");
    }

    @Test
    @DisplayName("조직이 없는 참여자도 Response DTO로 변환한다")
    void toResponse_withoutOrganization() {
        Company company = Company.builder()
                .id(10L)
                .companyCode("COMP-001")
                .companyName("운영 회사")
                .build();

        OperationParticipant participant = OperationParticipant.builder()
                .id(101L)
                .company(company)
                .organization(null)
                .participantRole(OperationParticipantRole.SERVICE_PROVIDER)
                .primary(false)
                .build();

        OperationParticipantResponse response =
                mapper.toResponse(participant);

        assertThat(response.getId()).isEqualTo(101L);
        assertThat(response.getRole())
                .isEqualTo(OperationParticipantRole.SERVICE_PROVIDER);
        assertThat(response.isPrimary()).isFalse();

        assertThat(response.getCompany()).isNotNull();
        assertThat(response.getCompany().getCompanyName())
                .isEqualTo("운영 회사");
        assertThat(response.getOrganization()).isNull();
    }
}
