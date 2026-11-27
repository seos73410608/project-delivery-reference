package com.seos.pmis.company.controller;

import com.seos.pmis.auth.security.filter.JwtAuthenticationFilter;
import com.seos.pmis.auth.security.handler.JwtAccessDeniedHandler;
import com.seos.pmis.auth.security.handler.JwtAuthenticationEntryPoint;
import com.seos.pmis.auth.security.jwt.JwtProvider;
import com.seos.pmis.auth.service.CustomUserDetailsService;
import com.seos.pmis.auth.service.UserSessionService;
import com.seos.pmis.company.dto.request.CreateOrganizationRequest;
import com.seos.pmis.company.dto.request.UpdateOrganizationRequest;
import com.seos.pmis.company.dto.response.OrganizationResponse;
import com.seos.pmis.company.entity.OrganizationStatus;
import com.seos.pmis.company.entity.OrganizationType;
import com.seos.pmis.company.service.OrganizationService;
import com.seos.pmis.config.security.SecurityConfig;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrganizationController.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        JwtAccessDeniedHandler.class,
        JwtAuthenticationEntryPoint.class
})
class OrganizationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrganizationService organizationService;

    @MockitoBean
    private JwtProvider jwtProvider;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @MockitoBean
    private UserSessionService userSessionService;

    private static final Long COMPANY_ID = 1L;
    private static final Long ORGANIZATION_ID = 10L;

    private OrganizationResponse createResponse() {
        return OrganizationResponse.builder()
                .id(ORGANIZATION_ID)
                .companyId(COMPANY_ID)
                .companyCode("TEST-COMPANY")
                .companyName("테스트 회사")
                .organizationCode("DEV")
                .organizationName("개발팀")
                .organizationType(OrganizationType.TEAM)
                .parentOrganizationId(2L)
                .parentOrganizationName("개발본부")
                .managerUserId(100L)
                .managerName("홍길동")
                .status(OrganizationStatus.ACTIVE)
                .effectiveFrom(LocalDate.of(2026, 11, 27))
                .description("개발 조직")
                .createdAt(LocalDateTime.of(2026, 11, 27, 9, 0))
                .updatedAt(LocalDateTime.of(2026, 11, 27, 9, 0))
                .build();
    }

    /**
     * 조직 목록 조회
     */
    @Test
    @DisplayName("USER는 회사의 조직 목록을 조회할 수 있다.")
    @WithMockUser(roles = "USER")
    void getOrganizations_success() throws Exception {

        given(organizationService.getOrganizations(COMPANY_ID))
                .willReturn(List.of(createResponse()));

        mockMvc.perform(get(
                        "/api/companies/{companyId}/organizations",
                        COMPANY_ID
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(ORGANIZATION_ID))
                .andExpect(jsonPath("$.data[0].organizationCode").value("DEV"))
                .andExpect(jsonPath("$.data[0].organizationName").value("개발팀"))
                .andExpect(jsonPath("$.data[0].organizationType").value("TEAM"))
                .andExpect(jsonPath("$.data[0].status").value("ACTIVE"));

        then(organizationService).should()
                .getOrganizations(COMPANY_ID);
    }

    @Test
    @DisplayName("조직이 없으면 빈 목록을 반환한다.")
    @WithMockUser(roles = "USER")
    void getOrganizations_empty() throws Exception {

        given(organizationService.getOrganizations(COMPANY_ID))
                .willReturn(List.of());

        mockMvc.perform(get(
                        "/api/companies/{companyId}/organizations",
                        COMPANY_ID
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(0));

        then(organizationService).should()
                .getOrganizations(COMPANY_ID);
    }

    /**
     * 조직 상세 조회
     */
    @Test
    @DisplayName("USER는 조직 상세 정보를 조회할 수 있다.")
    @WithMockUser(roles = "USER")
    void getOrganization_success() throws Exception {

        given(organizationService.get(COMPANY_ID, ORGANIZATION_ID))
                .willReturn(createResponse());

        mockMvc.perform(get(
                        "/api/companies/{companyId}/organizations/{organizationId}",
                        COMPANY_ID,
                        ORGANIZATION_ID
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(ORGANIZATION_ID))
                .andExpect(jsonPath("$.data.companyId").value(COMPANY_ID))
                .andExpect(jsonPath("$.data.organizationCode").value("DEV"))
                .andExpect(jsonPath("$.data.organizationName").value("개발팀"))
                .andExpect(jsonPath("$.data.parentOrganizationName").value("개발본부"))
                .andExpect(jsonPath("$.data.managerName").value("홍길동"));

        then(organizationService).should()
                .get(COMPANY_ID, ORGANIZATION_ID);
    }

    /**
     * 조직 등록
     */
    @Test
    @DisplayName("ADMIN은 조직을 등록할 수 있다.")
    @WithMockUser(roles = "ADMIN")
    void createOrganization_success() throws Exception {

        given(organizationService.create(
                eq(COMPANY_ID),
                any(CreateOrganizationRequest.class)
        )).willReturn(createResponse());

        mockMvc.perform(post(
                        "/api/companies/{companyId}/organizations",
                        COMPANY_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "organizationCode": "DEV",
                                    "organizationName": "개발팀",
                                    "organizationType": "TEAM",
                                    "parentOrganizationId": 2,
                                    "managerUserId": 100,
                                    "effectiveFrom": "2026-11-27",
                                    "description": "개발 조직"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(ORGANIZATION_ID))
                .andExpect(jsonPath("$.data.organizationCode").value("DEV"))
                .andExpect(jsonPath("$.data.organizationName").value("개발팀"))
                .andExpect(jsonPath("$.data.organizationType").value("TEAM"));

        then(organizationService).should()
                .create(eq(COMPANY_ID), any(CreateOrganizationRequest.class));
    }

    @Test
    @DisplayName("USER는 조직을 등록할 수 없다.")
    @WithMockUser(roles = "USER")
    void createOrganization_forbidden() throws Exception {

        mockMvc.perform(post(
                        "/api/companies/{companyId}/organizations",
                        COMPANY_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "organizationCode": "DEV",
                                    "organizationName": "개발팀",
                                    "organizationType": "TEAM"
                                }
                                """))
                .andExpect(status().isForbidden());

        then(organizationService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("조직 코드가 없으면 등록할 수 없다.")
    @WithMockUser(roles = "ADMIN")
    void createOrganization_invalidOrganizationCode() throws Exception {

        mockMvc.perform(post(
                        "/api/companies/{companyId}/organizations",
                        COMPANY_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "organizationName": "개발팀",
                                    "organizationType": "TEAM"
                                }
                                """))
                .andExpect(status().isBadRequest());

        then(organizationService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("조직명이 없으면 등록할 수 없다.")
    @WithMockUser(roles = "ADMIN")
    void createOrganization_invalidOrganizationName() throws Exception {

        mockMvc.perform(post(
                        "/api/companies/{companyId}/organizations",
                        COMPANY_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "organizationCode": "DEV",
                                    "organizationType": "TEAM"
                                }
                                """))
                .andExpect(status().isBadRequest());

        then(organizationService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("조직 유형이 없으면 등록할 수 없다.")
    @WithMockUser(roles = "ADMIN")
    void createOrganization_invalidOrganizationType() throws Exception {

        mockMvc.perform(post(
                        "/api/companies/{companyId}/organizations",
                        COMPANY_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "organizationCode": "DEV",
                                    "organizationName": "개발팀"
                                }
                                """))
                .andExpect(status().isBadRequest());

        then(organizationService).shouldHaveNoInteractions();
    }

    /**
     * 조직 수정
     */
    @Test
    @DisplayName("ADMIN은 조직 정보를 수정할 수 있다.")
    @WithMockUser(roles = "ADMIN")
    void updateOrganization_success() throws Exception {

        given(organizationService.update(
                eq(COMPANY_ID),
                eq(ORGANIZATION_ID),
                any(UpdateOrganizationRequest.class)
        )).willReturn(createResponse());

        mockMvc.perform(put(
                        "/api/companies/{companyId}/organizations/{organizationId}",
                        COMPANY_ID,
                        ORGANIZATION_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "organizationName": "개발팀",
                                    "organizationType": "TEAM",
                                    "parentOrganizationId": 2,
                                    "managerUserId": 100,
                                    "effectiveFrom": "2026-11-27",
                                    "description": "개발 조직"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(ORGANIZATION_ID))
                .andExpect(jsonPath("$.data.organizationName").value("개발팀"));

        then(organizationService).should()
                .update(
                        eq(COMPANY_ID),
                        eq(ORGANIZATION_ID),
                        any(UpdateOrganizationRequest.class)
                );
    }

    @Test
    @DisplayName("USER는 조직 정보를 수정할 수 없다.")
    @WithMockUser(roles = "USER")
    void updateOrganization_forbidden() throws Exception {

        mockMvc.perform(put(
                        "/api/companies/{companyId}/organizations/{organizationId}",
                        COMPANY_ID,
                        ORGANIZATION_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "organizationName": "개발팀",
                                    "organizationType": "TEAM"
                                }
                                """))
                .andExpect(status().isForbidden());

        then(organizationService).should(never())
                .update(
                        eq(COMPANY_ID),
                        eq(ORGANIZATION_ID),
                        any(UpdateOrganizationRequest.class)
                );
    }

    @Test
    @DisplayName("조직명이 없으면 수정할 수 없다.")
    @WithMockUser(roles = "ADMIN")
    void updateOrganization_invalidOrganizationName() throws Exception {

        mockMvc.perform(put(
                        "/api/companies/{companyId}/organizations/{organizationId}",
                        COMPANY_ID,
                        ORGANIZATION_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "organizationType": "TEAM"
                                }
                                """))
                .andExpect(status().isBadRequest());

        then(organizationService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("조직 유형이 없으면 수정할 수 없다.")
    @WithMockUser(roles = "ADMIN")
    void updateOrganization_invalidOrganizationType() throws Exception {

        mockMvc.perform(put(
                        "/api/companies/{companyId}/organizations/{organizationId}",
                        COMPANY_ID,
                        ORGANIZATION_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "organizationName": "개발팀"
                                }
                                """))
                .andExpect(status().isBadRequest());

        then(organizationService).shouldHaveNoInteractions();
    }

    /**
     * 조직 상태 변경
     */
    @Test
    @DisplayName("ADMIN은 조직 상태를 변경할 수 있다.")
    @WithMockUser(roles = "ADMIN")
    void changeOrganizationStatus_success() throws Exception {

        OrganizationResponse inactiveResponse =
                OrganizationResponse.builder()
                        .id(ORGANIZATION_ID)
                        .companyId(COMPANY_ID)
                        .organizationCode("DEV")
                        .organizationName("개발팀")
                        .organizationType(OrganizationType.TEAM)
                        .status(OrganizationStatus.INACTIVE)
                        .build();

        given(organizationService.changeStatus(
                COMPANY_ID,
                ORGANIZATION_ID,
                OrganizationStatus.INACTIVE
        )).willReturn(inactiveResponse);

        mockMvc.perform(patch(
                        "/api/companies/{companyId}/organizations/{organizationId}/status",
                        COMPANY_ID,
                        ORGANIZATION_ID
                )
                        .param("status", "INACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("INACTIVE"));

        then(organizationService).should()
                .changeStatus(
                        COMPANY_ID,
                        ORGANIZATION_ID,
                        OrganizationStatus.INACTIVE
                );
    }

    @Test
    @DisplayName("USER는 조직 상태를 변경할 수 없다.")
    @WithMockUser(roles = "USER")
    void changeOrganizationStatus_forbidden() throws Exception {

        mockMvc.perform(patch(
                        "/api/companies/{companyId}/organizations/{organizationId}/status",
                        COMPANY_ID,
                        ORGANIZATION_ID
                )
                        .param("status", "INACTIVE"))
                .andExpect(status().isForbidden());

        then(organizationService).should(never())
                .changeStatus(
                        COMPANY_ID,
                        ORGANIZATION_ID,
                        OrganizationStatus.INACTIVE
                );
    }

    @Test
    @DisplayName("잘못된 조직 상태는 400을 반환한다.")
    @WithMockUser(roles = "ADMIN")
    void changeOrganizationStatus_invalidStatus() throws Exception {

        mockMvc.perform(patch(
                        "/api/companies/{companyId}/organizations/{organizationId}/status",
                        COMPANY_ID,
                        ORGANIZATION_ID
                )
                        .param("status", "UNKNOWN"))
                .andExpect(status().isBadRequest());

        then(organizationService).shouldHaveNoInteractions();
    }

    /**
     * 인증 테스트
     */
    @Test
    @DisplayName("미인증 사용자는 조직 목록을 조회할 수 없다.")
    void getOrganizations_unauthorized() throws Exception {

        mockMvc.perform(get(
                        "/api/companies/{companyId}/organizations",
                        COMPANY_ID
                ))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));

        then(organizationService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("미인증 사용자는 조직을 등록할 수 없다.")
    void createOrganization_unauthorized() throws Exception {

        mockMvc.perform(post(
                        "/api/companies/{companyId}/organizations",
                        COMPANY_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "organizationCode": "DEV",
                                    "organizationName": "개발팀",
                                    "organizationType": "TEAM"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));

        then(organizationService).shouldHaveNoInteractions();
    }
}