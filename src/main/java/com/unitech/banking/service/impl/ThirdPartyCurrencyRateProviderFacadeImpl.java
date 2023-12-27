package com.unitech.banking.service.impl;

import com.unitech.banking.model.dto.CurrencyPair;
import com.unitech.banking.model.enums.Currency;
import com.unitech.banking.service.ThirdPartyCurrencyRateProviderFacade;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Random;

@Log4j2
@Service
public class ThirdPartyCurrencyRateProviderFacadeImpl implements ThirdPartyCurrencyRateProviderFacade {
    private Map<CurrencyPair, BigDecimal> currencyRateMap;
    private final Random random = new Random();

    // the cache expiration time can be modified in the application.yml file.
    @Cacheable(cacheNames = "currency-rates")
    @Override
    public Map<CurrencyPair, BigDecimal> getRates(){
        //  third party service api call to update rates
        currencyRateMap = updateRates();

        return currencyRateMap;
    }

    private Map<CurrencyPair, BigDecimal> updateRates(){
        log.info("Third party currency api call was made");

        Map<CurrencyPair, BigDecimal> currencyRateMap = Map.of(
            new CurrencyPair(Currency.USD, Currency.AZN), generateRandomCurrencyRate(1.65, 1.80),
            new CurrencyPair(Currency.AZN, Currency.USD), generateRandomCurrencyRate(0.55, 0.80),

            new CurrencyPair(Currency.EUR, Currency.AZN), generateRandomCurrencyRate(1.7, 1.9),
            new CurrencyPair(Currency.AZN, Currency.EUR), generateRandomCurrencyRate(0.52, 0.70),

            new CurrencyPair(Currency.EUR, Currency.USD), generateRandomCurrencyRate(1.10, 1.20),
            new CurrencyPair(Currency.USD, Currency.EUR), generateRandomCurrencyRate(0.90, 1.05)
        );

        return currencyRateMap;
    }

    private BigDecimal generateRandomCurrencyRate(double min, double max) {
        return BigDecimal
                .valueOf(min + (max - min) * random.nextDouble())
                .setScale(3, RoundingMode.HALF_UP);
    }
}
