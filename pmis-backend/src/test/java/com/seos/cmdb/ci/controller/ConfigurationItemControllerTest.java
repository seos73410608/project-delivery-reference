
package com.seos.cmdb.ci.controller;

import com.seos.cmdb.ci.dto.request.ChangeConfigurationItemStatusRequest;
import com.seos.cmdb.ci.dto.request.CreateConfigurationItemRequest;
import com.seos.cmdb.ci.dto.request.UpdateConfigurationItemRequest;
import com.seos.cmdb.ci.dto.response.ConfigurationItemResponse;
import com.seos.cmdb.ci.entity.CiEnvironment;
import com.seos.cmdb.ci.entity.CiStatus;
import com.seos.cmdb.ci.service.ConfigurationItemService;
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

@WebMvcTest(ConfigurationItemController.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        JwtAccessDeniedHandler.class,
        JwtAuthenticationEntryPoint.class
})
class ConfigurationItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConfigurationItemService configurationItemService;

    @MockitoBean
    private JwtProvider jwtProvider;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @Test
    @WithMockUser(roles = "PM")
    void create_PM은_CI를_생성할_수_있다() throws Exception {
        given(configurationItemService.create(any(CreateConfigurationItemRequest.class)))
                .willReturn(createResponse());

        String request = """
                {
                    "ciName": "금융결제원 업무 서버",
                    "ciTypeId": 1,
                    "environment": "PROD",
                    "location": "전산실",
                    "description": "업무 서비스 운영 서버"
                }
                """;

        mockMvc.perform(post("/api/configuration-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.ciCode").value("CI-000001"))
                .andExpect(jsonPath("$.data.ciName").value("금융결제원 업무 서버"));

        then(configurationItemService).should()
                .create(any(CreateConfigurationItemRequest.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAll_USER는_CI_목록을_조회할_수_있다() throws Exception {
        given(configurationItemService.getAll())
                .willReturn(List.of(createResponse()));

        mockMvc.perform(get("/api/configuration-items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].ciCode").value("CI-000001"));

        then(configurationItemService).should().getAll();
    }

    @Test
    @WithMockUser(roles = "PM")
    void getAll_PM은_USER_권한을_상속하여_조회할_수_있다() throws Exception {
        given(configurationItemService.getAll()).willReturn(List.of());

        mockMvc.perform(get("/api/configuration-items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getById_ADMIN은_CI를_조회할_수_있다() throws Exception {
        given(configurationItemService.getById(1L))
                .willReturn(createResponse());

        mockMvc.perform(get("/api/configuration-items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.ciCode").value("CI-000001"))
                .andExpect(jsonPath("$.data.ciTypeName").value("서버"));
    }

    @Test
    @WithMockUser(roles = "PM")
    void update_PM은_CI를_수정할_수_있다() throws Exception {
        given(configurationItemService.update(eq(1L), any(UpdateConfigurationItemRequest.class)))
                .willReturn(createResponse());

        String request = """
                {
                    "ciName": "금융결제원 업무 서버 수정",
                    "ciTypeId": 1,
                    "environment": "PROD",
                    "location": "제1전산실",
                    "description": "운영 서버 정보 수정"
                }
                """;

        mockMvc.perform(put("/api/configuration-items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        then(configurationItemService).should()
                .update(eq(1L), any(UpdateConfigurationItemRequest.class));
    }

    @Test
    @WithMockUser(roles = "PM")
    void changeStatus_PM은_CI_상태를_변경할_수_있다() throws Exception {
        given(configurationItemService.changeStatus(
                eq(1L), any(ChangeConfigurationItemStatusRequest.class)))
                .willReturn(createResponse());

        String request = """
                {
                    "status": "ACTIVE"
                }
                """;

        mockMvc.perform(patch("/api/configuration-items/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        then(configurationItemService).should()
                .changeStatus(eq(1L), any(ChangeConfigurationItemStatusRequest.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void create_USER는_CI를_생성할_수_없다() throws Exception {
        String request = """
                {
                    "ciName": "업무 서버",
                    "ciTypeId": 1
                }
                """;

        mockMvc.perform(post("/api/configuration-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false));

        then(configurationItemService).should(never())
                .create(any(CreateConfigurationItemRequest.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void update_USER는_CI를_수정할_수_없다() throws Exception {
        String request = """
                {
                    "ciName": "수정 서버",
                    "ciTypeId": 1
                }
                """;

        mockMvc.perform(put("/api/configuration-items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isForbidden());

        then(configurationItemService).should(never())
                .update(eq(1L), any(UpdateConfigurationItemRequest.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void changeStatus_USER는_CI_상태를_변경할_수_없다() throws Exception {
        String request = """
                {
                    "status": "ACTIVE"
                }
                """;

        mockMvc.perform(patch("/api/configuration-items/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isForbidden());

        then(configurationItemService).should(never())
                .changeStatus(eq(1L), any(ChangeConfigurationItemStatusRequest.class));
    }

    @Test
    void getAll_인증되지_않은_사용자는_조회할_수_없다() throws Exception {
        mockMvc.perform(get("/api/configuration-items"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser(roles = "PM")
    void create_ciName이_누락되면_400을_반환한다() throws Exception {
        String request = """
                {
                    "ciTypeId": 1
                }
                """;

        mockMvc.perform(post("/api/configuration-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        then(configurationItemService).should(never())
                .create(any(CreateConfigurationItemRequest.class));
    }

    @Test
    @WithMockUser(roles = "PM")
    void create_ciTypeId가_누락되면_400을_반환한다() throws Exception {
        String request = """
                {
                    "ciName": "업무 서버"
                }
                """;

        mockMvc.perform(post("/api/configuration-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        then(configurationItemService).should(never())
                .create(any(CreateConfigurationItemRequest.class));
    }

    @Test
    @WithMockUser(roles = "PM")
    void changeStatus_status가_누락되면_400을_반환한다() throws Exception {
        mockMvc.perform(patch("/api/configuration-items/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        then(configurationItemService).should(never())
                .changeStatus(eq(1L), any(ChangeConfigurationItemStatusRequest.class));
    }

    private ConfigurationItemResponse createResponse() {
        return ConfigurationItemResponse.builder()
                .id(1L)
                .ciCode("CI-000001")
                .ciName("금융결제원 업무 서버")
                .ciTypeId(1L)
                .ciTypeCode("SERVER")
                .ciTypeName("서버")
                .status(CiStatus.PLANNED)
                .environment(CiEnvironment.PROD)
                .location("전산실")
                .description("업무 서비스 운영 서버")
                .build();
    }
}
