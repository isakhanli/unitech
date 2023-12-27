package com.unitech.banking.model.dto;

import com.unitech.banking.model.enums.Currency;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {
    // Basic validations are used here,
    // but in a real application, we'd implement more robust checks.

    @NotBlank(message = "Source account can not be empty")
    private String source;

    @NotBlank(message = "Target account can not be empty")
    private String target;

    @Positive(message = "Transfer amount can not be negative")
    private BigDecimal amount;

    private Currency currency;
}
