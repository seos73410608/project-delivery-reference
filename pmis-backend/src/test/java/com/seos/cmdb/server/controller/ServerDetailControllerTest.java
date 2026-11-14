package com.seos.cmdb.server.controller;

import com.seos.cmdb.ci.entity.CiEnvironment;
import com.seos.cmdb.ci.entity.CiStatus;
import com.seos.cmdb.server.dto.response.ServerRegistrationResponse;
import com.seos.cmdb.server.entity.ServerCategory;
import com.seos.cmdb.server.service.ServerDetailQueryService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ServerDetailController.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        JwtAccessDeniedHandler.class,
        JwtAuthenticationEntryPoint.class
})
class ServerDetailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ServerDetailQueryService serverDetailQueryService;

    @MockitoBean
    private JwtProvider jwtProvider;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @Test
    @WithMockUser(roles = "USER")
    void getServerDetail_USER는_서버_상세정보를_조회할_수_있다() throws Exception {
        given(serverDetailQueryService.getServerDetail(100L))
                .willReturn(createResponse());

        mockMvc.perform(get("/api/configuration-items/100/server-detail"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(100))
                .andExpect(jsonPath("$.data.ciCode").value("CI-000101"))
                .andExpect(jsonPath("$.data.ciTypeCode").value("SERVER"))
                .andExpect(jsonPath("$.data.serverDetail.hostname")
                        .value("web-prod-01"))
                .andExpect(jsonPath("$.data.serverDetail.serverCategory")
                        .value("PHYSICAL"))
                .andExpect(jsonPath("$.data.serverDetail.managementIp")
                        .value("192.168.10.11"));

        then(serverDetailQueryService).should().getServerDetail(100L);
    }

    @Test
    @WithMockUser(roles = "PM")
    void getServerDetail_PM은_USER_권한을_상속하여_조회할_수_있다() throws Exception {
        given(serverDetailQueryService.getServerDetail(100L))
                .willReturn(createResponse());

        mockMvc.perform(get("/api/configuration-items/100/server-detail"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        then(serverDetailQueryService).should().getServerDetail(100L);
    }

    @Test
    @WithMockUser(roles = "GUEST")
    void getServerDetail_조회_권한이_없는_사용자는_조회할_수_없다() throws Exception {
        mockMvc.perform(get("/api/configuration-items/100/server-detail"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false));

        then(serverDetailQueryService).should(never()).getServerDetail(100L);
    }

    @Test
    void getServerDetail_인증되지_않은_사용자는_조회할_수_없다() throws Exception {
        mockMvc.perform(get("/api/configuration-items/100/server-detail"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));

        then(serverDetailQueryService).should(never()).getServerDetail(100L);
    }

    private ServerRegistrationResponse createResponse() {
        ServerRegistrationResponse.ServerDetailResponse detail =
                ServerRegistrationResponse.ServerDetailResponse.builder()
                        .hostname("web-prod-01")
                        .managementIp("192.168.10.11")
                        .serverCategory(ServerCategory.PHYSICAL)
                        .osName("AIX")
                        .osVersion("7.2")
                        .cpuCores(8)
                        .memorySizeMb(32768L)
                        .build();

        return ServerRegistrationResponse.builder()
                .id(100L)
                .ciCode("CI-000101")
                .ciName("운영 WEB 서버 01")
                .ciTypeId(1L)
                .ciTypeCode("SERVER")
                .ciTypeName("서버")
                .status(CiStatus.PLANNED)
                .environment(CiEnvironment.PROD)
                .serverDetail(detail)
                .build();
    }
}