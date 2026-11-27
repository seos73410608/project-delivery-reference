package com.seos.common.exception;

import com.seos.common.exception.code.CommonErrorCode;
import com.seos.common.response.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("데이터 무결성 위반은 400 INVALID_REQUEST로 반환한다")
    void handleDataIntegrityViolation() {

        // given
        DataIntegrityViolationException exception =
                new DataIntegrityViolationException(
                        "Duplicate company role assignment"
                );

        // when
        ResponseEntity<ApiResponse<Void>> response =
                globalExceptionHandler.handleDataIntegrityViolation(exception);

        // then
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode())
                .isEqualTo(CommonErrorCode.INVALID_REQUEST.getCode());
    }

    @Test
    @DisplayName("필수 요청 파라미터 누락은 400 INVALID_REQUEST로 반환한다")
    void handleMissingRequestParameter() {

        // given
        MissingServletRequestParameterException exception =
                new MissingServletRequestParameterException(
                        "effectiveTo",
                        "LocalDate"
                );

        // when
        ResponseEntity<ApiResponse<Void>> response =
                globalExceptionHandler.handleMissingRequestParameter(exception);

        // then
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode())
                .isEqualTo(CommonErrorCode.INVALID_REQUEST.getCode());
    }
}