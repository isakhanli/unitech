package com.unitech.banking.exception;

import com.unitech.banking.model.dto.BaseResponse;
import com.unitech.banking.model.enums.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Optional;

@Log4j2
@RequiredArgsConstructor
@RestControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler({UniTechBaseException.class})
    public ResponseEntity<BaseResponse> handleRLinkException(UniTechBaseException exception) {
        var response = BaseResponse.builder()
                .status(exception.getResponse().status())
                .build();

        return new ResponseEntity<>(response, exception.getResponse().getHttpStatus());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<BaseResponse> handleValidationException(MethodArgumentNotValidException exception) {
        String description = Optional.ofNullable(exception.getFieldError())
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .get();

        BaseResponse.Status status = BaseResponse.Status.builder()
                .code(Response.INVALID_REQUEST.getCode())
                .message(description)
                .build();

        var response = BaseResponse.builder()
                .status(status)
                .build();

        return new ResponseEntity<>(response, Response.INVALID_REQUEST.getHttpStatus());
    }
}
