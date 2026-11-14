
package com.seos.cmdb.server.controller;

import com.seos.cmdb.server.dto.response.ServerRegistrationResponse;
import com.seos.cmdb.server.service.ServerDetailQueryService;
import com.seos.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/configuration-items")
@RequiredArgsConstructor
public class ServerDetailController {

    private final ServerDetailQueryService serverDetailQueryService;

    /**
     * CI ID로 서버 상세정보 조회
     */
    @GetMapping("/{ciId}/server-detail")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<ServerRegistrationResponse>> getServerDetail(
            @PathVariable Long ciId
    ) {

        ServerRegistrationResponse response =
                serverDetailQueryService.getServerDetail(ciId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "서버 상세정보를 조회했습니다.",
                        response
                )
        );
    }
}
