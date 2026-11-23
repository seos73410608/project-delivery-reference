
package com.seos.pmis.operationscope.controller;

import com.seos.common.exception.BusinessException;
import com.seos.pmis.auth.security.filter.JwtAuthenticationFilter;
import com.seos.pmis.auth.security.handler.JwtAccessDeniedHandler;
import com.seos.pmis.auth.security.handler.JwtAuthenticationEntryPoint;
import com.seos.pmis.auth.security.jwt.JwtProvider;
import com.seos.pmis.auth.service.CustomUserDetailsService;
import com.seos.pmis.auth.service.UserSessionService;
import com.seos.pmis.config.security.SecurityConfig;
import com.seos.pmis.operation.entity.OperationStatus;
import com.seos.pmis.operation.entity.OperationType;
import com.seos.pmis.operationscope.dto.response.OperationScopeResponse;
import com.seos.pmis.operationscope.entity.OperationScopeStatus;
import com.seos.pmis.operationscope.exception.OperationScopeErrorCode;
import com.seos.pmis.operationscope.service.OperationScopeService;
import com.seos.pmis.requirement.entity.OperationalScopeStatus;
import com.seos.pmis.requirement.entity.OperationalScopeType;
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

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OperationScopeController.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        JwtAccessDeniedHandler.class,
        JwtAuthenticationEntryPoint.class
})
class OperationScopeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OperationScopeService operationScopeService;

    @MockitoBean
    private JwtProvider jwtProvider;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @MockitoBean
    private UserSessionService userSessionService;

    private static final Long OPERATION_SCOPE_ID = 1L;
    private static final Long OPERATION_ID = 100L;
    private static final Long OPERATIONAL_SCOPE_ID = 200L;

    private OperationScopeResponse createResponse() {
        return OperationScopeResponse.builder()
                .id(OPERATION_SCOPE_ID)
                .status(OperationScopeStatus.ACTIVE)
                .operationId(OPERATION_ID)
                .operationCode("OP-001")
                .operationName("통합 운영 서비스")
                .operationType(OperationType.INFRASTRUCTURE_SERVICE)
                .operationStatus(OperationStatus.ACTIVE)
                .operationalScopeId(OPERATIONAL_SCOPE_ID)
                .scopeCode("SCP-001")
                .scopeName("운영 인프라")
                .scopeType(OperationalScopeType.SERVER_GROUP)
                .operationalScopeStatus(OperationalScopeStatus.ACTIVE)
                .effectiveFrom(LocalDate.of(2026, 1, 1))
                .effectiveTo(LocalDate.of(2026, 12, 31))
                .description("초기 운영 범위 관계")
                .createdAt(LocalDateTime.of(2026, 11, 23, 9, 0))
                .updatedAt(LocalDateTime.of(2026, 11, 23, 9, 0))
                .build();
    }

    /**
     * 운영 서비스-운영 범위 관계 생성 테스트
     */
    @Test
    @DisplayName("ADMIN은 운영 서비스와 운영 범위를 연결할 수 있다.")
    @WithMockUser(roles = "ADMIN")
    void create_success() throws Exception {

        given(operationScopeService.createResponse(
                OPERATION_ID,
                OPERATIONAL_SCOPE_ID,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31),
                "초기 운영 범위 관계"
        )).willReturn(createResponse());

        mockMvc.perform(post("/api/operation-scopes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "operationId": 100,
                                    "operationalScopeId": 200,
                                    "effectiveFrom": "2026-01-01",
                                    "effectiveTo": "2026-12-31",
                                    "description": "초기 운영 범위 관계"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.operationId").value(100))
                .andExpect(jsonPath("$.data.operationalScopeId").value(200))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));

        then(operationScopeService).should()
                .createResponse(
                        OPERATION_ID,
                        OPERATIONAL_SCOPE_ID,
                        LocalDate.of(2026, 1, 1),
                        LocalDate.of(2026, 12, 31),
                        "초기 운영 범위 관계"
                );
    }

    @Test
    @DisplayName("USER는 운영 서비스와 운영 범위를 연결할 수 없다.")
    @WithMockUser(roles = "USER")
    void create_forbidden() throws Exception {

        mockMvc.perform(post("/api/operation-scopes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "operationId": 100,
                                    "operationalScopeId": 200
                                }
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false));

        then(operationScopeService).should(never())
                .createResponse(
                        OPERATION_ID,
                        OPERATIONAL_SCOPE_ID,
                        null,
                        null,
                        null
                );
    }

    @Test
    @DisplayName("운영 서비스 ID가 없으면 관계를 생성할 수 없다.")
    @WithMockUser(roles = "ADMIN")
    void create_invalidOperationId() throws Exception {

        mockMvc.perform(post("/api/operation-scopes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "operationalScopeId": 200
                                }
                                """))
                .andExpect(status().isBadRequest());

        then(operationScopeService).shouldHaveNoInteractions();
    }

    /**
     * 운영 범위 관계 상세 조회 테스트
     */
    @Test
    @DisplayName("USER는 운영 범위 관계 상세 정보를 조회할 수 있다.")
    @WithMockUser(roles = "USER")
    void getDetail_success() throws Exception {

        given(operationScopeService.getDetailResponse(OPERATION_SCOPE_ID))
                .willReturn(createResponse());

        mockMvc.perform(get(
                        "/api/operation-scopes/{id}",
                        OPERATION_SCOPE_ID
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.operationCode").value("OP-001"))
                .andExpect(jsonPath("$.data.scopeCode").value("SCP-001"));

        then(operationScopeService).should()
                .getDetailResponse(OPERATION_SCOPE_ID);
    }

    @Test
    @DisplayName("존재하지 않는 관계를 조회하면 404를 반환한다.")
    @WithMockUser(roles = "USER")
    void getDetail_notFound() throws Exception {

        given(operationScopeService.getDetailResponse(OPERATION_SCOPE_ID))
                .willThrow(new BusinessException(
                        OperationScopeErrorCode.OPERATION_SCOPE_NOT_FOUND
                ));

        mockMvc.perform(get(
                        "/api/operation-scopes/{id}",
                        OPERATION_SCOPE_ID
                ))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code")
                        .value("PMIS_OPERATION_SCOPE_003"));

        then(operationScopeService).should()
                .getDetailResponse(OPERATION_SCOPE_ID);
    }

    /**
     * 운영 서비스 기준 관계 조회 테스트
     */
    @Test
    @DisplayName("USER는 운영 서비스에 연결된 운영 범위 목록을 조회할 수 있다.")
    @WithMockUser(roles = "USER")
    void findByOperation_success() throws Exception {

        given(operationScopeService.findResponsesByOperation(OPERATION_ID))
                .willReturn(List.of(createResponse()));

        mockMvc.perform(get(
                        "/api/operation-scopes/by-operation/{operationId}",
                        OPERATION_ID
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].operationId").value(100))
                .andExpect(jsonPath("$.data[0].operationalScopeId").value(200));

        then(operationScopeService).should()
                .findResponsesByOperation(OPERATION_ID);
    }

    @Test
    @DisplayName("연결된 운영 범위가 없으면 빈 목록을 반환한다.")
    @WithMockUser(roles = "USER")
    void findByOperation_empty() throws Exception {

        given(operationScopeService.findResponsesByOperation(OPERATION_ID))
                .willReturn(List.of());

        mockMvc.perform(get(
                        "/api/operation-scopes/by-operation/{operationId}",
                        OPERATION_ID
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(0));

        then(operationScopeService).should()
                .findResponsesByOperation(OPERATION_ID);
    }

    /**
     * 운영 범위 기준 관계 조회 테스트
     */
    @Test
    @DisplayName("USER는 운영 범위에 연결된 운영 서비스 목록을 조회할 수 있다.")
    @WithMockUser(roles = "USER")
    void findByOperationalScope_success() throws Exception {

        given(operationScopeService
                .findResponsesByOperationalScope(OPERATIONAL_SCOPE_ID))
                .willReturn(List.of(createResponse()));

        mockMvc.perform(get(
                        "/api/operation-scopes/by-operational-scope/{operationalScopeId}",
                        OPERATIONAL_SCOPE_ID
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].operationalScopeId").value(200))
                .andExpect(jsonPath("$.data[0].operationId").value(100));

        then(operationScopeService).should()
                .findResponsesByOperationalScope(OPERATIONAL_SCOPE_ID);
    }

    /**
     * 운영 범위 관계 수정 테스트
     */
    @Test
    @DisplayName("ADMIN은 운영 범위 관계의 기간과 설명을 수정할 수 있다.")
    @WithMockUser(roles = "ADMIN")
    void update_success() throws Exception {

        given(operationScopeService.updateResponse(
                OPERATION_SCOPE_ID,
                LocalDate.of(2026, 2, 1),
                LocalDate.of(2026, 12, 31),
                "운영 범위 수정"
        )).willReturn(createResponse());

        mockMvc.perform(put(
                        "/api/operation-scopes/{id}",
                        OPERATION_SCOPE_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "effectiveFrom": "2026-02-01",
                                    "effectiveTo": "2026-12-31",
                                    "description": "운영 범위 수정"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));

        then(operationScopeService).should()
                .updateResponse(
                        OPERATION_SCOPE_ID,
                        LocalDate.of(2026, 2, 1),
                        LocalDate.of(2026, 12, 31),
                        "운영 범위 수정"
                );
    }

    @Test
    @DisplayName("USER는 운영 범위 관계를 수정할 수 없다.")
    @WithMockUser(roles = "USER")
    void update_forbidden() throws Exception {

        mockMvc.perform(put(
                        "/api/operation-scopes/{id}",
                        OPERATION_SCOPE_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "description": "수정 시도"
                                }
                                """))
                .andExpect(status().isForbidden());

        then(operationScopeService).should(never())
                .updateResponse(
                        OPERATION_SCOPE_ID,
                        null,
                        null,
                        "수정 시도"
                );
    }

    /**
     * 운영 범위 관계 상태 변경 테스트
     */
    @Test
    @DisplayName("ADMIN은 운영 범위 관계 상태를 변경할 수 있다.")
    @WithMockUser(roles = "ADMIN")
    void changeStatus_success() throws Exception {

        given(operationScopeService.changeStatusResponse(
                OPERATION_SCOPE_ID,
                OperationScopeStatus.ACTIVE
        )).willReturn(createResponse());

        mockMvc.perform(patch(
                        "/api/operation-scopes/{id}/status",
                        OPERATION_SCOPE_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "status": "ACTIVE"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));

        then(operationScopeService).should()
                .changeStatusResponse(
                        OPERATION_SCOPE_ID,
                        OperationScopeStatus.ACTIVE
                );
    }

    @Test
    @DisplayName("USER는 운영 범위 관계 상태를 변경할 수 없다.")
    @WithMockUser(roles = "USER")
    void changeStatus_forbidden() throws Exception {

        mockMvc.perform(patch(
                        "/api/operation-scopes/{id}/status",
                        OPERATION_SCOPE_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "status": "ACTIVE"
                                }
                                """))
                .andExpect(status().isForbidden());

        then(operationScopeService).should(never())
                .changeStatusResponse(
                        OPERATION_SCOPE_ID,
                        OperationScopeStatus.ACTIVE
                );
    }

    @Test
    @DisplayName("변경할 상태가 없으면 400을 반환한다.")
    @WithMockUser(roles = "ADMIN")
    void changeStatus_invalidRequest() throws Exception {

        mockMvc.perform(patch(
                        "/api/operation-scopes/{id}/status",
                        OPERATION_SCOPE_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {}
                                """))
                .andExpect(status().isBadRequest());

        then(operationScopeService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("허용되지 않은 상태 전이는 400을 반환한다.")
    @WithMockUser(roles = "ADMIN")
    void changeStatus_invalidTransition() throws Exception {

        given(operationScopeService.changeStatusResponse(
                OPERATION_SCOPE_ID,
                OperationScopeStatus.PLANNED
        )).willThrow(new BusinessException(
                OperationScopeErrorCode.OPERATION_SCOPE_STATUS_INVALID
        ));

        mockMvc.perform(patch(
                        "/api/operation-scopes/{id}/status",
                        OPERATION_SCOPE_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "status": "PLANNED"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code")
                        .value("PMIS_OPERATION_SCOPE_005"));

        then(operationScopeService).should()
                .changeStatusResponse(
                        OPERATION_SCOPE_ID,
                        OperationScopeStatus.PLANNED
                );
    }

    /**
     * 인증 테스트
     */
    @Test
    @DisplayName("인증되지 않은 사용자는 운영 범위 관계를 조회할 수 없다.")
    void getDetail_unauthorized() throws Exception {

        mockMvc.perform(get(
                        "/api/operation-scopes/{id}",
                        OPERATION_SCOPE_ID
                ))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));

        then(operationScopeService).should(never())
                .getDetailResponse(OPERATION_SCOPE_ID);
    }

    @Test
    @DisplayName("인증되지 않은 사용자는 운영 범위 관계를 생성할 수 없다.")
    void create_unauthorized() throws Exception {

        mockMvc.perform(post("/api/operation-scopes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "operationId": 100,
                                    "operationalScopeId": 200
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));

        then(operationScopeService).shouldHaveNoInteractions();
    }
}
