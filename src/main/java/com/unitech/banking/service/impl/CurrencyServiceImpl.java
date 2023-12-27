package com.unitech.banking.service.impl;

import com.unitech.banking.exception.UniTechBaseException;
import com.unitech.banking.model.dto.CurrencyPair;
import com.unitech.banking.model.dto.CurrencyRateRequest;
import com.unitech.banking.model.dto.CurrencyRateResponse;
import com.unitech.banking.model.enums.Currency;
import com.unitech.banking.model.enums.Response;
import com.unitech.banking.service.CurrencyService;
import com.unitech.banking.service.ThirdPartyCurrencyRateProviderFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@Log4j2
@RequiredArgsConstructor
@Service
public class CurrencyServiceImpl implements CurrencyService {
    private final ThirdPartyCurrencyRateProviderFacade thirdPartyCurrencyServiceFacade;
    @Override
    public BigDecimal convert(Currency source, Currency target, BigDecimal amount) {
        log.info("Received currency convert request, amount: {}, source: {}, target: {}", amount, source, target);

        if (target.equals(source)) {
            return  amount;
        }

        Map<CurrencyPair, BigDecimal> rates = thirdPartyCurrencyServiceFacade.getRates();

        BigDecimal rate = Optional.ofNullable(rates.get(new CurrencyPair(target, source)))
                .orElseThrow(() -> {
                    log.info("Currency convert request declined, since specified source: {} and target: {} currency pair has not been found", source, target);
                    return new UniTechBaseException(Response.CURRENCY_PAIR_NOT_FOUND);
                });

        BigDecimal convertedAmount = rate.multiply(amount);

        log.info("Currency converted, converted amount: {}, rate: {}, initial amount: {}, source: {}, target: {}", convertedAmount, rate, amount, source, target);

        return convertedAmount;
    }

    @Override
    public CurrencyRateResponse getRate(CurrencyRateRequest request) {
        log.info("Received currency rate request: {}", request);

        Map<CurrencyPair, BigDecimal> rates = thirdPartyCurrencyServiceFacade.getRates();

        BigDecimal rate = Optional.ofNullable(rates.get(request.getPair()))
                .orElseThrow(() -> {
                    log.info("Currency rate request: {} declined, since specified currency pair has not been found", request);
                    return new UniTechBaseException(Response.CURRENCY_PAIR_NOT_FOUND);
                });

        var response =  CurrencyRateResponse.builder()
                .rate(rate)
                .status(Response.SUCCESS.status())
                .build();

        log.info("Generated currency rate response: {}", request);

        return response;
    }
}
