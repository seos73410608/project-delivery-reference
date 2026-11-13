package com.seos.cmdb.server.exception;

import com.seos.common.exception.code.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ServerErrorCode implements ErrorCode {

    SERVER_CI_TYPE_REQUIRED(
            HttpStatus.BAD_REQUEST,
            "CMDB_SERVER_001",
            "서버 등록에는 SERVER 유형의 CI Type이 필요합니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ServerErrorCode(
            HttpStatus httpStatus,
            String code,
            String message
    ) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
