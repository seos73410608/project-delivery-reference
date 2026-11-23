
package com.seos.pmis.projectoperation.controller;

import com.seos.pmis.auth.security.filter.JwtAuthenticationFilter;
import com.seos.pmis.auth.security.handler.JwtAccessDeniedHandler;
import com.seos.pmis.auth.security.handler.JwtAuthenticationEntryPoint;
import com.seos.pmis.auth.security.jwt.JwtProvider;
import com.seos.pmis.auth.security.principal.UserPrincipal;
import com.seos.pmis.auth.service.CustomUserDetailsService;
import com.seos.pmis.auth.service.UserSessionService;
import com.seos.pmis.config.security.SecurityConfig;
import com.seos.pmis.projectoperation.dto.response.ProjectOperationHandoverResponse;
import com.seos.pmis.projectoperation.entity.ProjectOperationHandoverStatus;
import com.seos.pmis.projectoperation.entity.ProjectOperationHandoverType;
import com.seos.pmis.projectoperation.service.ProjectOperationHandoverService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectOperationHandoverController.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        JwtAccessDeniedHandler.class,
        JwtAuthenticationEntryPoint.class
})
@DisplayName("ProjectOperationHandoverController 테스트")
class ProjectOperationHandoverControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProjectOperationHandoverService handoverService;

    @MockitoBean
    private JwtProvider jwtProvider;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @MockitoBean
    private UserSessionService userSessionService;

    private static final Long HANDOVER_ID = 1L;
    private static final Long PROJECT_OPERATION_ID = 10L;
    private static final Long FROM_PARTICIPANT_ID = 20L;
    private static final Long TO_PARTICIPANT_ID = 30L;
    private static final Long USER_ID = 7L;

    private ProjectOperationHandoverResponse createResponse() {
        return ProjectOperationHandoverResponse.builder()
                .id(HANDOVER_ID)
                .handoverType(ProjectOperationHandoverType.INITIAL)
                .status(ProjectOperationHandoverStatus.REQUESTED)
                .sequenceNo(1)
                .plannedHandoverDate(LocalDate.of(2026, 12, 1))
                .requestedAt(LocalDateTime.of(2026, 11, 23, 10, 0))
                .build();
    }

    private UsernamePasswordAuthenticationToken adminAuthentication() {
        UserPrincipal principal = UserPrincipal.builder()
                .userId(USER_ID)
                .username("test-admin")
                .password("encoded-password")
                .role("ROLE_ADMIN")
                .build();

        return new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities()
        );
    }

    // ---------------------------------------------------------------------
    // Create
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("ADMIN은 최초 인수인계를 생성할 수 있다.")
    @WithMockUser(roles = "ADMIN")
    void createInitial_success() throws Exception {

        given(handoverService.createInitialResponse(
                PROJECT_OPERATION_ID,
                TO_PARTICIPANT_ID,
                LocalDate.of(2026, 12, 1),
                "최초 인수인계"
        )).willReturn(createResponse());

        mockMvc.perform(post("/api/project-operation-handovers/initial")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "projectOperationId": 10,
                                    "toParticipantId": 30,
                                    "plannedHandoverDate": "2026-12-01",
                                    "notes": "최초 인수인계"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.handoverType").value("INITIAL"));

        then(handoverService).should().createInitialResponse(
                PROJECT_OPERATION_ID,
                TO_PARTICIPANT_ID,
                LocalDate.of(2026, 12, 1),
                "최초 인수인계"
        );
    }

    @Test
    @DisplayName("ADMIN은 운영자 변경 인수인계를 생성할 수 있다.")
    @WithMockUser(roles = "ADMIN")
    void createOperatorChange_success() throws Exception {

        given(handoverService.createOperatorChangeResponse(
                PROJECT_OPERATION_ID,
                FROM_PARTICIPANT_ID,
                TO_PARTICIPANT_ID,
                LocalDate.of(2026, 12, 1),
                "운영자 변경"
        )).willReturn(createResponse());

        mockMvc.perform(post("/api/project-operation-handovers/operator-change")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "projectOperationId": 10,
                                    "fromParticipantId": 20,
                                    "toParticipantId": 30,
                                    "plannedHandoverDate": "2026-12-01",
                                    "notes": "운영자 변경"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));

        then(handoverService).should().createOperatorChangeResponse(
                PROJECT_OPERATION_ID,
                FROM_PARTICIPANT_ID,
                TO_PARTICIPANT_ID,
                LocalDate.of(2026, 12, 1),
                "운영자 변경"
        );
    }

    @Test
    @DisplayName("ADMIN은 재인수인계를 생성할 수 있다.")
    @WithMockUser(roles = "ADMIN")
    void createReHandover_success() throws Exception {

        given(handoverService.createReHandoverResponse(
                PROJECT_OPERATION_ID,
                FROM_PARTICIPANT_ID,
                TO_PARTICIPANT_ID,
                LocalDate.of(2026, 12, 1),
                "동일 운영자 재인수인계"
        )).willReturn(createResponse());

        mockMvc.perform(post("/api/project-operation-handovers/re-handover")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "projectOperationId": 10,
                                    "fromParticipantId": 20,
                                    "toParticipantId": 30,
                                    "plannedHandoverDate": "2026-12-01",
                                    "notes": "동일 운영자 재인수인계"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));

        then(handoverService).should().createReHandoverResponse(
                PROJECT_OPERATION_ID,
                FROM_PARTICIPANT_ID,
                TO_PARTICIPANT_ID,
                LocalDate.of(2026, 12, 1),
                "동일 운영자 재인수인계"
        );
    }

    @Test
    @DisplayName("USER는 인수인계를 생성할 수 없다.")
    @WithMockUser(roles = "USER")
    void createInitial_forbidden() throws Exception {

        mockMvc.perform(post("/api/project-operation-handovers/initial")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "projectOperationId": 10,
                                    "toParticipantId": 30
                                }
                                """))
                .andExpect(status().isForbidden());

        then(handoverService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("프로젝트 운영 관계 ID가 없으면 400을 반환한다.")
    @WithMockUser(roles = "ADMIN")
    void createInitial_invalidRequest() throws Exception {

        mockMvc.perform(post("/api/project-operation-handovers/initial")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "toParticipantId": 30
                                }
                                """))
                .andExpect(status().isBadRequest());

        then(handoverService).shouldHaveNoInteractions();
    }

    // ---------------------------------------------------------------------
    // Read
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("USER는 인수인계 상세 정보를 조회할 수 있다.")
    @WithMockUser(roles = "USER")
    void getDetail_success() throws Exception {

        given(handoverService.getDetailResponse(HANDOVER_ID))
                .willReturn(createResponse());

        mockMvc.perform(get(
                        "/api/project-operation-handovers/{id}",
                        HANDOVER_ID
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.handoverType").value("INITIAL"))
                .andExpect(jsonPath("$.data.status").value("REQUESTED"));

        then(handoverService).should().getDetailResponse(HANDOVER_ID);
    }

    @Test
    @DisplayName("USER는 프로젝트 운영 관계별 인수인계 이력을 조회할 수 있다.")
    @WithMockUser(roles = "USER")
    void getHistory_success() throws Exception {

        given(handoverService.getHistoryResponses(PROJECT_OPERATION_ID))
                .willReturn(List.of(createResponse()));

        mockMvc.perform(get(
                        "/api/project-operation-handovers/by-project-operation/{projectOperationId}",
                        PROJECT_OPERATION_ID
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(1));

        then(handoverService).should()
                .getHistoryResponses(PROJECT_OPERATION_ID);
    }

    @Test
    @DisplayName("인수인계 이력이 없으면 빈 목록을 반환한다.")
    @WithMockUser(roles = "USER")
    void getHistory_empty() throws Exception {

        given(handoverService.getHistoryResponses(PROJECT_OPERATION_ID))
                .willReturn(List.of());

        mockMvc.perform(get(
                        "/api/project-operation-handovers/by-project-operation/{projectOperationId}",
                        PROJECT_OPERATION_ID
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(0));

        then(handoverService).should()
                .getHistoryResponses(PROJECT_OPERATION_ID);
    }

    // ---------------------------------------------------------------------
    // State transitions
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("ADMIN은 인수인계를 요청할 수 있으며 인증 사용자 ID를 전달한다.")
    void request_success() throws Exception {

        given(handoverService.requestResponse(
                eq(HANDOVER_ID),
                eq(USER_ID),
                any(LocalDateTime.class)
        )).willReturn(createResponse());

        mockMvc.perform(patch(
                        "/api/project-operation-handovers/{id}/request",
                        HANDOVER_ID
                ).with(authentication(adminAuthentication())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));

        then(handoverService).should().requestResponse(
                eq(HANDOVER_ID),
                eq(USER_ID),
                any(LocalDateTime.class)
        );
    }

    @Test
    @DisplayName("ADMIN은 인수인계 진행을 시작할 수 있다.")
    @WithMockUser(roles = "ADMIN")
    void start_success() throws Exception {

        given(handoverService.startResponse(HANDOVER_ID))
                .willReturn(createResponse());

        mockMvc.perform(patch(
                        "/api/project-operation-handovers/{id}/start",
                        HANDOVER_ID
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        then(handoverService).should().startResponse(HANDOVER_ID);
    }

    @Test
    @DisplayName("ADMIN은 인수인계 검토를 요청할 수 있다.")
    @WithMockUser(roles = "ADMIN")
    void submitForReview_success() throws Exception {

        given(handoverService.submitForReviewResponse(HANDOVER_ID))
                .willReturn(createResponse());

        mockMvc.perform(patch(
                        "/api/project-operation-handovers/{id}/submit-for-review",
                        HANDOVER_ID
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        then(handoverService).should()
                .submitForReviewResponse(HANDOVER_ID);
    }

    @Test
    @DisplayName("ADMIN은 인수인계를 승인할 수 있으며 인증 사용자 ID를 전달한다.")
    void accept_success() throws Exception {

        given(handoverService.acceptResponse(
                eq(HANDOVER_ID),
                eq(USER_ID),
                any(LocalDateTime.class)
        )).willReturn(createResponse());

        mockMvc.perform(patch(
                        "/api/project-operation-handovers/{id}/accept",
                        HANDOVER_ID
                ).with(authentication(adminAuthentication())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        then(handoverService).should().acceptResponse(
                eq(HANDOVER_ID),
                eq(USER_ID),
                any(LocalDateTime.class)
        );
    }

    @Test
    @DisplayName("ADMIN은 인수인계를 완료할 수 있다.")
    @WithMockUser(roles = "ADMIN")
    void complete_success() throws Exception {

        given(handoverService.completeResponse(
                eq(HANDOVER_ID),
                any(LocalDateTime.class)
        )).willReturn(createResponse());

        mockMvc.perform(patch(
                        "/api/project-operation-handovers/{id}/complete",
                        HANDOVER_ID
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        then(handoverService).should().completeResponse(
                eq(HANDOVER_ID),
                any(LocalDateTime.class)
        );
    }

    @Test
    @DisplayName("ADMIN은 인수인계를 반려할 수 있다.")
    @WithMockUser(roles = "ADMIN")
    void reject_success() throws Exception {

        given(handoverService.rejectResponse(HANDOVER_ID))
                .willReturn(createResponse());

        mockMvc.perform(patch(
                        "/api/project-operation-handovers/{id}/reject",
                        HANDOVER_ID
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        then(handoverService).should().rejectResponse(HANDOVER_ID);
    }

    @Test
    @DisplayName("ADMIN은 인수인계를 취소할 수 있다.")
    @WithMockUser(roles = "ADMIN")
    void cancel_success() throws Exception {

        given(handoverService.cancelResponse(HANDOVER_ID))
                .willReturn(createResponse());

        mockMvc.perform(patch(
                        "/api/project-operation-handovers/{id}/cancel",
                        HANDOVER_ID
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        then(handoverService).should().cancelResponse(HANDOVER_ID);
    }

    // ---------------------------------------------------------------------
    // Authorization
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("USER는 인수인계 상태를 변경할 수 없다.")
    @WithMockUser(roles = "USER")
    void start_forbidden() throws Exception {

        mockMvc.perform(patch(
                        "/api/project-operation-handovers/{id}/start",
                        HANDOVER_ID
                ))
                .andExpect(status().isForbidden());

        then(handoverService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("인증되지 않은 사용자는 인수인계를 조회할 수 없다.")
    void getDetail_unauthorized() throws Exception {

        mockMvc.perform(get(
                        "/api/project-operation-handovers/{id}",
                        HANDOVER_ID
                ))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));

        then(handoverService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("인증되지 않은 사용자는 인수인계를 생성할 수 없다.")
    void createInitial_unauthorized() throws Exception {

        mockMvc.perform(post("/api/project-operation-handovers/initial")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "projectOperationId": 10,
                                    "toParticipantId": 30
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));

        then(handoverService).shouldHaveNoInteractions();
    }
}
