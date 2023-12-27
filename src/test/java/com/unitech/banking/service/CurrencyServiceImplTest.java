package com.unitech.banking.service;

import com.unitech.banking.exception.UniTechBaseException;
import com.unitech.banking.model.dto.CurrencyPair;
import com.unitech.banking.model.dto.CurrencyRateRequest;
import com.unitech.banking.model.dto.CurrencyRateResponse;
import com.unitech.banking.model.enums.Currency;
import com.unitech.banking.model.enums.Response;
import com.unitech.banking.service.impl.CurrencyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CurrencyServiceImplTest {
    @Mock
    private ThirdPartyCurrencyRateProviderFacade thirdPartyCurrencyServiceFacade;

    @InjectMocks
    private CurrencyServiceImpl currencyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void convert_same_currency() {
        // given
        Currency source = Currency.USD;
        Currency target = Currency.USD;
        BigDecimal amount = BigDecimal.valueOf(100);

        //when
        BigDecimal result = currencyService.convert(source, target, amount);

        // then
        assertEquals(amount, result);
        verifyNoInteractions(thirdPartyCurrencyServiceFacade);
    }

    @Test
    void convert_success() {
        // given
        Currency source = Currency.USD;
        Currency target = Currency.AZN;
        BigDecimal amount = BigDecimal.valueOf(100);

        Map<CurrencyPair, BigDecimal> rates = new HashMap<>();
        rates.put(new CurrencyPair(target, source), BigDecimal.valueOf(1.7));

        // when
        when(thirdPartyCurrencyServiceFacade.getRates()).thenReturn(rates);

        // then
        BigDecimal result = currencyService.convert(source, target, amount);

        assertEquals(BigDecimal.valueOf(170.00), result);
        verify(thirdPartyCurrencyServiceFacade, times(1)).getRates();
    }

    @Test
    void convert_pair_not_found() {
        // given
        Currency source = Currency.USD;
        Currency target = Currency.EUR;
        BigDecimal amount = BigDecimal.valueOf(100);

        // when
        when(thirdPartyCurrencyServiceFacade.getRates()).thenReturn(new HashMap<>());

        // then
        UniTechBaseException exception = assertThrows(UniTechBaseException.class,
                () -> currencyService.convert(source, target, amount));

        assertEquals(Response.CURRENCY_PAIR_NOT_FOUND, exception.getResponse());
        verify(thirdPartyCurrencyServiceFacade, times(1)).getRates();
    }


    @Test
    void get_rate_with_valid_currency_pair() {
        // given
        CurrencyPair currencyPair = new CurrencyPair(Currency.USD, Currency.AZN);
        CurrencyRateRequest request = new CurrencyRateRequest(Currency.USD, Currency.AZN);

        BigDecimal expectedRate = BigDecimal.valueOf(1.7);
        Map<CurrencyPair, BigDecimal> rates = Map.of(currencyPair, expectedRate);
        when(thirdPartyCurrencyServiceFacade.getRates()).thenReturn(rates);

        // when
        CurrencyRateResponse response = currencyService.getRate(request);

        // then
        assertEquals(Response.SUCCESS.status(), response.getStatus());
        assertEquals(expectedRate, response.getRate());
    }
}