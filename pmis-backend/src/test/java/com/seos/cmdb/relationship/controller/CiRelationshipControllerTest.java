package com.seos.cmdb.relationship.controller;

import com.seos.cmdb.relationship.dto.request.CreateCiRelationshipRequest;
import com.seos.cmdb.relationship.dto.request.UpdateCiRelationshipRequest;
import com.seos.cmdb.relationship.dto.response.CiRelationshipResponse;
import com.seos.cmdb.relationship.dto.response.CiRelationshipTypeResponse;
import com.seos.cmdb.relationship.entity.RelationshipDirection;
import com.seos.cmdb.relationship.entity.RelationshipStatus;
import com.seos.cmdb.relationship.service.CiRelationshipService;
import com.seos.pmis.auth.security.filter.JwtAuthenticationFilter;
import com.seos.pmis.auth.security.handler.JwtAccessDeniedHandler;
import com.seos.pmis.auth.security.handler.JwtAuthenticationEntryPoint;
import com.seos.pmis.auth.security.jwt.JwtProvider;
import com.seos.pmis.auth.service.CustomUserDetailsService;
import com.seos.pmis.config.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CiRelationshipController.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        JwtAccessDeniedHandler.class,
        JwtAuthenticationEntryPoint.class
})
class CiRelationshipControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CiRelationshipService ciRelationshipService;

    @MockitoBean
    private JwtProvider jwtProvider;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @Test
    @WithMockUser(roles = "PM")
    void create_PM은_CI_관계를_생성할_수_있다() throws Exception {
        given(ciRelationshipService.create(eq(1L), any(CreateCiRelationshipRequest.class)))
                .willReturn(createResponse());

        String request = """
                {
                    "targetCiId": 2,
                    "relationshipTypeId": 1,
                    "description": "애플리케이션 서버가 DB 서버에 의존"
                }
                """;

        mockMvc.perform(post("/api/configuration-items/1/relationships")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(10))
                .andExpect(jsonPath("$.data.sourceCiId").value(1))
                .andExpect(jsonPath("$.data.targetCiId").value(2))
                .andExpect(jsonPath("$.data.relationshipTypeCode").value("DEPENDS_ON"));

        then(ciRelationshipService).should()
                .create(eq(1L), any(CreateCiRelationshipRequest.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllForCi_USER는_관계_목록을_조회할_수_있다() throws Exception {
        given(ciRelationshipService.getAllForCi(
                1L, RelationshipDirection.ALL, null, null
        )).willReturn(List.of(createResponse()));

        mockMvc.perform(get("/api/configuration-items/1/relationships"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].targetCiId").value(2));

        then(ciRelationshipService).should()
                .getAllForCi(1L, RelationshipDirection.ALL, null, null);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllForCi_방향과_상태_유형으로_필터링할_수_있다() throws Exception {
        given(ciRelationshipService.getAllForCi(
                1L, RelationshipDirection.OUTGOING, RelationshipStatus.ACTIVE, 1L
        )).willReturn(List.of(createResponse()));

        mockMvc.perform(get("/api/configuration-items/1/relationships")
                        .param("direction", "OUTGOING")
                        .param("status", "ACTIVE")
                        .param("relationshipTypeId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        then(ciRelationshipService).should()
                .getAllForCi(1L, RelationshipDirection.OUTGOING, RelationshipStatus.ACTIVE, 1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getById_USER는_관계_상세를_조회할_수_있다() throws Exception {
        given(ciRelationshipService.getById(10L)).willReturn(createResponse());

        mockMvc.perform(get("/api/ci-relationships/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(10))
                .andExpect(jsonPath("$.data.sourceCiCode").value("CI-000001"));

        then(ciRelationshipService).should().getById(10L);
    }

    @Test
    @WithMockUser(roles = "PM")
    void update_PM은_관계_설명을_수정할_수_있다() throws Exception {
        given(ciRelationshipService.update(eq(10L), any(UpdateCiRelationshipRequest.class)))
                .willReturn(createResponse());

        String request = """
                {
                    "description": "관계 설명 수정"
                }
                """;

        mockMvc.perform(put("/api/ci-relationships/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        then(ciRelationshipService).should()
                .update(eq(10L), any(UpdateCiRelationshipRequest.class));
    }

    @Test
    @WithMockUser(roles = "PM")
    void deactivate_PM은_관계를_비활성화할_수_있다() throws Exception {
        given(ciRelationshipService.deactivate(10L, "서버 교체"))
                .willReturn(createResponse());

        String request = """
                {
                    "reason": "서버 교체"
                }
                """;

        mockMvc.perform(patch("/api/ci-relationships/10/deactivate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        then(ciRelationshipService).should().deactivate(10L, "서버 교체");
    }

    @Test
    @WithMockUser(roles = "USER")
    void getEnabledRelationshipTypes_USER는_활성_관계_유형을_조회할_수_있다() throws Exception {
        given(ciRelationshipService.getRelationshipTypes(false))
                .willReturn(List.of(createTypeResponse()));

        mockMvc.perform(get("/api/ci-relationship-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].code").value("DEPENDS_ON"))
                .andExpect(jsonPath("$.data[0].enabled").value(true));

        then(ciRelationshipService).should().getRelationshipTypes(false);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllRelationshipTypes_ADMIN은_전체_관계_유형을_조회할_수_있다() throws Exception {
        given(ciRelationshipService.getRelationshipTypes(true))
                .willReturn(List.of(createTypeResponse()));

        mockMvc.perform(get("/api/ci-relationship-types/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        then(ciRelationshipService).should().getRelationshipTypes(true);
    }

    @Test
    @WithMockUser(roles = "USER")
    void create_USER는_CI_관계를_생성할_수_없다() throws Exception {
        String request = """
                {
                    "targetCiId": 2,
                    "relationshipTypeId": 1
                }
                """;

        mockMvc.perform(post("/api/configuration-items/1/relationships")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isForbidden());

        then(ciRelationshipService).should(never())
                .create(eq(1L), any(CreateCiRelationshipRequest.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void deactivate_USER는_관계를_비활성화할_수_없다() throws Exception {
        String request = """
                {
                    "reason": "서버 교체"
                }
                """;

        mockMvc.perform(patch("/api/ci-relationships/10/deactivate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isForbidden());

        then(ciRelationshipService).should(never())
                .deactivate(eq(10L), any(String.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllRelationshipTypes_USER는_ADMIN_전용_API를_호출할_수_없다() throws Exception {
        mockMvc.perform(get("/api/ci-relationship-types/all"))
                .andExpect(status().isForbidden());

        then(ciRelationshipService).should(never()).getRelationshipTypes(true);
    }

    @Test
    void getAllForCi_미인증_사용자는_조회할_수_없다() throws Exception {
        mockMvc.perform(get("/api/configuration-items/1/relationships"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));

        then(ciRelationshipService).should(never())
                .getAllForCi(any(), any(), any(), any());
    }

    @Test
    @WithMockUser(roles = "PM")
    void create_targetCiId가_누락되면_400을_반환한다() throws Exception {
        String request = """
                {
                    "relationshipTypeId": 1
                }
                """;

        mockMvc.perform(post("/api/configuration-items/1/relationships")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        then(ciRelationshipService).should(never())
                .create(eq(1L), any(CreateCiRelationshipRequest.class));
    }

    @Test
    @WithMockUser(roles = "PM")
    void deactivate_reason이_누락되면_400을_반환한다() throws Exception {
        mockMvc.perform(patch("/api/ci-relationships/10/deactivate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        then(ciRelationshipService).should(never())
                .deactivate(eq(10L), any(String.class));
    }

    private CiRelationshipResponse createResponse() {
        return CiRelationshipResponse.builder()
                .id(10L)
                .sourceCiId(1L)
                .sourceCiCode("CI-000001")
                .sourceCiName("업무 애플리케이션 서버")
                .targetCiId(2L)
                .targetCiCode("CI-000002")
                .targetCiName("업무 DB 서버")
                .relationshipTypeId(1L)
                .relationshipTypeCode("DEPENDS_ON")
                .relationshipTypeName("의존")
                .description("애플리케이션 서버가 DB 서버에 의존")
                .status(RelationshipStatus.ACTIVE)
                .build();
    }

    private CiRelationshipTypeResponse createTypeResponse() {
        return CiRelationshipTypeResponse.builder()
                .id(1L)
                .code("DEPENDS_ON")
                .name("의존")
                .description("대상 CI에 의존하는 관계")
                .enabled(true)
                .sortOrder(2)
                .build();
    }
}