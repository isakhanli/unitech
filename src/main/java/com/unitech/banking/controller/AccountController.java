package com.unitech.banking.controller;

import com.unitech.banking.model.dto.AccountListResponse;
import com.unitech.banking.model.dto.TransferRequest;
import com.unitech.banking.model.dto.TransferResponse;
import com.unitech.banking.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
public class AccountController {
    private final AccountService accountService;

    @GetMapping("/accounts")
    public ResponseEntity<AccountListResponse> getAll(){
        var response = accountService.getActiveAccountList();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/account/transfer")
    public ResponseEntity<TransferResponse> transfer(@RequestBody @Valid TransferRequest request){
        var response = accountService.transfer(request);
        return ResponseEntity.ok(response);
    }

}
