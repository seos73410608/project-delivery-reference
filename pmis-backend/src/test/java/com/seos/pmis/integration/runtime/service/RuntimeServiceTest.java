package com.seos.pmis.integration.runtime.service;

import com.seos.pmis.common.exception.BusinessException;
import com.seos.pmis.evidence.exception.code.EvidenceErrorCode;
import com.seos.pmis.integration.runtime.client.RuntimeApiClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RuntimeServiceTest {

    @Test
    void downloadFileContent_preservesOriginalExceptionAsCause() {
        RuntimeApiClient runtimeApiClient = mock(RuntimeApiClient.class);
        RuntimeService runtimeService = new RuntimeService(runtimeApiClient);

        String fileId = "test-file-id";
        ResourceAccessException originalException =
                new ResourceAccessException("Runtime connection failed");

        when(runtimeApiClient.downloadFileContent(fileId))
                .thenThrow(originalException);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> runtimeService.downloadFileContent(fileId)
        );

        assertEquals(
                EvidenceErrorCode.RUNTIME_API_UNAVAILABLE,
                exception.getErrorCode()
        );
        assertSame(originalException, exception.getCause());
        verify(runtimeApiClient).downloadFileContent(fileId);
    }

    @Test
    void downloadFileContent_preservesHttpErrorStatusAndCause() {
        RuntimeApiClient runtimeApiClient = mock(RuntimeApiClient.class);
        RuntimeService runtimeService = new RuntimeService(runtimeApiClient);

        String fileId = "missing-file-id";

        RestClientResponseException originalException =
                new RestClientResponseException(
                        "Runtime returned 404",
                        HttpStatus.NOT_FOUND.value(),
                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                        HttpHeaders.EMPTY,
                        new byte[0],
                        null
                );

        when(runtimeApiClient.downloadFileContent(fileId))
                .thenThrow(originalException);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> runtimeService.downloadFileContent(fileId)
        );

        assertEquals(
                EvidenceErrorCode.RUNTIME_API_UNAVAILABLE,
                exception.getErrorCode()
        );
        assertSame(originalException, exception.getCause());
        assertEquals(
                HttpStatus.NOT_FOUND.value(),
                ((RestClientResponseException) exception.getCause())
                        .getStatusCode()
                        .value()
        );

        verify(runtimeApiClient).downloadFileContent(fileId);
    }

    @Test
    void downloadFileContent_mapsInvalidResponseToRuntimeApiUnavailable() {
        RuntimeApiClient runtimeApiClient = mock(RuntimeApiClient.class);
        RuntimeService runtimeService = new RuntimeService(runtimeApiClient);

        String fileId = "test-file-id";
        IllegalStateException originalException =
                new IllegalStateException("Runtime returned an empty response");

        when(runtimeApiClient.downloadFileContent(fileId))
                .thenThrow(originalException);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> runtimeService.downloadFileContent(fileId)
        );

        assertEquals(
                EvidenceErrorCode.RUNTIME_API_UNAVAILABLE,
                exception.getErrorCode()
        );
        assertSame(originalException, exception.getCause());

        verify(runtimeApiClient).downloadFileContent(fileId);
    }
}
