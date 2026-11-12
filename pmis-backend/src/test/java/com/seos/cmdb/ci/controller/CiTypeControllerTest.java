
package com.seos.cmdb.ci.controller;

import com.seos.cmdb.ci.dto.request.CreateCiTypeRequest;
import com.seos.cmdb.ci.dto.request.UpdateCiTypeRequest;
import com.seos.cmdb.ci.dto.response.CiTypeResponse;
import com.seos.cmdb.ci.service.CiTypeService;
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

@WebMvcTest(CiTypeController.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        JwtAccessDeniedHandler.class,
        JwtAuthenticationEntryPoint.class
})
class CiTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CiTypeService ciTypeService;

    @MockitoBean
    private JwtProvider jwtProvider;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_ADMIN은_CI_Type을_생성할_수_있다() throws Exception {
        CiTypeResponse response = createResponse();

        given(ciTypeService.create(any(CreateCiTypeRequest.class)))
                .willReturn(response);

        String request = """
                {
                    "code": "SERVER",
                    "name": "서버",
                    "assignable": true,
                    "sortOrder": 1
                }
                """;

        mockMvc.perform(post("/api/ci-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("CI Type이 생성되었습니다."))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.code").value("SERVER"));

        then(ciTypeService).should()
                .create(any(CreateCiTypeRequest.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAll_USER는_CI_Type_목록을_조회할_수_있다() throws Exception {
        given(ciTypeService.getAll())
                .willReturn(List.of(createResponse()));

        mockMvc.perform(get("/api/ci-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].code").value("SERVER"));

        then(ciTypeService).should().getAll();
    }

    @Test
    @WithMockUser(roles = "PM")
    void getAll_PM은_USER_권한을_상속하여_조회할_수_있다() throws Exception {
        given(ciTypeService.getAll()).willReturn(List.of());

        mockMvc.perform(get("/api/ci-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getById_ADMIN은_CI_Type을_조회할_수_있다() throws Exception {
        given(ciTypeService.getById(1L))
                .willReturn(createResponse());

        mockMvc.perform(get("/api/ci-types/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("서버"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_ADMIN은_CI_Type을_수정할_수_있다() throws Exception {
        given(ciTypeService.update(eq(1L), any(UpdateCiTypeRequest.class)))
                .willReturn(createResponse());

        String request = """
                {
                    "name": "물리 서버",
                    "assignable": true,
                    "sortOrder": 2
                }
                """;

        mockMvc.perform(put("/api/ci-types/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        then(ciTypeService).should()
                .update(eq(1L), any(UpdateCiTypeRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void enable_ADMIN은_CI_Type을_활성화할_수_있다() throws Exception {
        given(ciTypeService.enable(1L))
                .willReturn(createResponse());

        mockMvc.perform(patch("/api/ci-types/1/enable"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("CI Type이 활성화되었습니다."));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void disable_ADMIN은_CI_Type을_비활성화할_수_있다() throws Exception {
        mockMvc.perform(patch("/api/ci-types/1/disable"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("CI Type이 비활성화되었습니다."));

        then(ciTypeService).should().disable(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_ADMIN은_CI_Type을_삭제할_수_있다() throws Exception {
        mockMvc.perform(delete("/api/ci-types/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("CI Type이 삭제되었습니다."));

        then(ciTypeService).should().delete(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void create_USER는_CI_Type을_생성할_수_없다() throws Exception {
        String request = """
                {
                    "code": "SERVER",
                    "name": "서버",
                    "assignable": true
                }
                """;

        mockMvc.perform(post("/api/ci-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false));

        then(ciTypeService).should(never())
                .create(any(CreateCiTypeRequest.class));
    }

    @Test
    @WithMockUser(roles = "PM")
    void create_PM은_ADMIN_권한이_없어_생성할_수_없다() throws Exception {
        String request = """
                {
                    "code": "SERVER",
                    "name": "서버",
                    "assignable": true
                }
                """;

        mockMvc.perform(post("/api/ci-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isForbidden());

        then(ciTypeService).should(never())
                .create(any(CreateCiTypeRequest.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void update_USER는_CI_Type을_수정할_수_없다() throws Exception {
        String request = """
                {
                    "name": "물리 서버",
                    "assignable": true,
                    "sortOrder": 2
                }
                """;

        mockMvc.perform(put("/api/ci-types/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isForbidden());

        then(ciTypeService).should(never())
                .update(eq(1L), any(UpdateCiTypeRequest.class));
    }

    @Test
    @WithMockUser(roles = "PM")
    void disable_PM은_CI_Type을_비활성화할_수_없다() throws Exception {
        mockMvc.perform(patch("/api/ci-types/1/disable"))
                .andExpect(status().isForbidden());

        then(ciTypeService).should(never()).disable(1L);
    }

    @Test
    void getAll_인증되지_않은_사용자는_조회할_수_없다() throws Exception {
        mockMvc.perform(get("/api/ci-types"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_code가_누락되면_400을_반환한다() throws Exception {
        String request = """
                {
                    "name": "서버",
                    "assignable": true
                }
                """;

        mockMvc.perform(post("/api/ci-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        then(ciTypeService).should(never())
                .create(any(CreateCiTypeRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_sortOrder가_누락되면_400을_반환한다() throws Exception {
        String request = """
                {
                    "name": "물리 서버",
                    "assignable": true
                }
                """;

        mockMvc.perform(put("/api/ci-types/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        then(ciTypeService).should(never())
                .update(eq(1L), any(UpdateCiTypeRequest.class));
    }

    private CiTypeResponse createResponse() {
        return CiTypeResponse.builder()
                .id(1L)
                .code("SERVER")
                .name("서버")
                .assignable(true)
                .enabled(true)
                .sortOrder(1)
                .build();
    }
}
