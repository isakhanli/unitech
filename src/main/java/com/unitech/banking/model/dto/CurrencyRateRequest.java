package com.unitech.banking.model.dto;


import com.unitech.banking.model.enums.Currency;
import lombok.*;
import lombok.experimental.SuperBuilder;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyRateRequest {
    private Currency base;
    private Currency quote;

    public CurrencyPair getPair(){
        return new CurrencyPair(base, quote);
    }
}
