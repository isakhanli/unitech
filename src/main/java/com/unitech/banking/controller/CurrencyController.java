package com.unitech.banking.controller;

import com.unitech.banking.model.dto.*;
import com.unitech.banking.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/currency")
@RestController
public class CurrencyController {
    private final CurrencyService currencyService;

    @GetMapping()
    public ResponseEntity<CurrencyRateResponse> get(@RequestBody CurrencyRateRequest request){
        var response = currencyService.getRate(request);
        return ResponseEntity.ok(response);
    }

}
