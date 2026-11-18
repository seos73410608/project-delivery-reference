
package com.seos.pmis.requirement.controller;

import com.seos.pmis.auth.service.UserSessionService;
import com.seos.common.exception.BusinessException;
import com.seos.cmdb.ci.exception.ConfigurationItemErrorCode;
import com.seos.pmis.auth.security.filter.JwtAuthenticationFilter;
import com.seos.pmis.auth.security.handler.JwtAccessDeniedHandler;
import com.seos.pmis.auth.security.handler.JwtAuthenticationEntryPoint;
import com.seos.pmis.auth.security.jwt.JwtProvider;
import com.seos.pmis.auth.service.CustomUserDetailsService;
import com.seos.pmis.config.security.SecurityConfig;
import com.seos.pmis.requirement.dto.response.OperationalScopeCiResponse;
import com.seos.pmis.requirement.exception.OperationalScopeCiErrorCode;
import com.seos.pmis.requirement.exception.OperationalScopeErrorCode;
import com.seos.pmis.requirement.service.OperationalScopeCiService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OperationalScopeCiController.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        JwtAccessDeniedHandler.class,
        JwtAuthenticationEntryPoint.class
})
class OperationalScopeCiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OperationalScopeCiService operationalScopeCiService;

    @MockitoBean
    private JwtProvider jwtProvider;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @MockitoBean
    private UserSessionService userSessionService;

    private static final Long SCOPE_ID = 1L;
    private static final Long CI_ID = 100L;

    private OperationalScopeCiResponse createResponse() {
        return OperationalScopeCiResponse.builder()
                .id(1L)
                .scopeId(SCOPE_ID)
                .ciId(CI_ID)
                .ciCode("CI-SERVER-001")
                .ciName("금융결제 업무 서버")
                .ciTypeId(10L)
                .ciTypeCode("SERVER")
                .ciTypeName("서버")
                .createdAt(LocalDateTime.of(2026, 11, 16, 10, 0))
                .updatedAt(LocalDateTime.of(2026, 11, 16, 10, 0))
                .build();
    }

    /**
     * Operational Scope-CI 연결 테스트
     */
    @Test
    @DisplayName("ADMIN은 Operational Scope에 CI를 연결할 수 있다.")
    @WithMockUser(roles = "ADMIN")
    void connect_success() throws Exception {

        given(operationalScopeCiService.connect(SCOPE_ID, CI_ID))
                .willReturn(createResponse());

        mockMvc.perform(post(
                        "/api/operational-scopes/{scopeId}/cis/{ciId}",
                        SCOPE_ID,
                        CI_ID
                )
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.scopeId").value(1))
                .andExpect(jsonPath("$.data.ciId").value(100))
                .andExpect(jsonPath("$.data.ciCode")
                        .value("CI-SERVER-001"))
                .andExpect(jsonPath("$.data.ciName")
                        .value("금융결제 업무 서버"));

        then(operationalScopeCiService).should()
                .connect(SCOPE_ID, CI_ID);
    }

    @Test
    @DisplayName("USER는 Operational Scope에 CI를 연결할 수 없다.")
    @WithMockUser(roles = "USER")
    void connect_forbidden() throws Exception {

        mockMvc.perform(post(
                        "/api/operational-scopes/{scopeId}/cis/{ciId}",
                        SCOPE_ID,
                        CI_ID
                )
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false));

        then(operationalScopeCiService).should(never())
                .connect(SCOPE_ID, CI_ID);
    }

    @Test
    @DisplayName("존재하지 않는 Operational Scope에 CI를 연결하면 404를 반환한다.")
    @WithMockUser(roles = "ADMIN")
    void connect_scopeNotFound() throws Exception {

        given(operationalScopeCiService.connect(SCOPE_ID, CI_ID))
                .willThrow(new BusinessException(
                        OperationalScopeErrorCode.OPERATIONAL_SCOPE_NOT_FOUND
                ));

        mockMvc.perform(post(
                        "/api/operational-scopes/{scopeId}/cis/{ciId}",
                        SCOPE_ID,
                        CI_ID
                ))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code")
                        .value("PMIS_OPERATIONAL_SCOPE_005"));

        then(operationalScopeCiService).should()
                .connect(SCOPE_ID, CI_ID);
    }

    @Test
    @DisplayName("존재하지 않는 CI를 연결하면 404를 반환한다.")
    @WithMockUser(roles = "ADMIN")
    void connect_ciNotFound() throws Exception {

        given(operationalScopeCiService.connect(SCOPE_ID, CI_ID))
                .willThrow(new BusinessException(
                        ConfigurationItemErrorCode.CI_NOT_FOUND
                ));

        mockMvc.perform(post(
                        "/api/operational-scopes/{scopeId}/cis/{ciId}",
                        SCOPE_ID,
                        CI_ID
                ))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("CMDB_CI_001"));

        then(operationalScopeCiService).should()
                .connect(SCOPE_ID, CI_ID);
    }

    @Test
    @DisplayName("이미 연결된 CI를 다시 연결하면 409를 반환한다.")
    @WithMockUser(roles = "ADMIN")
    void connect_alreadyExists() throws Exception {

        given(operationalScopeCiService.connect(SCOPE_ID, CI_ID))
                .willThrow(new BusinessException(
                        OperationalScopeCiErrorCode.OPERATIONAL_SCOPE_CI_ALREADY_EXISTS
                ));

        mockMvc.perform(post(
                        "/api/operational-scopes/{scopeId}/cis/{ciId}",
                        SCOPE_ID,
                        CI_ID
                ))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code")
                        .value("PMIS_OPERATIONAL_SCOPE_CI_002"));

        then(operationalScopeCiService).should()
                .connect(SCOPE_ID, CI_ID);
    }

    /**
     * Operational Scope-CI 목록 조회 테스트
     */
    @Test
    @DisplayName("USER는 Operational Scope에 연결된 CI 목록을 조회할 수 있다.")
    @WithMockUser(roles = "USER")
    void getAll_success() throws Exception {

        given(operationalScopeCiService.getAllByScopeId(SCOPE_ID))
                .willReturn(List.of(createResponse()));

        mockMvc.perform(get(
                        "/api/operational-scopes/{scopeId}/cis",
                        SCOPE_ID
                )
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].scopeId").value(1))
                .andExpect(jsonPath("$.data[0].ciId").value(100))
                .andExpect(jsonPath("$.data[0].ciCode")
                        .value("CI-SERVER-001"));

        then(operationalScopeCiService).should()
                .getAllByScopeId(SCOPE_ID);
    }

    @Test
    @DisplayName("연결된 CI가 없으면 빈 목록을 반환한다.")
    @WithMockUser(roles = "USER")
    void getAll_empty() throws Exception {

        given(operationalScopeCiService.getAllByScopeId(SCOPE_ID))
                .willReturn(List.of());

        mockMvc.perform(get(
                        "/api/operational-scopes/{scopeId}/cis",
                        SCOPE_ID
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(0));

        then(operationalScopeCiService).should()
                .getAllByScopeId(SCOPE_ID);
    }

    @Test
    @DisplayName("존재하지 않는 Scope의 CI 목록을 조회하면 404를 반환한다.")
    @WithMockUser(roles = "USER")
    void getAll_scopeNotFound() throws Exception {

        given(operationalScopeCiService.getAllByScopeId(SCOPE_ID))
                .willThrow(new BusinessException(
                        OperationalScopeErrorCode.OPERATIONAL_SCOPE_NOT_FOUND
                ));

        mockMvc.perform(get(
                        "/api/operational-scopes/{scopeId}/cis",
                        SCOPE_ID
                ))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code")
                        .value("PMIS_OPERATIONAL_SCOPE_005"));

        then(operationalScopeCiService).should()
                .getAllByScopeId(SCOPE_ID);
    }

    /**
     * Operational Scope-CI 상세 조회 테스트
     */
    @Test
    @DisplayName("USER는 Operational Scope와 CI의 연결 상세 정보를 조회할 수 있다.")
    @WithMockUser(roles = "USER")
    void getDetail_success() throws Exception {

        given(operationalScopeCiService.getByScopeIdAndCiId(
                SCOPE_ID,
                CI_ID
        )).willReturn(createResponse());

        mockMvc.perform(get(
                        "/api/operational-scopes/{scopeId}/cis/{ciId}",
                        SCOPE_ID,
                        CI_ID
                )
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.scopeId").value(1))
                .andExpect(jsonPath("$.data.ciId").value(100))
                .andExpect(jsonPath("$.data.ciTypeCode").value("SERVER"));

        then(operationalScopeCiService).should()
                .getByScopeIdAndCiId(SCOPE_ID, CI_ID);
    }

    @Test
    @DisplayName("연결 정보가 없으면 404를 반환한다.")
    @WithMockUser(roles = "USER")
    void getDetail_notFound() throws Exception {

        given(operationalScopeCiService.getByScopeIdAndCiId(
                SCOPE_ID,
                CI_ID
        )).willThrow(new BusinessException(
                OperationalScopeCiErrorCode.OPERATIONAL_SCOPE_CI_NOT_FOUND
        ));

        mockMvc.perform(get(
                        "/api/operational-scopes/{scopeId}/cis/{ciId}",
                        SCOPE_ID,
                        CI_ID
                ))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code")
                        .value("PMIS_OPERATIONAL_SCOPE_CI_001"));

        then(operationalScopeCiService).should()
                .getByScopeIdAndCiId(SCOPE_ID, CI_ID);
    }

    /**
     * Operational Scope-CI 연결 해제 테스트
     */
    @Test
    @DisplayName("ADMIN은 Operational Scope와 CI의 연결을 해제할 수 있다.")
    @WithMockUser(roles = "ADMIN")
    void disconnect_success() throws Exception {

        mockMvc.perform(delete(
                        "/api/operational-scopes/{scopeId}/cis/{ciId}",
                        SCOPE_ID,
                        CI_ID
                )
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.message")
                        .value("Configuration Item 연결이 해제되었습니다."));

        then(operationalScopeCiService).should()
                .disconnect(SCOPE_ID, CI_ID);
    }

    @Test
    @DisplayName("USER는 Operational Scope와 CI의 연결을 해제할 수 없다.")
    @WithMockUser(roles = "USER")
    void disconnect_forbidden() throws Exception {

        mockMvc.perform(delete(
                        "/api/operational-scopes/{scopeId}/cis/{ciId}",
                        SCOPE_ID,
                        CI_ID
                ))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false));

        then(operationalScopeCiService).should(never())
                .disconnect(SCOPE_ID, CI_ID);
    }

    @Test
    @DisplayName("연결 정보가 없으면 연결 해제 시 404를 반환한다.")
    @WithMockUser(roles = "ADMIN")
    void disconnect_notFound() throws Exception {

        doThrow(new BusinessException(
                OperationalScopeCiErrorCode.OPERATIONAL_SCOPE_CI_NOT_FOUND
        )).when(operationalScopeCiService)
                .disconnect(SCOPE_ID, CI_ID);

        mockMvc.perform(delete(
                        "/api/operational-scopes/{scopeId}/cis/{ciId}",
                        SCOPE_ID,
                        CI_ID
                ))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code")
                        .value("PMIS_OPERATIONAL_SCOPE_CI_001"));

        then(operationalScopeCiService).should()
                .disconnect(SCOPE_ID, CI_ID);
    }

    /**
     * 인증 테스트
     */
    @Test
    @DisplayName("인증되지 않은 사용자는 CI 목록을 조회할 수 없다.")
    void getAll_unauthorized() throws Exception {

        mockMvc.perform(get(
                        "/api/operational-scopes/{scopeId}/cis",
                        SCOPE_ID
                ))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));

        then(operationalScopeCiService).should(never())
                .getAllByScopeId(SCOPE_ID);
    }

    @Test
    @DisplayName("인증되지 않은 사용자는 CI를 연결할 수 없다.")
    void connect_unauthorized() throws Exception {

        mockMvc.perform(post(
                        "/api/operational-scopes/{scopeId}/cis/{ciId}",
                        SCOPE_ID,
                        CI_ID
                ))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));

        then(operationalScopeCiService).should(never())
                .connect(SCOPE_ID, CI_ID);
    }
}
