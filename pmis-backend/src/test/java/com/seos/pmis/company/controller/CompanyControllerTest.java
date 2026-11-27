package com.seos.pmis.company.controller;

import com.seos.pmis.auth.security.filter.JwtAuthenticationFilter;
import com.seos.pmis.auth.security.handler.JwtAccessDeniedHandler;
import com.seos.pmis.auth.security.handler.JwtAuthenticationEntryPoint;
import com.seos.pmis.auth.security.jwt.JwtProvider;
import com.seos.pmis.auth.service.CustomUserDetailsService;
import com.seos.pmis.auth.service.UserSessionService;
import com.seos.pmis.company.dto.request.CompanySearchRequest;
import com.seos.pmis.company.dto.request.CreateCompanyRequest;
import com.seos.pmis.company.dto.request.UpdateCompanyRequest;
import com.seos.pmis.company.dto.response.CompanyResponse;
import com.seos.pmis.company.dto.response.CompanyRoleAssignmentResponse;
import com.seos.pmis.company.entity.CompanyStatus;
import com.seos.pmis.company.service.CompanyRoleAssignmentService;
import com.seos.pmis.company.service.CompanyService;
import com.seos.pmis.config.security.SecurityConfig;
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

@WebMvcTest(CompanyController.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        JwtAccessDeniedHandler.class,
        JwtAuthenticationEntryPoint.class
})
class CompanyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CompanyService companyService;

    @MockitoBean
    private CompanyRoleAssignmentService companyRoleAssignmentService;

    @MockitoBean
    private JwtProvider jwtProvider;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @MockitoBean
    private UserSessionService userSessionService;

    private static final Long COMPANY_ID = 1L;
    private static final Long ASSIGNMENT_ID = 10L;

    private CompanyResponse createResponse() {
        return CompanyResponse.builder()
                .id(COMPANY_ID)
                .companyCode("TEST-COMPANY")
                .companyName("테스트 회사")
                .legalName("테스트 주식회사")
                .businessRegistrationNumber("123-45-67890")
                .countryCode("KOR")
                .website("https://example.com")
                .status(CompanyStatus.ACTIVE)
                .description("테스트 회사 설명")
                .createdAt(LocalDateTime.of(2026, 11, 27, 9, 0))
                .updatedAt(LocalDateTime.of(2026, 11, 27, 9, 0))
                .build();
    }

    private CompanyRoleAssignmentResponse createRoleAssignmentResponse() {
        return CompanyRoleAssignmentResponse.builder()
                .id(ASSIGNMENT_ID)
                .companyId(COMPANY_ID)
                .companyCode("TEST-COMPANY")
                .companyName("테스트 회사")
                .companyRoleId(1L)
                .roleCode("CUSTOMER")
                .roleName("고객사")
                .effectiveFrom(LocalDate.of(2026, 11, 27))
                .description("프로젝트 고객사")
                .build();
    }

    /**
     * 회사 등록
     */
    @Test
    @DisplayName("ADMIN은 회사를 등록할 수 있다.")
    @WithMockUser(roles = "ADMIN")
    void createCompany_success() throws Exception {

        given(companyService.create(any(CreateCompanyRequest.class)))
                .willReturn(createResponse());

        mockMvc.perform(post("/api/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "companyCode": "TEST-COMPANY",
                                    "companyName": "테스트 회사",
                                    "legalName": "테스트 주식회사",
                                    "businessRegistrationNumber": "123-45-67890",
                                    "countryCode": "KOR",
                                    "website": "https://example.com",
                                    "description": "테스트 회사 설명"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.companyCode")
                        .value("TEST-COMPANY"))
                .andExpect(jsonPath("$.data.companyName")
                        .value("테스트 회사"))
                .andExpect(jsonPath("$.data.countryCode").value("KOR"))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));

        then(companyService).should()
                .create(any(CreateCompanyRequest.class));
    }

    @Test
    @DisplayName("USER는 회사를 등록할 수 없다.")
    @WithMockUser(roles = "USER")
    void createCompany_forbidden() throws Exception {

        mockMvc.perform(post("/api/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "companyCode": "TEST-COMPANY",
                                    "companyName": "테스트 회사",
                                    "countryCode": "KOR"
                                }
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false));

        then(companyService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("회사 코드가 없으면 등록할 수 없다.")
    @WithMockUser(roles = "ADMIN")
    void createCompany_invalidCompanyCode() throws Exception {

        mockMvc.perform(post("/api/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "companyName": "테스트 회사",
                                    "countryCode": "KOR"
                                }
                                """))
                .andExpect(status().isBadRequest());

        then(companyService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("잘못된 국가 코드로는 회사를 등록할 수 없다.")
    @WithMockUser(roles = "ADMIN")
    void createCompany_invalidCountryCode() throws Exception {

        mockMvc.perform(post("/api/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "companyCode": "TEST-COMPANY",
                                    "companyName": "테스트 회사",
                                    "countryCode": "KR"
                                }
                                """))
                .andExpect(status().isBadRequest());

        then(companyService).shouldHaveNoInteractions();
    }

    /**
     * 회사 검색
     */
    @Test
    @DisplayName("USER는 회사 목록을 검색할 수 있다.")
    @WithMockUser(roles = "USER")
    void searchCompanies_success() throws Exception {

        Page<CompanyResponse> companyPage = new PageImpl<>(
                List.of(createResponse()),
                PageRequest.of(0, 10),
                1
        );

        given(companyService.search(
                any(CompanySearchRequest.class),
                any()
        )).willReturn(companyPage);

        mockMvc.perform(get("/api/companies")
                        .param("companyName", "테스트")
                        .param("countryCode", "KOR")
                        .param("status", "ACTIVE")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath(
                        "$.data.content[0].companyCode"
                ).value("TEST-COMPANY"))
                .andExpect(jsonPath("$.data.totalElements").value(1));

        then(companyService).should()
                .search(any(CompanySearchRequest.class), any());
    }

    @Test
    @DisplayName("검색 결과가 없으면 빈 목록을 반환한다.")
    @WithMockUser(roles = "USER")
    void searchCompanies_empty() throws Exception {

        Page<CompanyResponse> emptyPage = new PageImpl<>(
                List.of(),
                PageRequest.of(0, 10),
                0
        );

        given(companyService.search(
                any(CompanySearchRequest.class),
                any()
        )).willReturn(emptyPage);

        mockMvc.perform(get("/api/companies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content.length()").value(0))
                .andExpect(jsonPath("$.data.totalElements").value(0));

        then(companyService).should()
                .search(any(CompanySearchRequest.class), any());
    }

    /**
     * 회사 상세 조회
     */
    @Test
    @DisplayName("USER는 회사 상세 정보를 조회할 수 있다.")
    @WithMockUser(roles = "USER")
    void getCompany_success() throws Exception {

        given(companyService.get(COMPANY_ID))
                .willReturn(createResponse());

        mockMvc.perform(get(
                        "/api/companies/{companyId}",
                        COMPANY_ID
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.companyCode")
                        .value("TEST-COMPANY"))
                .andExpect(jsonPath("$.data.companyName")
                        .value("테스트 회사"))
                .andExpect(jsonPath("$.data.countryCode").value("KOR"));

        then(companyService).should().get(COMPANY_ID);
    }

    /**
     * 회사 수정
     */
    @Test
    @DisplayName("ADMIN은 회사 정보를 수정할 수 있다.")
    @WithMockUser(roles = "ADMIN")
    void updateCompany_success() throws Exception {

        given(companyService.update(
                eq(COMPANY_ID),
                any(UpdateCompanyRequest.class)
        )).willReturn(createResponse());

        mockMvc.perform(put(
                        "/api/companies/{companyId}",
                        COMPANY_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "companyName": "테스트 회사",
                                    "legalName": "테스트 주식회사",
                                    "businessRegistrationNumber": "123-45-67890",
                                    "countryCode": "KOR",
                                    "website": "https://example.com",
                                    "description": "테스트 회사 설명"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.companyName")
                        .value("테스트 회사"));

        then(companyService).should()
                .update(eq(COMPANY_ID), any(UpdateCompanyRequest.class));
    }

    @Test
    @DisplayName("USER는 회사 정보를 수정할 수 없다.")
    @WithMockUser(roles = "USER")
    void updateCompany_forbidden() throws Exception {

        mockMvc.perform(put(
                        "/api/companies/{companyId}",
                        COMPANY_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "companyName": "변경 회사",
                                    "countryCode": "KOR"
                                }
                                """))
                .andExpect(status().isForbidden());

        then(companyService).should(never())
                .update(eq(COMPANY_ID), any(UpdateCompanyRequest.class));
    }

    @Test
    @DisplayName("회사명이 없으면 수정할 수 없다.")
    @WithMockUser(roles = "ADMIN")
    void updateCompany_invalidCompanyName() throws Exception {

        mockMvc.perform(put(
                        "/api/companies/{companyId}",
                        COMPANY_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "countryCode": "KOR"
                                }
                                """))
                .andExpect(status().isBadRequest());

        then(companyService).shouldHaveNoInteractions();
    }

    /**
     * 회사 상태 변경
     */
    @Test
    @DisplayName("ADMIN은 회사 상태를 변경할 수 있다.")
    @WithMockUser(roles = "ADMIN")
    void changeCompanyStatus_success() throws Exception {

        given(companyService.changeStatus(
                COMPANY_ID,
                CompanyStatus.INACTIVE
        )).willReturn(
                CompanyResponse.builder()
                        .id(COMPANY_ID)
                        .companyCode("TEST-COMPANY")
                        .companyName("테스트 회사")
                        .countryCode("KOR")
                        .status(CompanyStatus.INACTIVE)
                        .build()
        );

        mockMvc.perform(patch(
                        "/api/companies/{companyId}/status",
                        COMPANY_ID
                )
                        .param("status", "INACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("INACTIVE"));

        then(companyService).should()
                .changeStatus(COMPANY_ID, CompanyStatus.INACTIVE);
    }

    @Test
    @DisplayName("USER는 회사 상태를 변경할 수 없다.")
    @WithMockUser(roles = "USER")
    void changeCompanyStatus_forbidden() throws Exception {

        mockMvc.perform(patch(
                        "/api/companies/{companyId}/status",
                        COMPANY_ID
                )
                        .param("status", "INACTIVE"))
                .andExpect(status().isForbidden());

        then(companyService).should(never())
                .changeStatus(COMPANY_ID, CompanyStatus.INACTIVE);
    }

    @Test
    @DisplayName("잘못된 회사 상태는 400을 반환한다.")
    @WithMockUser(roles = "ADMIN")
    void changeCompanyStatus_invalidStatus() throws Exception {

        mockMvc.perform(patch(
                        "/api/companies/{companyId}/status",
                        COMPANY_ID
                )
                        .param("status", "UNKNOWN"))
                .andExpect(status().isBadRequest());

        then(companyService).shouldHaveNoInteractions();
    }

    /**
     * 회사 역할 배정 이력 조회
     */
    @Test
    @DisplayName("USER는 회사 역할 배정 이력을 조회할 수 있다.")
    @WithMockUser(roles = "USER")
    void getCompanyRoleAssignments_success() throws Exception {

        given(companyRoleAssignmentService.getAssignments(COMPANY_ID))
                .willReturn(List.of(createRoleAssignmentResponse()));

        mockMvc.perform(get(
                        "/api/companies/{companyId}/roles",
                        COMPANY_ID
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(ASSIGNMENT_ID))
                .andExpect(jsonPath("$.data[0].roleCode").value("CUSTOMER"))
                .andExpect(jsonPath("$.data[0].roleName").value("고객사"));

        then(companyRoleAssignmentService).should()
                .getAssignments(COMPANY_ID);
    }

    @Test
    @DisplayName("미인증 사용자는 회사 역할 배정 이력을 조회할 수 없다.")
    void getCompanyRoleAssignments_unauthorized() throws Exception {

        mockMvc.perform(get(
                        "/api/companies/{companyId}/roles",
                        COMPANY_ID
                ))
                .andExpect(status().isUnauthorized());

        then(companyRoleAssignmentService).shouldHaveNoInteractions();
    }

    /**
     * 회사 역할 배정
     */
    @Test
    @DisplayName("ADMIN은 회사 역할을 배정할 수 있다.")
    @WithMockUser(roles = "ADMIN")
    void assignCompanyRole_success() throws Exception {

        given(companyRoleAssignmentService.assign(
                eq(COMPANY_ID),
                any()
        )).willReturn(createRoleAssignmentResponse());

        mockMvc.perform(post(
                        "/api/companies/{companyId}/roles",
                        COMPANY_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "roleCode": "CUSTOMER",
                                    "effectiveFrom": "2026-11-27",
                                    "description": "프로젝트 고객사"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(ASSIGNMENT_ID))
                .andExpect(jsonPath("$.data.roleCode").value("CUSTOMER"))
                .andExpect(jsonPath("$.data.effectiveFrom")
                        .value("2026-11-27"));

        then(companyRoleAssignmentService).should()
                .assign(eq(COMPANY_ID), any());
    }

    @Test
    @DisplayName("USER는 회사 역할을 배정할 수 없다.")
    @WithMockUser(roles = "USER")
    void assignCompanyRole_forbidden() throws Exception {

        mockMvc.perform(post(
                        "/api/companies/{companyId}/roles",
                        COMPANY_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "roleCode": "CUSTOMER",
                                    "effectiveFrom": "2026-11-27"
                                }
                                """))
                .andExpect(status().isForbidden());

        then(companyRoleAssignmentService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("역할 코드가 없으면 회사 역할을 배정할 수 없다.")
    @WithMockUser(roles = "ADMIN")
    void assignCompanyRole_invalidRoleCode() throws Exception {

        mockMvc.perform(post(
                        "/api/companies/{companyId}/roles",
                        COMPANY_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "effectiveFrom": "2026-11-27"
                                }
                                """))
                .andExpect(status().isBadRequest());

        then(companyRoleAssignmentService).shouldHaveNoInteractions();
    }

    /**
     * 회사 역할 배정 종료
     */
    @Test
    @DisplayName("ADMIN은 회사 역할 배정을 종료할 수 있다.")
    @WithMockUser(roles = "ADMIN")
    void closeCompanyRoleAssignment_success() throws Exception {

        CompanyRoleAssignmentResponse closedResponse =
                CompanyRoleAssignmentResponse.builder()
                        .id(ASSIGNMENT_ID)
                        .companyId(COMPANY_ID)
                        .companyCode("TEST-COMPANY")
                        .companyName("테스트 회사")
                        .companyRoleId(1L)
                        .roleCode("CUSTOMER")
                        .roleName("고객사")
                        .effectiveFrom(LocalDate.of(2026, 11, 27))
                        .effectiveTo(LocalDate.of(2026, 12, 31))
                        .description("프로젝트 고객사")
                        .build();

        given(companyRoleAssignmentService.close(
                eq(COMPANY_ID),
                eq(ASSIGNMENT_ID),
                any()
        )).willReturn(closedResponse);

        mockMvc.perform(patch(
                        "/api/companies/{companyId}/roles/{assignmentId}/close",
                        COMPANY_ID,
                        ASSIGNMENT_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "effectiveTo": "2026-12-31"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(ASSIGNMENT_ID))
                .andExpect(jsonPath("$.data.effectiveTo")
                        .value("2026-12-31"));

        then(companyRoleAssignmentService).should()
                .close(eq(COMPANY_ID), eq(ASSIGNMENT_ID), any());
    }

    /**
     * 인증 테스트
     */
    @Test
    @DisplayName("인증되지 않은 사용자는 회사 정보를 조회할 수 없다.")
    void getCompany_unauthorized() throws Exception {

        mockMvc.perform(get(
                        "/api/companies/{companyId}",
                        COMPANY_ID
                ))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));

        then(companyService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("인증되지 않은 사용자는 회사를 등록할 수 없다.")
    void createCompany_unauthorized() throws Exception {

        mockMvc.perform(post("/api/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "companyCode": "TEST-COMPANY",
                                    "companyName": "테스트 회사",
                                    "countryCode": "KOR"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));

        then(companyService).shouldHaveNoInteractions();
    }
}