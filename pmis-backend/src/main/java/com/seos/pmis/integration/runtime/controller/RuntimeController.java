package com.seos.pmis.integration.runtime.controller;

import com.seos.pmis.common.response.ApiResponse;
import com.seos.pmis.integration.runtime.dto.RuntimeOcrJobRequest;
import com.seos.pmis.integration.runtime.dto.RuntimeOcrJobResponse;
import com.seos.pmis.integration.runtime.dto.RuntimeOcrJobStatusResponse;
import com.seos.pmis.integration.runtime.dto.RuntimeOcrResultResponse;
import com.seos.pmis.integration.runtime.dto.RuntimeOcrUploadResponse;
import com.seos.pmis.integration.runtime.dto.RuntimeOutputFileResponse;
import com.seos.pmis.integration.runtime.service.RuntimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    /**
     * OCR 대상 이미지 업로드
     *
     * API:
     * POST /api/runtime/ocr/uploads
     *
     * Content-Type:
     * multipart/form-data
     */
    @PostMapping(
            value = "/ocr/uploads",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiResponse<RuntimeOcrUploadResponse>>
    uploadOcrImage(
            @RequestPart("file") MultipartFile file
    ) throws IOException {

        if (file.isEmpty()) {
            throw new IllegalArgumentException(
                    "OCR 업로드 파일은 비어 있을 수 없습니다."
            );
        }

        String fileName = file.getOriginalFilename();

        if (fileName == null || fileName.isBlank()) {
            fileName = "upload";
        }

        MediaType mediaType = resolveImageMediaType(file.getContentType());

        RuntimeOcrUploadResponse response =
                runtimeService.uploadImage(
                        fileName,
                        mediaType,
                        file.getBytes()
                );

        return ResponseEntity.ok(
                ApiResponse.success(response)
        );
    }

    /**
     * OCR 작업 생성
     *
     * API:
     * POST /api/runtime/ocr/jobs
     */
    @PostMapping(
            value = "/ocr/jobs",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<RuntimeOcrJobResponse>>
    createOcrJob(
            @RequestBody RuntimeOcrJobRequest request
    ) {
        return ResponseEntity.accepted().body(
                ApiResponse.success(
                        runtimeService.createOcrJob(request)
                )
        );
    }

    /**
     * OCR 작업 상태 조회
     *
     * API:
     * GET /api/runtime/ocr/jobs/{ocrRequestId}
     */
    @GetMapping("/ocr/jobs/{ocrRequestId}")
    public ResponseEntity<ApiResponse<RuntimeOcrJobStatusResponse>>
    getOcrJobStatus(
            @PathVariable String ocrRequestId
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        runtimeService.getOcrJobStatus(ocrRequestId)
                )
        );
    }

    /**
     * OCR 결과 조회
     *
     * API:
     * GET /api/runtime/ocr/jobs/{ocrRequestId}/result
     */
    @GetMapping("/ocr/jobs/{ocrRequestId}/result")
    public ResponseEntity<ApiResponse<RuntimeOcrResultResponse>>
    getOcrResult(
            @PathVariable String ocrRequestId
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        runtimeService.getOcrResult(ocrRequestId)
                )
        );
    }

    /**
     * 허용하는 이미지 MIME 타입을 확인한다.
     */
    private MediaType resolveImageMediaType(String contentType) {
        if (contentType == null) {
            throw new IllegalArgumentException(
                    "이미지 Content-Type이 필요합니다."
            );
        }

        MediaType mediaType = MediaType.parseMediaType(contentType);

        if (MediaType.IMAGE_PNG.isCompatibleWith(mediaType)) {
            return MediaType.IMAGE_PNG;
        }

        if (MediaType.IMAGE_JPEG.isCompatibleWith(mediaType)) {
            return MediaType.IMAGE_JPEG;
        }

        throw new IllegalArgumentException(
                "PNG 또는 JPEG 이미지 파일만 업로드할 수 있습니다."
        );
    }
}