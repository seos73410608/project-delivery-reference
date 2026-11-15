package com.seos.pmis.requirement.controller;

import com.seos.pmis.auth.security.filter.JwtAuthenticationFilter;
import com.seos.pmis.auth.security.handler.JwtAccessDeniedHandler;
import com.seos.pmis.auth.security.handler.JwtAuthenticationEntryPoint;
import com.seos.pmis.auth.security.jwt.JwtProvider;
import com.seos.pmis.auth.service.CustomUserDetailsService;
import com.seos.pmis.config.security.SecurityConfig;
import com.seos.pmis.requirement.dto.request.OperationalScopeCreateRequest;
import com.seos.pmis.requirement.dto.response.OperationalScopeResponse;
import com.seos.pmis.requirement.entity.OperationalScopeStatus;
import com.seos.pmis.requirement.entity.OperationalScopeType;
import com.seos.pmis.requirement.service.OperationalScopeCreationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OperationalScopeController.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        JwtAccessDeniedHandler.class,
        JwtAuthenticationEntryPoint.class
})
class OperationalScopeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OperationalScopeCreationService operationalScopeCreationService;

    @MockitoBean
    private JwtProvider jwtProvider;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_ADMIN은_OperationalScope를_생성할_수_있다() throws Exception {

        given(operationalScopeCreationService.create(
                any(OperationalScopeCreateRequest.class)
        )).willReturn(createResponse());

        String request = """
                {
                    "scopeName": "금융결제 운영 데이터베이스",
                    "scopeType": "DATABASE",
                    "ownerId": 10,
                    "description": "금융결제 서비스 운영 DB 범위",
                    "effectiveFrom": "2026-11-01"
                }
                """;

        mockMvc.perform(post("/api/operational-scopes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.message")
                        .value("Operational Scope가 생성되었습니다."))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.scopeCode").value("OPS-000001"))
                .andExpect(jsonPath("$.data.scopeName")
                        .value("금융결제 운영 데이터베이스"))
                .andExpect(jsonPath("$.data.scopeType").value("DATABASE"))
                .andExpect(jsonPath("$.data.status").value("PLANNED"));

        then(operationalScopeCreationService).should()
                .create(any(OperationalScopeCreateRequest.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void create_USER는_OperationalScope를_생성할_수_없다() throws Exception {

        String request = """
                {
                    "scopeName": "운영 시스템",
                    "scopeType": "SYSTEM"
                }
                """;

        mockMvc.perform(post("/api/operational-scopes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false));

        then(operationalScopeCreationService).should(never())
                .create(any(OperationalScopeCreateRequest.class));
    }

    @Test
    @WithMockUser(roles = "PM")
    void create_PM은_ADMIN_권한이_없어_생성할_수_없다() throws Exception {

        String request = """
                {
                    "scopeName": "운영 시스템",
                    "scopeType": "SYSTEM"
                }
                """;

        mockMvc.perform(post("/api/operational-scopes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isForbidden());

        then(operationalScopeCreationService).should(never())
                .create(any(OperationalScopeCreateRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_scopeName이_누락되면_400을_반환한다() throws Exception {

        String request = """
                {
                    "scopeType": "SYSTEM"
                }
                """;

        mockMvc.perform(post("/api/operational-scopes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        then(operationalScopeCreationService).should(never())
                .create(any(OperationalScopeCreateRequest.class));
    }

    @Test
    void create_인증되지_않은_사용자는_생성할_수_없다() throws Exception {

        String request = """
                {
                    "scopeName": "운영 시스템",
                    "scopeType": "SYSTEM"
                }
                """;

        mockMvc.perform(post("/api/operational-scopes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));

        then(operationalScopeCreationService).should(never())
                .create(any(OperationalScopeCreateRequest.class));
    }

    private OperationalScopeResponse createResponse() {
        return OperationalScopeResponse.builder()
                .id(1L)
                .scopeCode("OPS-000001")
                .scopeName("금융결제 운영 데이터베이스")
                .scopeType(OperationalScopeType.DATABASE)
                .status(OperationalScopeStatus.PLANNED)
                .ownerId(10L)
                .ownerUsername("manager")
                .ownerName("담당자")
                .description("금융결제 서비스 운영 DB 범위")
                .effectiveFrom(LocalDate.of(2026, 11, 1))
                .build();
    }
}