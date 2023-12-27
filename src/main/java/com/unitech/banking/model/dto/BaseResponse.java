package com.unitech.banking.model.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
        "code",
        "message",
})
public class BaseResponse {
    private Status status;

    @EqualsAndHashCode
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class Status{
        private int code;
        private String message;
    }
}


