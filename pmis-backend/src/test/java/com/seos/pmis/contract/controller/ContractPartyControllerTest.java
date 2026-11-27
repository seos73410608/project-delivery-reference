
package com.seos.pmis.contract.controller;

import com.seos.pmis.auth.security.filter.JwtAuthenticationFilter;
import com.seos.pmis.auth.security.handler.JwtAccessDeniedHandler;
import com.seos.pmis.auth.security.handler.JwtAuthenticationEntryPoint;
import com.seos.pmis.auth.security.jwt.JwtProvider;
import com.seos.pmis.auth.service.CustomUserDetailsService;
import com.seos.pmis.auth.service.UserSessionService;
import com.seos.pmis.config.security.SecurityConfig;
import com.seos.pmis.contract.dto.request.CreateContractPartyRequest;
import com.seos.pmis.contract.dto.response.ContractPartyResponse;
import com.seos.pmis.contract.entity.ContractPartyRole;
import com.seos.pmis.contract.service.ContractPartyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ContractPartyController.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        JwtAccessDeniedHandler.class,
        JwtAuthenticationEntryPoint.class
})
class ContractPartyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ContractPartyService contractPartyService;

    @MockitoBean
    private JwtProvider jwtProvider;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @MockitoBean
    private UserSessionService userSessionService;

    private static final Long CONTRACT_ID = 1L;
    private static final Long PARTY_ID = 100L;
    private static final Long COMPANY_ID = 10L;

    private ContractPartyResponse createResponse() {
        return ContractPartyResponse.builder()
                .id(PARTY_ID)
                .contractId(CONTRACT_ID)
                .contractCode("CONTRACT-2026-001")
                .contractName("PMIS 구축 계약")
                .companyId(COMPANY_ID)
                .companyCode("COMPANY-001")
                .companyName("고객사")
                .organizationId(20L)
                .organizationCode("ORG-001")
                .organizationName("IT사업부")
                .partyRole(ContractPartyRole.CONTRACTOR)
                .primary(true)
                .effectiveFrom(LocalDate.of(2026, 12, 1))
                .effectiveTo(null)
                .description("계약 수행 조직")
                .createdAt(LocalDateTime.of(2026, 11, 27, 9, 0))
                .updatedAt(LocalDateTime.of(2026, 11, 27, 9, 0))
                .build();
    }

    /**
     * 계약별 당사자 목록 조회
     */
    @Test
    @DisplayName("USER는 계약별 당사자 목록을 조회할 수 있다.")
    @WithMockUser(roles = "USER")
    void getByContract_success() throws Exception {

        given(contractPartyService.getByContract(CONTRACT_ID))
                .willReturn(List.of(createResponse()));

        mockMvc.perform(get(
                        "/api/contracts/{contractId}/parties",
                        CONTRACT_ID
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(PARTY_ID))
                .andExpect(jsonPath("$.data[0].contractId").value(CONTRACT_ID))
                .andExpect(jsonPath("$.data[0].companyId").value(COMPANY_ID))
                .andExpect(jsonPath("$.data[0].partyRole")
                        .value("CONTRACTOR"))
                .andExpect(jsonPath("$.data[0].primary").value(true));

        then(contractPartyService).should()
                .getByContract(CONTRACT_ID);
    }

    @Test
    @DisplayName("계약 당사자가 없으면 빈 목록을 반환한다.")
    @WithMockUser(roles = "USER")
    void getByContract_empty() throws Exception {

        given(contractPartyService.getByContract(CONTRACT_ID))
                .willReturn(List.of());

        mockMvc.perform(get(
                        "/api/contracts/{contractId}/parties",
                        CONTRACT_ID
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(0));

        then(contractPartyService).should()
                .getByContract(CONTRACT_ID);
    }

    /**
     * 회사별 계약 당사자 이력 조회
     */
    @Test
    @DisplayName("USER는 회사별 계약 당사자 이력을 조회할 수 있다.")
    @WithMockUser(roles = "USER")
    void getByCompany_success() throws Exception {

        given(contractPartyService.getByCompany(COMPANY_ID))
                .willReturn(List.of(createResponse()));

        mockMvc.perform(get(
                        "/api/companies/{companyId}/contract-parties",
                        COMPANY_ID
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].companyId").value(COMPANY_ID))
                .andExpect(jsonPath("$.data[0].contractCode")
                        .value("CONTRACT-2026-001"));

        then(contractPartyService).should()
                .getByCompany(COMPANY_ID);
    }

    /**
     * 계약 당사자 등록
     */
    @Test
    @DisplayName("ADMIN은 계약 당사자를 등록할 수 있다.")
    @WithMockUser(roles = "ADMIN")
    void addParty_success() throws Exception {

        given(contractPartyService.addParty(
                eq(CONTRACT_ID),
                any(CreateContractPartyRequest.class)
        )).willReturn(createResponse());

        mockMvc.perform(post(
                        "/api/contracts/{contractId}/parties",
                        CONTRACT_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "companyId": 10,
                                    "organizationId": 20,
                                    "partyRole": "CONTRACTOR",
                                    "primary": true,
                                    "effectiveFrom": "2026-12-01",
                                    "description": "계약 수행 조직"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(PARTY_ID))
                .andExpect(jsonPath("$.data.companyId").value(COMPANY_ID))
                .andExpect(jsonPath("$.data.partyRole")
                        .value("CONTRACTOR"));

        then(contractPartyService).should()
                .addParty(
                        eq(CONTRACT_ID),
                        any(CreateContractPartyRequest.class)
                );
    }

    @Test
    @DisplayName("USER는 계약 당사자를 등록할 수 없다.")
    @WithMockUser(roles = "USER")
    void addParty_forbidden() throws Exception {

        mockMvc.perform(post(
                        "/api/contracts/{contractId}/parties",
                        CONTRACT_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "companyId": 10,
                                    "partyRole": "CONTRACTOR",
                                    "effectiveFrom": "2026-12-01"
                                }
                                """))
                .andExpect(status().isForbidden());

        then(contractPartyService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("회사 ID가 없으면 계약 당사자를 등록할 수 없다.")
    @WithMockUser(roles = "ADMIN")
    void addParty_invalidCompanyId() throws Exception {

        mockMvc.perform(post(
                        "/api/contracts/{contractId}/parties",
                        CONTRACT_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "partyRole": "CONTRACTOR",
                                    "effectiveFrom": "2026-12-01"
                                }
                                """))
                .andExpect(status().isBadRequest());

        then(contractPartyService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("당사자 역할이 없으면 계약 당사자를 등록할 수 없다.")
    @WithMockUser(roles = "ADMIN")
    void addParty_invalidPartyRole() throws Exception {

        mockMvc.perform(post(
                        "/api/contracts/{contractId}/parties",
                        CONTRACT_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "companyId": 10,
                                    "effectiveFrom": "2026-12-01"
                                }
                                """))
                .andExpect(status().isBadRequest());

        then(contractPartyService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("시작일이 없으면 계약 당사자를 등록할 수 없다.")
    @WithMockUser(roles = "ADMIN")
    void addParty_invalidEffectiveFrom() throws Exception {

        mockMvc.perform(post(
                        "/api/contracts/{contractId}/parties",
                        CONTRACT_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "companyId": 10,
                                    "partyRole": "CONTRACTOR"
                                }
                                """))
                .andExpect(status().isBadRequest());

        then(contractPartyService).shouldHaveNoInteractions();
    }

    /**
     * 계약 당사자 종료
     */
    @Test
    @DisplayName("ADMIN은 계약 당사자를 종료할 수 있다.")
    @WithMockUser(roles = "ADMIN")
    void closeParty_success() throws Exception {

        given(contractPartyService.closeParty(
                CONTRACT_ID,
                PARTY_ID,
                LocalDate.of(2027, 11, 30)
        )).willReturn(createResponse());

        mockMvc.perform(patch(
                        "/api/contracts/{contractId}/parties/{partyId}/close",
                        CONTRACT_ID,
                        PARTY_ID
                )
                        .param("effectiveTo", "2027-11-30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(PARTY_ID));

        then(contractPartyService).should()
                .closeParty(
                        CONTRACT_ID,
                        PARTY_ID,
                        LocalDate.of(2027, 11, 30)
                );
    }

    @Test
    @DisplayName("USER는 계약 당사자를 종료할 수 없다.")
    @WithMockUser(roles = "USER")
    void closeParty_forbidden() throws Exception {

        mockMvc.perform(patch(
                        "/api/contracts/{contractId}/parties/{partyId}/close",
                        CONTRACT_ID,
                        PARTY_ID
                )
                        .param("effectiveTo", "2027-11-30"))
                .andExpect(status().isForbidden());

        then(contractPartyService).should(never())
                .closeParty(
                        eq(CONTRACT_ID),
                        eq(PARTY_ID),
                        any(LocalDate.class)
                );
    }

    @Test
    @DisplayName("종료일 형식이 잘못되면 400을 반환한다.")
    @WithMockUser(roles = "ADMIN")
    void closeParty_invalidDate() throws Exception {

        mockMvc.perform(patch(
                        "/api/contracts/{contractId}/parties/{partyId}/close",
                        CONTRACT_ID,
                        PARTY_ID
                )
                        .param("effectiveTo", "2027/11/30"))
                .andExpect(status().isBadRequest());

        then(contractPartyService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("종료일이 없으면 400을 반환한다.")
    @WithMockUser(roles = "ADMIN")
    void closeParty_missingDate() throws Exception {

        mockMvc.perform(patch(
                        "/api/contracts/{contractId}/parties/{partyId}/close",
                        CONTRACT_ID,
                        PARTY_ID
                ))
                .andExpect(status().isBadRequest());

        then(contractPartyService).shouldHaveNoInteractions();
    }

    /**
     * 주요 당사자 여부 변경
     */
    @Test
    @DisplayName("ADMIN은 주요 당사자 여부를 변경할 수 있다.")
    @WithMockUser(roles = "ADMIN")
    void changePrimary_success() throws Exception {

        given(contractPartyService.changePrimary(
                CONTRACT_ID,
                PARTY_ID,
                false
        )).willReturn(createResponse());

        mockMvc.perform(patch(
                        "/api/contracts/{contractId}/parties/{partyId}/primary",
                        CONTRACT_ID,
                        PARTY_ID
                )
                        .param("primary", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(PARTY_ID));

        then(contractPartyService).should()
                .changePrimary(CONTRACT_ID, PARTY_ID, false);
    }

    @Test
    @DisplayName("USER는 주요 당사자 여부를 변경할 수 없다.")
    @WithMockUser(roles = "USER")
    void changePrimary_forbidden() throws Exception {

        mockMvc.perform(patch(
                        "/api/contracts/{contractId}/parties/{partyId}/primary",
                        CONTRACT_ID,
                        PARTY_ID
                )
                        .param("primary", "false"))
                .andExpect(status().isForbidden());

        then(contractPartyService).should(never())
                .changePrimary(CONTRACT_ID, PARTY_ID, false);
    }

    @Test
    @DisplayName("주요 당사자 여부 값이 잘못되면 400을 반환한다.")
    @WithMockUser(roles = "ADMIN")
    void changePrimary_invalidValue() throws Exception {

        mockMvc.perform(patch(
                        "/api/contracts/{contractId}/parties/{partyId}/primary",
                        CONTRACT_ID,
                        PARTY_ID
                )
                        .param("primary", "invalid"))
                .andExpect(status().isBadRequest());

        then(contractPartyService).shouldHaveNoInteractions();
    }

    /**
     * 인증 테스트
     */
    @Test
    @DisplayName("미인증 사용자는 계약별 당사자 목록을 조회할 수 없다.")
    void getByContract_unauthorized() throws Exception {

        mockMvc.perform(get(
                        "/api/contracts/{contractId}/parties",
                        CONTRACT_ID
                ))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));

        then(contractPartyService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("미인증 사용자는 계약 당사자를 등록할 수 없다.")
    void addParty_unauthorized() throws Exception {

        mockMvc.perform(post(
                        "/api/contracts/{contractId}/parties",
                        CONTRACT_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "companyId": 10,
                                    "partyRole": "CONTRACTOR",
                                    "effectiveFrom": "2026-12-01"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));

        then(contractPartyService).shouldHaveNoInteractions();
    }
}
