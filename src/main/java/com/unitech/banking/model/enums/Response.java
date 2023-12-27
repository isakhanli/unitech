package com.unitech.banking.model.enums;

import com.unitech.banking.model.dto.BaseResponse;
import org.springframework.http.HttpStatus;

public enum Response {
    // 200s
    SUCCESS(1000, "Success", HttpStatus.OK),

    // 400s
    INVALID_REQUEST(2000, "Invalid request", HttpStatus.BAD_REQUEST),
    INVALID_PIN(2001, "Invalid pin", HttpStatus.BAD_REQUEST),
    DUPLICATE_PIN(2002, "Client with given PIN already registered", HttpStatus.CONFLICT),
    INVALID_CREDENTIALS(2004, "Invalid credentials", HttpStatus.BAD_REQUEST),
    SOURCE_ACCOUNT_NOT_FOUND(2005, "Account not found for the specified source account", HttpStatus.NOT_FOUND),
    SOURCE_ACCOUNT_INVALID_STATE(2006, "Source account is in invalid state", HttpStatus.BAD_REQUEST),
    SOURCE_ACCOUNT_INSUFFICIENT_FUND(2007, "Insufficient funds in the source account", HttpStatus.BAD_REQUEST),
    TARGET_ACCOUNT_NOT_FOUND(2008, "Account not found for the specified target account", HttpStatus.NOT_FOUND),
    TARGET_ACCOUNT_INVALID_STATE(2009, "Target account is in invalid state", HttpStatus.BAD_REQUEST),
    IDENTICAL_SOURCE_TARGET_ACCOUNTS(2010, "Identical source & target accounts", HttpStatus.CONFLICT),
    CURRENCY_PAIR_NOT_FOUND(2011, "Currency pair has not been found", HttpStatus.NOT_FOUND),

    // 500s
    INTERNAL_ERROR(3000, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);


    private int code;
    private String message;
    private HttpStatus httpStatus;

    Response(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public int getCode(){
        return this.code;
    }

    public String getMessage(){
        return this.message;
    }

    public HttpStatus getHttpStatus(){
        return this.httpStatus;
    }

    public BaseResponse.Status status(){
        return BaseResponse.Status.builder()
                .code(code)
                .message(message)
                .build();
    }
}
