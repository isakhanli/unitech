package com.unitech.banking.service.impl;

import com.unitech.banking.exception.UniTechBaseException;
import com.unitech.banking.model.dto.AccountListResponse;
import com.unitech.banking.model.dto.TransferRequest;
import com.unitech.banking.model.dto.TransferResponse;
import com.unitech.banking.model.entity.Account;
import com.unitech.banking.model.entity.Client;
import com.unitech.banking.model.enums.Response;
import com.unitech.banking.repository.AccountRepository;
import com.unitech.banking.service.AccountService;
import com.unitech.banking.service.AuthenticationService;
import com.unitech.banking.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Log4j2
@RequiredArgsConstructor
@Service
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final AuthenticationService authenticationService;
    private final CurrencyService currencyService;

    @Override
    public AccountListResponse getActiveAccountList() {
        log.info("Received get all accounts request");

        Client client = authenticationService.getAuthenticatedClient();

        List<Account> accountList = accountRepository.getActiveAccountList(client);

        log.info("Generated get all active accounts response");

        var response = AccountListResponse.builder()
                .accountList(accountList)
                .status(Response.SUCCESS.status())
                .build();

        log.info("Generated get all active accounts response: {}", response);

        return response;
    }

    @Override
    @Transactional
    public TransferResponse transfer(TransferRequest request) {
        log.info("Received transfer request: {}", request);

        Client client = authenticationService.getAuthenticatedClient();

        // get and validate source account
        Account sourceAccount = accountRepository
                .getByIdAndClient(request.getSource(), client)
                .orElseThrow(() -> {
                    log.info("Declined transfer request: {}, since requesting user does not have an account with specified id", request);
                    return new UniTechBaseException(Response.SOURCE_ACCOUNT_NOT_FOUND);
                });

        if (!sourceAccount.isActive()){
            log.info("Declined transfer request: {}, since source account is invalid state", request);
            throw new UniTechBaseException(Response.SOURCE_ACCOUNT_INVALID_STATE);
        }


        // get and validate target account
        Account targetAccount = accountRepository
                .findById(request.getTarget())
                .orElseThrow(() -> {
                    log.info("Declined transfer request: {}, since requested target account has not been found", request);
                    return new UniTechBaseException(Response.TARGET_ACCOUNT_NOT_FOUND);
                });

        if (!targetAccount.isActive()){
            log.info("Declined transfer request: {}, since target account is invalid state", request);
            throw new UniTechBaseException(Response.TARGET_ACCOUNT_INVALID_STATE);
        }

        // verify if the accounts are the same.
        if (sourceAccount.equals(targetAccount)){
            log.info("Declined transfer request: {}, since target and source accounts are identical", request);
            throw new UniTechBaseException(Response.IDENTICAL_SOURCE_TARGET_ACCOUNTS);
        }

        // check if transfer the amount exceeds the source account limits
        BigDecimal sourceAccountTransferAmount =
                currencyService.convert(request.getCurrency(), sourceAccount.getCurrency(), request.getAmount());

        if (!sourceAccount.isTransferAmountWithinLimits(sourceAccountTransferAmount)){
            log.info("Declined transfer request: {}, since requested amount exceeds source account limits", request);
            throw new UniTechBaseException(Response.SOURCE_ACCOUNT_INSUFFICIENT_FUND);
        }

        BigDecimal targetAccountTransferAmount =
                currencyService.convert(request.getCurrency(), targetAccount.getCurrency(), request.getAmount());

        // process transfer
        sourceAccount.subtract(sourceAccountTransferAmount);
        targetAccount.add(targetAccountTransferAmount);

        accountRepository.save(sourceAccount);
        accountRepository.save(targetAccount);

        var response =  TransferResponse.builder()
                .source(sourceAccount)
                .status(Response.SUCCESS.status())
                .build();

        log.info("Generated transfer response: {}", response);

        return response;
    }
}
