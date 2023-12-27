package com.unitech.banking.model.dto;

import com.unitech.banking.model.enums.Currency;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Objects;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyPair implements Serializable {
    private Currency base;
    private Currency quote;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CurrencyPair that)) return false;
        return getBase() == that.getBase() && getQuote() == that.getQuote();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBase(), getQuote());
    }
}
