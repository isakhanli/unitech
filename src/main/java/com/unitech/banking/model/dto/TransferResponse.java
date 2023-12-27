package com.unitech.banking.model.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.unitech.banking.model.entity.Account;
import com.unitech.banking.model.enums.Currency;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@ToString
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
        "source",
        "code",
        "message",
})
public class TransferResponse extends BaseResponse {
    private Account source;
}
