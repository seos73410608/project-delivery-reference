package com.seos.pmis.integration.runtime.controller;

import com.seos.pmis.common.response.ApiResponse;
import com.seos.pmis.integration.runtime.dto.RuntimeOutputFileResponse;
import com.seos.pmis.integration.runtime.service.RuntimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/runtime")
@RequiredArgsConstructor
public class RuntimeController {

    private final RuntimeService runtimeService;

    /**
     * 실행 결과 파일 목록 조회
     */
    @GetMapping("/executions/{executionUuid}/files")
    public ResponseEntity<ApiResponse<List<RuntimeOutputFileResponse>>>
    getExecutionFiles(
            @PathVariable String executionUuid
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        runtimeService.getExecutionFiles(executionUuid)
                )
        );
    }

    /**
     * 파일 메타데이터 조회
     */
    @GetMapping("/files/{fileId}")
    public ResponseEntity<ApiResponse<RuntimeOutputFileResponse>>
    getFileMetadata(
            @PathVariable String fileId
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        runtimeService.getFileMetadata(fileId)
                )
        );
    }

    /**
     * 파일 내용 다운로드
     */
    @GetMapping("/files/{fileId}/content")
    public ResponseEntity<byte[]> downloadFileContent(
            @PathVariable String fileId
    ) {
        byte[] content = runtimeService.downloadFileContent(fileId);

        ContentDisposition contentDisposition =
                ContentDisposition.attachment()
                        .filename("runtime-file.bin", StandardCharsets.UTF_8)
                        .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        contentDisposition.toString()
                )
                .body(content);
    }
}