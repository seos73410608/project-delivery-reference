
package com.seos.pmis.operation.controller;

import com.seos.common.exception.BusinessException;
import com.seos.pmis.auth.security.filter.JwtAuthenticationFilter;
import com.seos.pmis.auth.security.handler.JwtAccessDeniedHandler;
import com.seos.pmis.auth.security.handler.JwtAuthenticationEntryPoint;
import com.seos.pmis.auth.security.jwt.JwtProvider;
import com.seos.pmis.auth.service.CustomUserDetailsService;
import com.seos.pmis.auth.service.UserSessionService;
import com.seos.pmis.config.security.SecurityConfig;
import com.seos.pmis.operation.dto.response.OperationResponse;
import com.seos.pmis.operation.entity.OperationStatus;
import com.seos.pmis.operation.entity.OperationType;
import com.seos.pmis.operation.exception.OperationErrorCode;
import com.seos.pmis.operation.service.OperationService;
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

@WebMvcTest(OperationController.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        JwtAccessDeniedHandler.class,
        JwtAuthenticationEntryPoint.class
})
@DisplayName("OperationController 테스트")
class OperationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OperationService operationService;

    @MockitoBean
    private JwtProvider jwtProvider;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @MockitoBean
    private UserSessionService userSessionService;

    private static final Long OPERATION_ID = 1L;

    private OperationResponse createResponse() {
        return OperationResponse.builder()
                .id(OPERATION_ID)
                .operationCode("OP-001")
                .operationName("통합 운영 서비스")
                .operationType(OperationType.INFRASTRUCTURE_SERVICE)
                .status(OperationStatus.ACTIVE)
                .managerUserId(10L)
                .managerName("운영 담당자")
                .description("운영 서비스 설명")
                .effectiveFrom(LocalDate.of(2026, 1, 1))
                .effectiveTo(LocalDate.of(2026, 12, 31))
                .createdAt(LocalDateTime.of(2026, 11, 23, 9, 0))
                .updatedAt(LocalDateTime.of(2026, 11, 23, 9, 0))
                .build();
    }

    // ---------------------------------------------------------------------
    // Create
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("ADMIN은 운영 서비스를 생성할 수 있다.")
    @WithMockUser(roles = "ADMIN")
    void create_success() throws Exception {

        given(operationService.createResponse(
                "OP-001",
                "통합 운영 서비스",
                OperationType.INFRASTRUCTURE_SERVICE,
                10L,
                "운영 서비스 설명",
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31)
        )).willReturn(createResponse());

        mockMvc.perform(post("/api/operations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "operationCode": "OP-001",
                                    "operationName": "통합 운영 서비스",
                                    "operationType": "INFRASTRUCTURE_SERVICE",
                                    "managerUserId": 10,
                                    "description": "운영 서비스 설명",
                                    "effectiveFrom": "2026-01-01",
                                    "effectiveTo": "2026-12-31"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.operationCode").value("OP-001"))
                .andExpect(jsonPath("$.data.operationName")
                        .value("통합 운영 서비스"))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));

        then(operationService).should()
                .createResponse(
                        "OP-001",
                        "통합 운영 서비스",
                        OperationType.INFRASTRUCTURE_SERVICE,
                        10L,
                        "운영 서비스 설명",
                        LocalDate.of(2026, 1, 1),
                        LocalDate.of(2026, 12, 31)
                );
    }

    @Test
    @DisplayName("USER는 운영 서비스를 생성할 수 없다.")
    @WithMockUser(roles = "USER")
    void create_forbidden() throws Exception {

        mockMvc.perform(post("/api/operations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "operationCode": "OP-001",
                                    "operationName": "통합 운영 서비스",
                                    "operationType": "INFRASTRUCTURE_SERVICE"
                                }
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false));

        then(operationService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("필수 운영 코드가 없으면 400을 반환한다.")
    @WithMockUser(roles = "ADMIN")
    void create_invalidOperationCode() throws Exception {

        mockMvc.perform(post("/api/operations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "operationName": "통합 운영 서비스",
                                    "operationType": "INFRASTRUCTURE_SERVICE"
                                }
                                """))
                .andExpect(status().isBadRequest());

        then(operationService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("필수 운영 서비스 유형이 없으면 400을 반환한다.")
    @WithMockUser(roles = "ADMIN")
    void create_invalidOperationType() throws Exception {

        mockMvc.perform(post("/api/operations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "operationCode": "OP-001",
                                    "operationName": "통합 운영 서비스"
                                }
                                """))
                .andExpect(status().isBadRequest());

        then(operationService).shouldHaveNoInteractions();
    }

    // ---------------------------------------------------------------------
    // Read by code
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("USER는 운영 코드로 운영 서비스를 조회할 수 있다.")
    @WithMockUser(roles = "USER")
    void findByCode_success() throws Exception {

        given(operationService.findResponseByCode("OP-001"))
                .willReturn(createResponse());

        mockMvc.perform(get("/api/operations/by-code/{operationCode}", "OP-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.operationCode").value("OP-001"))
                .andExpect(jsonPath("$.data.operationName")
                        .value("통합 운영 서비스"));

        then(operationService).should().findResponseByCode("OP-001");
    }

    // ---------------------------------------------------------------------
    // Read detail
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("USER는 운영 서비스 상세 정보를 조회할 수 있다.")
    @WithMockUser(roles = "USER")
    void getDetail_success() throws Exception {

        given(operationService.getDetailResponse(OPERATION_ID))
                .willReturn(createResponse());

        mockMvc.perform(get("/api/operations/{id}", OPERATION_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.operationCode").value("OP-001"))
                .andExpect(jsonPath("$.data.managerUserId").value(10))
                .andExpect(jsonPath("$.data.managerName").value("운영 담당자"));

        then(operationService).should().getDetailResponse(OPERATION_ID);
    }

    @Test
    @DisplayName("존재하지 않는 운영 서비스를 조회하면 404를 반환한다.")
    @WithMockUser(roles = "USER")
    void getDetail_notFound() throws Exception {

        given(operationService.getDetailResponse(OPERATION_ID))
                .willThrow(new BusinessException(
                        OperationErrorCode.OPERATION_NOT_FOUND
                ));

        mockMvc.perform(get("/api/operations/{id}", OPERATION_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("PMIS_OPERATION_001"));

        then(operationService).should().getDetailResponse(OPERATION_ID);
    }

    // ---------------------------------------------------------------------
    // Read all
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("USER는 전체 운영 서비스 목록을 조회할 수 있다.")
    @WithMockUser(roles = "USER")
    void findAll_success() throws Exception {

        given(operationService.findAllResponses())
                .willReturn(List.of(createResponse()));

        mockMvc.perform(get("/api/operations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].operationCode").value("OP-001"));

        then(operationService).should().findAllResponses();
    }

    @Test
    @DisplayName("운영 서비스가 없으면 빈 목록을 반환한다.")
    @WithMockUser(roles = "USER")
    void findAll_empty() throws Exception {

        given(operationService.findAllResponses()).willReturn(List.of());

        mockMvc.perform(get("/api/operations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(0));

        then(operationService).should().findAllResponses();
    }

    // ---------------------------------------------------------------------
    // Update
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("ADMIN은 운영 서비스 정보를 수정할 수 있다.")
    @WithMockUser(roles = "ADMIN")
    void update_success() throws Exception {

        given(operationService.updateResponse(
                OPERATION_ID,
                "수정된 운영 서비스",
                OperationType.PLATFORM_SERVICE,
                10L,
                "수정된 설명",
                LocalDate.of(2026, 2, 1),
                LocalDate.of(2026, 11, 30)
        )).willReturn(createResponse());

        mockMvc.perform(put("/api/operations/{id}", OPERATION_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "operationName": "수정된 운영 서비스",
                                    "operationType": "PLATFORM_SERVICE",
                                    "managerUserId": 10,
                                    "description": "수정된 설명",
                                    "effectiveFrom": "2026-02-01",
                                    "effectiveTo": "2026-11-30"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));

        then(operationService).should()
                .updateResponse(
                        OPERATION_ID,
                        "수정된 운영 서비스",
                        OperationType.PLATFORM_SERVICE,
                        10L,
                        "수정된 설명",
                        LocalDate.of(2026, 2, 1),
                        LocalDate.of(2026, 11, 30)
                );
    }

    @Test
    @DisplayName("USER는 운영 서비스 정보를 수정할 수 없다.")
    @WithMockUser(roles = "USER")
    void update_forbidden() throws Exception {

        mockMvc.perform(put("/api/operations/{id}", OPERATION_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "operationName": "수정 시도",
                                    "operationType": "OTHER"
                                }
                                """))
                .andExpect(status().isForbidden());

        then(operationService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("수정할 운영 서비스명이 없으면 400을 반환한다.")
    @WithMockUser(roles = "ADMIN")
    void update_invalidRequest() throws Exception {

        mockMvc.perform(put("/api/operations/{id}", OPERATION_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "operationType": "OTHER"
                                }
                                """))
                .andExpect(status().isBadRequest());

        then(operationService).shouldHaveNoInteractions();
    }

    // ---------------------------------------------------------------------
    // Change status
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("ADMIN은 운영 서비스 상태를 변경할 수 있다.")
    @WithMockUser(roles = "ADMIN")
    void changeStatus_success() throws Exception {

        given(operationService.changeStatusResponse(
                OPERATION_ID,
                OperationStatus.SUSPENDED
        )).willReturn(createResponse());

        mockMvc.perform(patch("/api/operations/{id}/status", OPERATION_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "status": "SUSPENDED"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));

        then(operationService).should()
                .changeStatusResponse(OPERATION_ID, OperationStatus.SUSPENDED);
    }

    @Test
    @DisplayName("USER는 운영 서비스 상태를 변경할 수 없다.")
    @WithMockUser(roles = "USER")
    void changeStatus_forbidden() throws Exception {

        mockMvc.perform(patch("/api/operations/{id}/status", OPERATION_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "status": "SUSPENDED"
                                }
                                """))
                .andExpect(status().isForbidden());

        then(operationService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("변경할 상태가 없으면 400을 반환한다.")
    @WithMockUser(roles = "ADMIN")
    void changeStatus_invalidRequest() throws Exception {

        mockMvc.perform(patch("/api/operations/{id}/status", OPERATION_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {}
                                """))
                .andExpect(status().isBadRequest());

        then(operationService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("존재하지 않는 운영 서비스의 상태 변경은 404를 반환한다.")
    @WithMockUser(roles = "ADMIN")
    void changeStatus_notFound() throws Exception {

        given(operationService.changeStatusResponse(
                OPERATION_ID,
                OperationStatus.CLOSED
        )).willThrow(new BusinessException(
                OperationErrorCode.OPERATION_NOT_FOUND
        ));

        mockMvc.perform(patch("/api/operations/{id}/status", OPERATION_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "status": "CLOSED"
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("PMIS_OPERATION_001"));

        then(operationService).should()
                .changeStatusResponse(OPERATION_ID, OperationStatus.CLOSED);
    }

    // ---------------------------------------------------------------------
    // Authentication
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("인증되지 않은 사용자는 운영 서비스를 조회할 수 없다.")
    void getDetail_unauthorized() throws Exception {

        mockMvc.perform(get("/api/operations/{id}", OPERATION_ID))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));

        then(operationService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("인증되지 않은 사용자는 운영 서비스를 생성할 수 없다.")
    void create_unauthorized() throws Exception {

        mockMvc.perform(post("/api/operations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "operationCode": "OP-001",
                                    "operationName": "통합 운영 서비스",
                                    "operationType": "INFRASTRUCTURE_SERVICE"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));

        then(operationService).shouldHaveNoInteractions();
    }
}
