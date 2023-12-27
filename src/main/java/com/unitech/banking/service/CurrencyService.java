package com.unitech.banking.service;

import com.unitech.banking.model.dto.CurrencyRateRequest;
import com.unitech.banking.model.dto.CurrencyRateResponse;
import com.unitech.banking.model.enums.Currency;

import java.math.BigDecimal;

public interface CurrencyService {
    BigDecimal convert(Currency source, Currency target, BigDecimal amount);
    CurrencyRateResponse getRate(CurrencyRateRequest request);
}
