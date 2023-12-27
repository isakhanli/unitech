package com.unitech.banking.exception;

import com.unitech.banking.model.enums.Response;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UniTechBaseException extends RuntimeException{
    private Response response;
}
