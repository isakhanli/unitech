package com.unitech.banking.service;

import com.unitech.banking.model.dto.CurrencyPair;

import java.math.BigDecimal;
import java.util.Map;

public interface ThirdPartyCurrencyRateProviderFacade {
    Map<CurrencyPair, BigDecimal> getRates();
}
