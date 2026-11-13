
package com.seos.cmdb.server.controller;

import com.seos.cmdb.ci.entity.CiEnvironment;
import com.seos.cmdb.ci.entity.CiStatus;
import com.seos.cmdb.server.dto.request.CreateServerRequest;
import com.seos.cmdb.server.dto.response.ServerRegistrationResponse;
import com.seos.cmdb.server.entity.ServerCategory;
import com.seos.cmdb.server.service.ServerRegistrationService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ServerRegistrationController.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        JwtAccessDeniedHandler.class,
        JwtAuthenticationEntryPoint.class
})
class ServerRegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ServerRegistrationService serverRegistrationService;

    @MockitoBean
    private JwtProvider jwtProvider;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @Test
    @WithMockUser(roles = "PM")
    void register_PM은_서버를_등록할_수_있다() throws Exception {
        given(serverRegistrationService.register(any(CreateServerRequest.class)))
                .willReturn(createResponse());

        String request = """
                {
                    "ciName": "운영 WEB 서버 01",
                    "ciTypeId": 1,
                    "hostname": "web-prod-01",
                    "serverCategory": "PHYSICAL",
                    "environment": "PROD",
                    "managementIp": "192.168.10.11",
                    "osName": "AIX",
                    "osVersion": "7.2",
                    "cpuCores": 8,
                    "memorySizeMb": 32768
                }
                """;

        mockMvc.perform(post("/api/servers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
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

        then(serverRegistrationService).should()
                .register(any(CreateServerRequest.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void register_USER는_서버를_등록할_수_없다() throws Exception {
        String request = """
                {
                    "ciName": "운영 WEB 서버 01",
                    "ciTypeId": 1,
                    "hostname": "web-prod-01",
                    "serverCategory": "PHYSICAL"
                }
                """;

        mockMvc.perform(post("/api/servers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false));

        then(serverRegistrationService).should(never())
                .register(any(CreateServerRequest.class));
    }

    @Test
    void register_인증되지_않은_사용자는_등록할_수_없다() throws Exception {
        String request = """
                {
                    "ciName": "운영 WEB 서버 01",
                    "ciTypeId": 1,
                    "hostname": "web-prod-01",
                    "serverCategory": "PHYSICAL"
                }
                """;

        mockMvc.perform(post("/api/servers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));

        then(serverRegistrationService).should(never())
                .register(any(CreateServerRequest.class));
    }

    @Test
    @WithMockUser(roles = "PM")
    void register_hostname이_누락되면_400을_반환한다() throws Exception {
        String request = """
                {
                    "ciName": "운영 WEB 서버 01",
                    "ciTypeId": 1,
                    "serverCategory": "PHYSICAL"
                }
                """;

        mockMvc.perform(post("/api/servers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        then(serverRegistrationService).should(never())
                .register(any(CreateServerRequest.class));
    }

    @Test
    @WithMockUser(roles = "PM")
    void register_serverCategory가_누락되면_400을_반환한다() throws Exception {
        String request = """
                {
                    "ciName": "운영 WEB 서버 01",
                    "ciTypeId": 1,
                    "hostname": "web-prod-01"
                }
                """;

        mockMvc.perform(post("/api/servers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        then(serverRegistrationService).should(never())
                .register(any(CreateServerRequest.class));
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
