
package com.seos.pmis.contract.controller;

import com.seos.pmis.auth.security.filter.JwtAuthenticationFilter;
import com.seos.pmis.auth.security.handler.JwtAccessDeniedHandler;
import com.seos.pmis.auth.security.handler.JwtAuthenticationEntryPoint;
import com.seos.pmis.auth.security.jwt.JwtProvider;
import com.seos.pmis.auth.service.CustomUserDetailsService;
import com.seos.pmis.auth.service.UserSessionService;
import com.seos.pmis.config.security.SecurityConfig;
import com.seos.pmis.contract.dto.request.CreateContractRequest;
import com.seos.pmis.contract.dto.request.UpdateContractRequest;
import com.seos.pmis.contract.dto.response.ContractResponse;
import com.seos.pmis.contract.entity.ContractStatus;
import com.seos.pmis.contract.entity.ContractType;
import com.seos.pmis.contract.service.ContractService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ContractController.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        JwtAccessDeniedHandler.class,
        JwtAuthenticationEntryPoint.class
})
class ContractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ContractService contractService;

    @MockitoBean
    private JwtProvider jwtProvider;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @MockitoBean
    private UserSessionService userSessionService;

    private static final Long CONTRACT_ID = 1L;

    private ContractResponse createResponse() {
        return ContractResponse.builder()
                .id(CONTRACT_ID)
                .contractCode("CONTRACT-2026-001")
                .contractName("PMIS 구축 계약")
                .contractType(ContractType.PROJECT)
                .status(ContractStatus.DRAFT)
                .signedDate(LocalDate.of(2026, 11, 27))
                .startDate(LocalDate.of(2026, 12, 1))
                .endDate(LocalDate.of(2027, 11, 30))
                .currencyCode("KRW")
                .contractAmount(new BigDecimal("100000000"))
                .description("PMIS 구축 프로젝트 계약")
                .createdAt(LocalDateTime.of(2026, 11, 27, 9, 0))
                .updatedAt(LocalDateTime.of(2026, 11, 27, 9, 0))
                .build();
    }

    /**
     * 계약 목록 조회
     */
    @Test
    @DisplayName("USER는 계약 목록을 조회할 수 있다.")
    @WithMockUser(roles = "USER")
    void search_success() throws Exception {

        Page<ContractResponse> page = new PageImpl<>(
                List.of(createResponse()),
                PageRequest.of(0, 20),
                1
        );

        given(contractService.search(
                any(),
                any()
        )).willReturn(page);

        mockMvc.perform(get("/api/contracts")
                        .param("contractCode", "CONTRACT")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].id").value(CONTRACT_ID))
                .andExpect(jsonPath("$.data.content[0].contractCode")
                        .value("CONTRACT-2026-001"))
                .andExpect(jsonPath("$.data.content[0].contractName")
                        .value("PMIS 구축 계약"))
                .andExpect(jsonPath("$.data.content[0].status").value("DRAFT"));

        then(contractService).should()
                .search(any(), any());
    }

    @Test
    @DisplayName("계약이 없으면 빈 목록을 반환한다.")
    @WithMockUser(roles = "USER")
    void search_empty() throws Exception {

        given(contractService.search(any(), any()))
                .willReturn(new PageImpl<>(
                        List.of(),
                        PageRequest.of(0, 20),
                        0
                ));

        mockMvc.perform(get("/api/contracts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content.length()").value(0));

        then(contractService).should()
                .search(any(), any());
    }

    /**
     * 계약 상세 조회
     */
    @Test
    @DisplayName("USER는 계약 상세 정보를 조회할 수 있다.")
    @WithMockUser(roles = "USER")
    void get_success() throws Exception {

        given(contractService.get(CONTRACT_ID))
                .willReturn(createResponse());

        mockMvc.perform(get("/api/contracts/{contractId}", CONTRACT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(CONTRACT_ID))
                .andExpect(jsonPath("$.data.contractCode")
                        .value("CONTRACT-2026-001"))
                .andExpect(jsonPath("$.data.contractName")
                        .value("PMIS 구축 계약"))
                .andExpect(jsonPath("$.data.contractType").value("PROJECT"))
                .andExpect(jsonPath("$.data.status").value("DRAFT"))
                .andExpect(jsonPath("$.data.currencyCode").value("KRW"));

        then(contractService).should()
                .get(CONTRACT_ID);
    }

    /**
     * 계약 등록
     */
    @Test
    @DisplayName("ADMIN은 계약을 등록할 수 있다.")
    @WithMockUser(roles = "ADMIN")
    void create_success() throws Exception {

        given(contractService.create(any(CreateContractRequest.class)))
                .willReturn(createResponse());

        mockMvc.perform(post("/api/contracts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "contractCode": "CONTRACT-2026-001",
                                    "contractName": "PMIS 구축 계약",
                                    "contractType": "PROJECT",
                                    "signedDate": "2026-11-27",
                                    "startDate": "2026-12-01",
                                    "endDate": "2027-11-30",
                                    "currencyCode": "KRW",
                                    "contractAmount": 100000000,
                                    "description": "PMIS 구축 프로젝트 계약"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(CONTRACT_ID))
                .andExpect(jsonPath("$.data.contractCode")
                        .value("CONTRACT-2026-001"))
                .andExpect(jsonPath("$.data.contractName")
                        .value("PMIS 구축 계약"));

        then(contractService).should()
                .create(any(CreateContractRequest.class));
    }

    @Test
    @DisplayName("USER는 계약을 등록할 수 없다.")
    @WithMockUser(roles = "USER")
    void create_forbidden() throws Exception {

        mockMvc.perform(post("/api/contracts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "contractCode": "CONTRACT-2026-001",
                                    "contractName": "PMIS 구축 계약",
                                    "contractType": "PROJECT",
                                    "startDate": "2026-12-01",
                                    "endDate": "2027-11-30"
                                }
                                """))
                .andExpect(status().isForbidden());

        then(contractService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("계약 코드가 없으면 등록할 수 없다.")
    @WithMockUser(roles = "ADMIN")
    void create_invalidContractCode() throws Exception {

        mockMvc.perform(post("/api/contracts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "contractName": "PMIS 구축 계약",
                                    "contractType": "PROJECT",
                                    "startDate": "2026-12-01",
                                    "endDate": "2027-11-30"
                                }
                                """))
                .andExpect(status().isBadRequest());

        then(contractService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("계약명이 없으면 등록할 수 없다.")
    @WithMockUser(roles = "ADMIN")
    void create_invalidContractName() throws Exception {

        mockMvc.perform(post("/api/contracts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "contractCode": "CONTRACT-2026-001",
                                    "contractType": "PROJECT",
                                    "startDate": "2026-12-01",
                                    "endDate": "2027-11-30"
                                }
                                """))
                .andExpect(status().isBadRequest());

        then(contractService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("계약 유형이 없으면 등록할 수 없다.")
    @WithMockUser(roles = "ADMIN")
    void create_invalidContractType() throws Exception {

        mockMvc.perform(post("/api/contracts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "contractCode": "CONTRACT-2026-001",
                                    "contractName": "PMIS 구축 계약",
                                    "startDate": "2026-12-01",
                                    "endDate": "2027-11-30"
                                }
                                """))
                .andExpect(status().isBadRequest());

        then(contractService).shouldHaveNoInteractions();
    }

    /**
     * 계약 수정
     */
    @Test
    @DisplayName("ADMIN은 계약 정보를 수정할 수 있다.")
    @WithMockUser(roles = "ADMIN")
    void update_success() throws Exception {

        given(contractService.update(
                eq(CONTRACT_ID),
                any(UpdateContractRequest.class)
        )).willReturn(createResponse());

        mockMvc.perform(put("/api/contracts/{contractId}", CONTRACT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "contractName": "PMIS 구축 계약",
                                    "contractType": "PROJECT",
                                    "signedDate": "2026-11-27",
                                    "startDate": "2026-12-01",
                                    "endDate": "2027-11-30",
                                    "currencyCode": "KRW",
                                    "contractAmount": 100000000,
                                    "description": "PMIS 구축 프로젝트 계약"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(CONTRACT_ID))
                .andExpect(jsonPath("$.data.contractName")
                        .value("PMIS 구축 계약"));

        then(contractService).should()
                .update(
                        eq(CONTRACT_ID),
                        any(UpdateContractRequest.class)
                );
    }

    @Test
    @DisplayName("USER는 계약 정보를 수정할 수 없다.")
    @WithMockUser(roles = "USER")
    void update_forbidden() throws Exception {

        mockMvc.perform(put("/api/contracts/{contractId}", CONTRACT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "contractName": "PMIS 구축 계약",
                                    "contractType": "PROJECT",
                                    "startDate": "2026-12-01",
                                    "endDate": "2027-11-30",
                                    "currencyCode": "KRW"
                                }
                                """))
                .andExpect(status().isForbidden());

        then(contractService).should(never())
                .update(
                        eq(CONTRACT_ID),
                        any(UpdateContractRequest.class)
                );
    }

    @Test
    @DisplayName("계약명이 없으면 수정할 수 없다.")
    @WithMockUser(roles = "ADMIN")
    void update_invalidContractName() throws Exception {

        mockMvc.perform(put("/api/contracts/{contractId}", CONTRACT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "contractType": "PROJECT",
                                    "startDate": "2026-12-01",
                                    "endDate": "2027-11-30",
                                    "currencyCode": "KRW"
                                }
                                """))
                .andExpect(status().isBadRequest());

        then(contractService).shouldHaveNoInteractions();
    }

    /**
     * 계약 상태 변경
     */
    @Test
    @DisplayName("ADMIN은 계약 상태를 변경할 수 있다.")
    @WithMockUser(roles = "ADMIN")
    void changeStatus_success() throws Exception {

        ContractResponse activeResponse = ContractResponse.builder()
                .id(CONTRACT_ID)
                .contractCode("CONTRACT-2026-001")
                .contractName("PMIS 구축 계약")
                .contractType(ContractType.PROJECT)
                .status(ContractStatus.ACTIVE)
                .build();

        given(contractService.changeStatus(
                CONTRACT_ID,
                ContractStatus.ACTIVE
        )).willReturn(activeResponse);

        mockMvc.perform(patch("/api/contracts/{contractId}/status", CONTRACT_ID)
                        .param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));

        then(contractService).should()
                .changeStatus(CONTRACT_ID, ContractStatus.ACTIVE);
    }

    @Test
    @DisplayName("USER는 계약 상태를 변경할 수 없다.")
    @WithMockUser(roles = "USER")
    void changeStatus_forbidden() throws Exception {

        mockMvc.perform(patch("/api/contracts/{contractId}/status", CONTRACT_ID)
                        .param("status", "ACTIVE"))
                .andExpect(status().isForbidden());

        then(contractService).should(never())
                .changeStatus(CONTRACT_ID, ContractStatus.ACTIVE);
    }

    @Test
    @DisplayName("잘못된 계약 상태는 400을 반환한다.")
    @WithMockUser(roles = "ADMIN")
    void changeStatus_invalidStatus() throws Exception {

        mockMvc.perform(patch("/api/contracts/{contractId}/status", CONTRACT_ID)
                        .param("status", "UNKNOWN"))
                .andExpect(status().isBadRequest());

        then(contractService).shouldHaveNoInteractions();
    }

    /**
     * 인증 테스트
     */
    @Test
    @DisplayName("미인증 사용자는 계약 목록을 조회할 수 없다.")
    void search_unauthorized() throws Exception {

        mockMvc.perform(get("/api/contracts"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));

        then(contractService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("미인증 사용자는 계약을 등록할 수 없다.")
    void create_unauthorized() throws Exception {

        mockMvc.perform(post("/api/contracts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "contractCode": "CONTRACT-2026-001",
                                    "contractName": "PMIS 구축 계약",
                                    "contractType": "PROJECT",
                                    "startDate": "2026-12-01",
                                    "endDate": "2027-11-30"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));

        then(contractService).shouldHaveNoInteractions();
    }
}
