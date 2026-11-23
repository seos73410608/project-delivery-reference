
package com.seos.pmis.projectoperation.controller;

import com.seos.common.exception.BusinessException;
import com.seos.pmis.auth.security.filter.JwtAuthenticationFilter;
import com.seos.pmis.auth.security.handler.JwtAccessDeniedHandler;
import com.seos.pmis.auth.security.handler.JwtAuthenticationEntryPoint;
import com.seos.pmis.auth.security.jwt.JwtProvider;
import com.seos.pmis.auth.service.CustomUserDetailsService;
import com.seos.pmis.auth.service.UserSessionService;
import com.seos.pmis.config.security.SecurityConfig;
import com.seos.pmis.projectoperation.dto.response.ProjectOperationResponse;
import com.seos.pmis.projectoperation.entity.ProjectOperationRelationshipType;
import com.seos.pmis.projectoperation.entity.ProjectOperationStatus;
import com.seos.pmis.projectoperation.exception.ProjectOperationErrorCode;
import com.seos.pmis.projectoperation.service.ProjectOperationService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectOperationController.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        JwtAccessDeniedHandler.class,
        JwtAuthenticationEntryPoint.class
})
@DisplayName("ProjectOperationController 테스트")
class ProjectOperationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProjectOperationService projectOperationService;

    @MockitoBean
    private JwtProvider jwtProvider;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @MockitoBean
    private UserSessionService userSessionService;

    private static final Long PROJECT_OPERATION_ID = 1L;
    private static final Long PROJECT_ID = 10L;
    private static final Long OPERATION_ID = 20L;

    private ProjectOperationResponse createResponse() {
        return ProjectOperationResponse.builder()
                .id(PROJECT_OPERATION_ID)
                .relationshipType(ProjectOperationRelationshipType.BUILD_RESULT)
                .status(ProjectOperationStatus.ACTIVE)
                .projectId(PROJECT_ID)
                .projectCode("PRJ-001")
                .projectName("차세대 시스템 구축")
                .customerName("테스트 고객사")
                .projectManager("홍길동")
                .operationId(OPERATION_ID)
                .operationCode("OP-001")
                .operationName("통합 운영 서비스")
                .operationType(com.seos.pmis.operation.entity.OperationType.INFRASTRUCTURE_SERVICE)
                .operationStatus(com.seos.pmis.operation.entity.OperationStatus.ACTIVE)
                .effectiveFrom(LocalDate.of(2026, 1, 1))
                .effectiveTo(LocalDate.of(2026, 12, 31))
                .description("프로젝트 구축 결과 운영 이관")
                .createdAt(LocalDateTime.of(2026, 11, 23, 9, 0))
                .updatedAt(LocalDateTime.of(2026, 11, 23, 9, 0))
                .build();
    }

    // ---------------------------------------------------------------------
    // Create
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("ADMIN은 프로젝트 운영 관계를 생성할 수 있다.")
    @WithMockUser(roles = "ADMIN")
    void create_success() throws Exception {

        given(projectOperationService.createResponse(
                PROJECT_ID,
                OPERATION_ID,
                ProjectOperationRelationshipType.BUILD_RESULT,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31),
                "프로젝트 구축 결과 운영 이관"
        )).willReturn(createResponse());

        mockMvc.perform(post("/api/project-operations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "projectId": 10,
                                    "operationId": 20,
                                    "relationshipType": "BUILD_RESULT",
                                    "effectiveFrom": "2026-01-01",
                                    "effectiveTo": "2026-12-31",
                                    "description": "프로젝트 구축 결과 운영 이관"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.projectId").value(10))
                .andExpect(jsonPath("$.data.operationId").value(20))
                .andExpect(jsonPath("$.data.relationshipType")
                        .value("BUILD_RESULT"));

        then(projectOperationService).should()
                .createResponse(
                        PROJECT_ID,
                        OPERATION_ID,
                        ProjectOperationRelationshipType.BUILD_RESULT,
                        LocalDate.of(2026, 1, 1),
                        LocalDate.of(2026, 12, 31),
                        "프로젝트 구축 결과 운영 이관"
                );
    }

    @Test
    @DisplayName("USER는 프로젝트 운영 관계를 생성할 수 없다.")
    @WithMockUser(roles = "USER")
    void create_forbidden() throws Exception {

        mockMvc.perform(post("/api/project-operations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "projectId": 10,
                                    "operationId": 20,
                                    "relationshipType": "BUILD_RESULT"
                                }
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false));

        then(projectOperationService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("프로젝트 ID가 없으면 400을 반환한다.")
    @WithMockUser(roles = "ADMIN")
    void create_invalidProjectId() throws Exception {

        mockMvc.perform(post("/api/project-operations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "operationId": 20,
                                    "relationshipType": "BUILD_RESULT"
                                }
                                """))
                .andExpect(status().isBadRequest());

        then(projectOperationService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("운영 서비스 ID가 없으면 400을 반환한다.")
    @WithMockUser(roles = "ADMIN")
    void create_invalidOperationId() throws Exception {

        mockMvc.perform(post("/api/project-operations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "projectId": 10,
                                    "relationshipType": "BUILD_RESULT"
                                }
                                """))
                .andExpect(status().isBadRequest());

        then(projectOperationService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("관계 유형이 없으면 400을 반환한다.")
    @WithMockUser(roles = "ADMIN")
    void create_invalidRelationshipType() throws Exception {

        mockMvc.perform(post("/api/project-operations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "projectId": 10,
                                    "operationId": 20
                                }
                                """))
                .andExpect(status().isBadRequest());

        then(projectOperationService).shouldHaveNoInteractions();
    }

    // ---------------------------------------------------------------------
    // Read detail
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("USER는 프로젝트 운영 관계 상세 정보를 조회할 수 있다.")
    @WithMockUser(roles = "USER")
    void getDetail_success() throws Exception {

        given(projectOperationService.getDetailResponse(PROJECT_OPERATION_ID))
                .willReturn(createResponse());

        mockMvc.perform(get(
                        "/api/project-operations/{id}",
                        PROJECT_OPERATION_ID
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.projectCode").value("PRJ-001"))
                .andExpect(jsonPath("$.data.operationCode").value("OP-001"))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));

        then(projectOperationService).should()
                .getDetailResponse(PROJECT_OPERATION_ID);
    }

    @Test
    @DisplayName("존재하지 않는 프로젝트 운영 관계를 조회하면 404를 반환한다.")
    @WithMockUser(roles = "USER")
    void getDetail_notFound() throws Exception {

        given(projectOperationService.getDetailResponse(PROJECT_OPERATION_ID))
                .willThrow(new BusinessException(
                        ProjectOperationErrorCode.PROJECT_OPERATION_NOT_FOUND
                ));

        mockMvc.perform(get(
                        "/api/project-operations/{id}",
                        PROJECT_OPERATION_ID
                ))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code")
                        .value("PMIS_PROJECT_OPERATION_003"));

        then(projectOperationService).should()
                .getDetailResponse(PROJECT_OPERATION_ID);
    }

    // ---------------------------------------------------------------------
    // Read by project
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("USER는 프로젝트 기준 운영 관계 목록을 조회할 수 있다.")
    @WithMockUser(roles = "USER")
    void findByProject_success() throws Exception {

        given(projectOperationService.findByProjectResponses(PROJECT_ID))
                .willReturn(List.of(createResponse()));

        mockMvc.perform(get(
                        "/api/project-operations/by-project/{projectId}",
                        PROJECT_ID
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].projectId").value(10))
                .andExpect(jsonPath("$.data[0].operationId").value(20));

        then(projectOperationService).should()
                .findByProjectResponses(PROJECT_ID);
    }

    @Test
    @DisplayName("프로젝트에 연결된 운영 관계가 없으면 빈 목록을 반환한다.")
    @WithMockUser(roles = "USER")
    void findByProject_empty() throws Exception {

        given(projectOperationService.findByProjectResponses(PROJECT_ID))
                .willReturn(List.of());

        mockMvc.perform(get(
                        "/api/project-operations/by-project/{projectId}",
                        PROJECT_ID
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(0));

        then(projectOperationService).should()
                .findByProjectResponses(PROJECT_ID);
    }

    // ---------------------------------------------------------------------
    // Read by operation
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("USER는 운영 서비스 기준 프로젝트 관계 목록을 조회할 수 있다.")
    @WithMockUser(roles = "USER")
    void findByOperation_success() throws Exception {

        given(projectOperationService.findByOperationResponses(OPERATION_ID))
                .willReturn(List.of(createResponse()));

        mockMvc.perform(get(
                        "/api/project-operations/by-operation/{operationId}",
                        OPERATION_ID
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].operationId").value(20))
                .andExpect(jsonPath("$.data[0].projectId").value(10));

        then(projectOperationService).should()
                .findByOperationResponses(OPERATION_ID);
    }

    @Test
    @DisplayName("운영 서비스에 연결된 프로젝트 관계가 없으면 빈 목록을 반환한다.")
    @WithMockUser(roles = "USER")
    void findByOperation_empty() throws Exception {

        given(projectOperationService.findByOperationResponses(OPERATION_ID))
                .willReturn(List.of());

        mockMvc.perform(get(
                        "/api/project-operations/by-operation/{operationId}",
                        OPERATION_ID
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(0));

        then(projectOperationService).should()
                .findByOperationResponses(OPERATION_ID);
    }

    // ---------------------------------------------------------------------
    // Update
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("ADMIN은 프로젝트 운영 관계를 수정할 수 있다.")
    @WithMockUser(roles = "ADMIN")
    void update_success() throws Exception {

        given(projectOperationService.updateResponse(
                PROJECT_OPERATION_ID,
                ProjectOperationRelationshipType.ENHANCEMENT,
                LocalDate.of(2026, 2, 1),
                LocalDate.of(2026, 11, 30),
                "운영 관계 수정"
        )).willReturn(createResponse());

        mockMvc.perform(put(
                        "/api/project-operations/{id}",
                        PROJECT_OPERATION_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "relationshipType": "ENHANCEMENT",
                                    "effectiveFrom": "2026-02-01",
                                    "effectiveTo": "2026-11-30",
                                    "description": "운영 관계 수정"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));

        then(projectOperationService).should()
                .updateResponse(
                        PROJECT_OPERATION_ID,
                        ProjectOperationRelationshipType.ENHANCEMENT,
                        LocalDate.of(2026, 2, 1),
                        LocalDate.of(2026, 11, 30),
                        "운영 관계 수정"
                );
    }

    @Test
    @DisplayName("USER는 프로젝트 운영 관계를 수정할 수 없다.")
    @WithMockUser(roles = "USER")
    void update_forbidden() throws Exception {

        mockMvc.perform(put(
                        "/api/project-operations/{id}",
                        PROJECT_OPERATION_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "relationshipType": "ENHANCEMENT"
                                }
                                """))
                .andExpect(status().isForbidden());

        then(projectOperationService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("수정할 관계 유형이 없으면 400을 반환한다.")
    @WithMockUser(roles = "ADMIN")
    void update_invalidRequest() throws Exception {

        mockMvc.perform(put(
                        "/api/project-operations/{id}",
                        PROJECT_OPERATION_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "description": "관계 유형 누락"
                                }
                                """))
                .andExpect(status().isBadRequest());

        then(projectOperationService).shouldHaveNoInteractions();
    }

    // ---------------------------------------------------------------------
    // Change status
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("ADMIN은 프로젝트 운영 관계 상태를 변경할 수 있다.")
    @WithMockUser(roles = "ADMIN")
    void changeStatus_success() throws Exception {

        given(projectOperationService.changeStatusResponse(
                PROJECT_OPERATION_ID,
                ProjectOperationStatus.ACTIVE
        )).willReturn(createResponse());

        mockMvc.perform(patch(
                        "/api/project-operations/{id}/status",
                        PROJECT_OPERATION_ID
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

        then(projectOperationService).should()
                .changeStatusResponse(
                        PROJECT_OPERATION_ID,
                        ProjectOperationStatus.ACTIVE
                );
    }

    @Test
    @DisplayName("USER는 프로젝트 운영 관계 상태를 변경할 수 없다.")
    @WithMockUser(roles = "USER")
    void changeStatus_forbidden() throws Exception {

        mockMvc.perform(patch(
                        "/api/project-operations/{id}/status",
                        PROJECT_OPERATION_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "status": "ACTIVE"
                                }
                                """))
                .andExpect(status().isForbidden());

        then(projectOperationService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("변경할 상태가 없으면 400을 반환한다.")
    @WithMockUser(roles = "ADMIN")
    void changeStatus_invalidRequest() throws Exception {

        mockMvc.perform(patch(
                        "/api/project-operations/{id}/status",
                        PROJECT_OPERATION_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {}
                                """))
                .andExpect(status().isBadRequest());

        then(projectOperationService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("존재하지 않는 관계의 상태 변경은 404를 반환한다.")
    @WithMockUser(roles = "ADMIN")
    void changeStatus_notFound() throws Exception {

        given(projectOperationService.changeStatusResponse(
                PROJECT_OPERATION_ID,
                ProjectOperationStatus.ACTIVE
        )).willThrow(new BusinessException(
                ProjectOperationErrorCode.PROJECT_OPERATION_NOT_FOUND
        ));

        mockMvc.perform(patch(
                        "/api/project-operations/{id}/status",
                        PROJECT_OPERATION_ID
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "status": "ACTIVE"
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code")
                        .value("PMIS_PROJECT_OPERATION_003"));

        then(projectOperationService).should()
                .changeStatusResponse(
                        PROJECT_OPERATION_ID,
                        ProjectOperationStatus.ACTIVE
                );
    }

    // ---------------------------------------------------------------------
    // Authentication
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("인증되지 않은 사용자는 프로젝트 운영 관계를 조회할 수 없다.")
    void getDetail_unauthorized() throws Exception {

        mockMvc.perform(get(
                        "/api/project-operations/{id}",
                        PROJECT_OPERATION_ID
                ))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));

        then(projectOperationService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("인증되지 않은 사용자는 프로젝트 운영 관계를 생성할 수 없다.")
    void create_unauthorized() throws Exception {

        mockMvc.perform(post("/api/project-operations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "projectId": 10,
                                    "operationId": 20,
                                    "relationshipType": "BUILD_RESULT"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));

        then(projectOperationService).shouldHaveNoInteractions();
    }
}
