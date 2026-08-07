package com.seos.pmis.common.exception;

import com.seos.pmis.common.exception.code.CommonErrorCode;
import com.seos.pmis.common.exception.code.ErrorCode;
import com.seos.pmis.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Business Exception
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(
            BusinessException exception
    ) {

        ErrorCode errorCode = exception.getErrorCode();

        log.warn(
                "[BusinessException] code={}, message={}",
                errorCode.getCode(),
                errorCode.getMessage()
        );

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.fail(errorCode));
    }

    /**
     * Access Denied (403)
     */
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthorizationDenied(
            AuthorizationDeniedException exception
    ) {

        log.warn("Access Denied");

        return ResponseEntity
                .status(CommonErrorCode.ACCESS_DENIED.getHttpStatus())
                .body(ApiResponse.fail(CommonErrorCode.ACCESS_DENIED));
    }

    /**
     * Unexpected Exception
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(
            Exception exception
    ) {

        log.error("Unexpected Exception", exception);

        return ResponseEntity
                .status(CommonErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus())
                .body(ApiResponse.fail(CommonErrorCode.INTERNAL_SERVER_ERROR));
    }

}