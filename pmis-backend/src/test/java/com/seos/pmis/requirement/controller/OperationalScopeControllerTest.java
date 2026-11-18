
package com.seos.pmis.requirement.controller;

import com.seos.pmis.auth.service.UserSessionService;
import com.seos.common.exception.BusinessException;
import com.seos.pmis.auth.security.filter.JwtAuthenticationFilter;
import com.seos.pmis.auth.security.handler.JwtAccessDeniedHandler;
import com.seos.pmis.auth.security.handler.JwtAuthenticationEntryPoint;
import com.seos.pmis.auth.security.jwt.JwtProvider;
import com.seos.pmis.auth.service.CustomUserDetailsService;
import com.seos.pmis.config.security.SecurityConfig;
import com.seos.pmis.requirement.dto.request.OperationalScopeCreateRequest;
import com.seos.pmis.requirement.dto.request.OperationalScopeSearchRequest;
import com.seos.pmis.requirement.dto.request.OperationalScopeUpdateRequest;
import com.seos.pmis.requirement.dto.response.OperationalScopeResponse;
import com.seos.pmis.requirement.entity.OperationalScopeStatus;
import com.seos.pmis.requirement.entity.OperationalScopeType;
import com.seos.pmis.requirement.exception.OperationalScopeErrorCode;
import com.seos.pmis.requirement.service.OperationalScopeCreationService;
import com.seos.pmis.requirement.service.OperationalScopeQueryService;
import com.seos.pmis.requirement.service.OperationalScopeUpdateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
    private OperationalScopeQueryService operationalScopeQueryService;

    @MockitoBean
    private OperationalScopeUpdateService operationalScopeUpdateService;

    @MockitoBean
    private JwtProvider jwtProvider;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @MockitoBean
    private UserSessionService userSessionService;

    /**
     * Operational Scope 생성 테스트
     */
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

    /**
     * Operational Scope 검색 테스트
     */
    @Test
    @WithMockUser(roles = "USER")
    void search_USER는_OperationalScope를_검색할_수_있다() throws Exception {

        OperationalScopeResponse response = createResponse();

        given(operationalScopeQueryService.search(
                any(OperationalScopeSearchRequest.class),
                any()
        )).willReturn(new PageImpl<>(
                List.of(response),
                PageRequest.of(0, 10, Sort.by("scopeCode").ascending()),
                1
        ));

        mockMvc.perform(get("/api/operational-scopes")
                        .param("scopeName", "금융결제")
                        .param("scopeType", "DATABASE")
                        .param("status", "PLANNED")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "scopeCode,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].id").value(1))
                .andExpect(jsonPath("$.data.content[0].scopeCode")
                        .value("OPS-000001"))
                .andExpect(jsonPath("$.data.content[0].scopeName")
                        .value("금융결제 운영 데이터베이스"))
                .andExpect(jsonPath("$.data.content[0].scopeType")
                        .value("DATABASE"))
                .andExpect(jsonPath("$.data.content[0].status")
                        .value("PLANNED"))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.number").value(0))
                .andExpect(jsonPath("$.data.size").value(10));

        then(operationalScopeQueryService).should()
                .search(
                        any(OperationalScopeSearchRequest.class),
                        any()
                );
    }

    @Test
    @WithMockUser(roles = "PM")
    void search_PM은_상위_권한을_통해_OperationalScope를_검색할_수_있다()
            throws Exception {

        given(operationalScopeQueryService.search(
                any(OperationalScopeSearchRequest.class),
                any()
        )).willReturn(new PageImpl<>(
                List.of(),
                PageRequest.of(0, 10),
                0
        ));

        mockMvc.perform(get("/api/operational-scopes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content.length()").value(0));

        then(operationalScopeQueryService).should()
                .search(
                        any(OperationalScopeSearchRequest.class),
                        any()
                );
    }

    @Test
    void search_인증되지_않은_사용자는_검색할_수_없다() throws Exception {

        mockMvc.perform(get("/api/operational-scopes"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));

        then(operationalScopeQueryService).should(never())
                .search(
                        any(OperationalScopeSearchRequest.class),
                        any()
                );
    }

    /**
     * Operational Scope 상세 조회 테스트
     */
    @Test
    @WithMockUser(roles = "USER")
    void getDetail_USER는_OperationalScope를_상세_조회할_수_있다()
            throws Exception {

        given(operationalScopeQueryService.getDetail(1L))
                .willReturn(createResponse());

        mockMvc.perform(get("/api/operational-scopes/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.scopeCode").value("OPS-000001"))
                .andExpect(jsonPath("$.data.scopeName")
                        .value("금융결제 운영 데이터베이스"))
                .andExpect(jsonPath("$.data.scopeType").value("DATABASE"))
                .andExpect(jsonPath("$.data.status").value("PLANNED"));

        then(operationalScopeQueryService).should()
                .getDetail(1L);
    }

    @Test
    @WithMockUser(roles = "PM")
    void getDetail_PM은_상위_권한을_통해_상세_조회할_수_있다()
            throws Exception {

        given(operationalScopeQueryService.getDetail(1L))
                .willReturn(createResponse());

        mockMvc.perform(get("/api/operational-scopes/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));

        then(operationalScopeQueryService).should()
                .getDetail(1L);
    }

    @Test
    void getDetail_인증되지_않은_사용자는_상세_조회할_수_없다()
            throws Exception {

        mockMvc.perform(get("/api/operational-scopes/{id}", 1L))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));

        then(operationalScopeQueryService).should(never())
                .getDetail(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getDetail_존재하지_않는_ID는_404를_반환한다()
            throws Exception {

        given(operationalScopeQueryService.getDetail(999L))
                .willThrow(new BusinessException(
                        OperationalScopeErrorCode.OPERATIONAL_SCOPE_NOT_FOUND
                ));

        mockMvc.perform(get("/api/operational-scopes/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code")
                        .value("PMIS_OPERATIONAL_SCOPE_005"))
                .andExpect(jsonPath("$.message")
                        .value("Operational Scope를 찾을 수 없습니다."));

        then(operationalScopeQueryService).should()
                .getDetail(999L);
    }

    /**
     * Operational Scope 수정 테스트
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void update_ADMIN은_OperationalScope를_수정할_수_있다()
            throws Exception {

        OperationalScopeResponse response = OperationalScopeResponse.builder()
                .id(1L)
                .scopeCode("OPS-000001")
                .scopeName("금융결제 운영 데이터베이스 수정")
                .scopeType(OperationalScopeType.DATABASE)
                .status(OperationalScopeStatus.PLANNED)
                .version(1L)
                .ownerId(10L)
                .ownerUsername("manager")
                .ownerName("담당자")
                .description("수정된 운영 DB 범위")
                .effectiveFrom(LocalDate.of(2026, 11, 1))
                .build();

        given(operationalScopeUpdateService.update(
                eq(1L),
                any(OperationalScopeUpdateRequest.class)
        )).willReturn(response);

        String request = """
                {
                    "version": 0,
                    "scopeName": "금융결제 운영 데이터베이스 수정",
                    "scopeType": "DATABASE",
                    "ownerId": 10,
                    "description": "수정된 운영 DB 범위",
                    "effectiveFrom": "2026-11-01"
                }
                """;

        mockMvc.perform(put("/api/operational-scopes/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.message")
                        .value("Operational Scope가 수정되었습니다."))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.scopeCode").value("OPS-000001"))
                .andExpect(jsonPath("$.data.scopeName")
                        .value("금융결제 운영 데이터베이스 수정"))
                .andExpect(jsonPath("$.data.version").value(1));

        then(operationalScopeUpdateService).should()
                .update(
                        eq(1L),
                        any(OperationalScopeUpdateRequest.class)
                );
    }

    @Test
    @WithMockUser(roles = "USER")
    void update_USER는_OperationalScope를_수정할_수_없다()
            throws Exception {

        String request = """
                {
                    "version": 0,
                    "scopeName": "운영 시스템 수정",
                    "scopeType": "SYSTEM"
                }
                """;

        mockMvc.perform(put("/api/operational-scopes/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false));

        then(operationalScopeUpdateService).should(never())
                .update(
                        eq(1L),
                        any(OperationalScopeUpdateRequest.class)
                );
    }

    @Test
    void update_인증되지_않은_사용자는_수정할_수_없다()
            throws Exception {

        String request = """
                {
                    "version": 0,
                    "scopeName": "운영 시스템 수정",
                    "scopeType": "SYSTEM"
                }
                """;

        mockMvc.perform(put("/api/operational-scopes/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));

        then(operationalScopeUpdateService).should(never())
                .update(
                        eq(1L),
                        any(OperationalScopeUpdateRequest.class)
                );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_version이_누락되면_400을_반환한다()
            throws Exception {

        String request = """
                {
                    "scopeName": "운영 시스템 수정",
                    "scopeType": "SYSTEM"
                }
                """;

        mockMvc.perform(put("/api/operational-scopes/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        then(operationalScopeUpdateService).should(never())
                .update(
                        eq(1L),
                        any(OperationalScopeUpdateRequest.class)
                );
    }

    /**
     * 테스트 응답 객체
     */
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
