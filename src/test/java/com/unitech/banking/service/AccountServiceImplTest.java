package com.unitech.banking.service;

import com.unitech.banking.exception.UniTechBaseException;
import com.unitech.banking.model.dto.AccountListResponse;
import com.unitech.banking.model.dto.TransferRequest;
import com.unitech.banking.model.dto.TransferResponse;
import com.unitech.banking.model.entity.Account;
import com.unitech.banking.model.entity.Client;
import com.unitech.banking.model.enums.AccountState;
import com.unitech.banking.model.enums.Currency;
import com.unitech.banking.model.enums.Response;
import com.unitech.banking.repository.AccountRepository;
import com.unitech.banking.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private CurrencyService currencyService;

    @InjectMocks
    private AccountServiceImpl accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void get_active_account_list() {
        // given
        Client authenticatedClient = new Client();
        when(authenticationService.getAuthenticatedClient()).thenReturn(authenticatedClient);

        List<Account> accountList = List.of(
                new Account(UUID.randomUUID().toString(), authenticatedClient, BigDecimal.valueOf(1000), Currency.USD, AccountState.ACTIVE),
                new Account(UUID.randomUUID().toString(), authenticatedClient, BigDecimal.valueOf(200), Currency.AZN, AccountState.ACTIVE),
                new Account(UUID.randomUUID().toString(), authenticatedClient, BigDecimal.valueOf(300), Currency.EUR, AccountState.ACTIVE)
        );

        // when
        when(accountRepository.getActiveAccountList(authenticatedClient)).thenReturn(accountList);

        // then
        AccountListResponse response = accountService.getActiveAccountList();

        assertEquals(Response.SUCCESS.status(), response.getStatus());
        assertEquals(accountList, response.getAccountList());
        verify(accountRepository, times(1)).getActiveAccountList(authenticatedClient);
    }

    @Test
    void transfer_identical_currency_success() {
        // given
        Client authenticatedClient = new Client();

        BigDecimal transferAmount = BigDecimal.valueOf(100);
        BigDecimal transferAmountInSourceAccountCurrency = BigDecimal.valueOf(100);
        BigDecimal transferAmountInSourceTargetCurrency = BigDecimal.valueOf(100);
        Currency transferCurrency = Currency.USD;

        // source account details
        BigDecimal initialSourceAccountAmount = BigDecimal.valueOf(1000);
        Currency sourceAccountCurrency = Currency.USD;
        String sourceAccountId = UUID.randomUUID().toString();
        AccountState sourceAccountState = AccountState.ACTIVE;
        Client sourceAccountOwner = authenticatedClient;

        Account sourceAccount = Account.builder()
                .id(sourceAccountId)
                .client(sourceAccountOwner)
                .amount(initialSourceAccountAmount)
                .currency(sourceAccountCurrency)
                .status(sourceAccountState)
                .build();

        // target account details
        BigDecimal initialTargetAccountAmount = BigDecimal.valueOf(2000);
        Currency targetAccountCurrency = Currency.USD;
        String targetAccountId = UUID.randomUUID().toString();
        AccountState targetAccountState = AccountState.ACTIVE;
        Client targetAccountOwner = new Client();

        Account targetAccount = Account.builder()
                .id(targetAccountId)
                .client(targetAccountOwner)
                .amount(initialTargetAccountAmount)
                .currency(targetAccountCurrency)
                .status(targetAccountState)
                .build();

        TransferRequest request = new TransferRequest(sourceAccount.getId(), targetAccount.getId(), transferAmount, transferCurrency);

        //when
        when(authenticationService.getAuthenticatedClient()).thenReturn(authenticatedClient);

        // fetch source account
        when(accountRepository.getByIdAndClient(request.getSource(), authenticatedClient)).thenReturn(Optional.of(sourceAccount));

        // fetch target account
        when(accountRepository.findById(request.getTarget())).thenReturn(Optional.of(targetAccount));


        when(currencyService.convert(transferCurrency, sourceAccountCurrency, transferAmount)).thenReturn(transferAmountInSourceAccountCurrency);
        when(currencyService.convert(transferCurrency, targetAccountCurrency, transferAmount)).thenReturn(transferAmountInSourceTargetCurrency);

        // then
        TransferResponse response = accountService.transfer(request);

        assertEquals(Response.SUCCESS.status(), response.getStatus());
        assertEquals(initialSourceAccountAmount.subtract(transferAmountInSourceAccountCurrency), sourceAccount.getAmount());
        assertEquals(initialTargetAccountAmount.add(transferAmountInSourceTargetCurrency), targetAccount.getAmount());
        verify(accountRepository, times(2)).save(any());
    }

    @Test
    void transfer_different_currency_success() {
        // given
        Client authenticatedClient = new Client();

        BigDecimal transferAmount = BigDecimal.valueOf(100);
        BigDecimal transferAmountInSourceAccountCurrency = BigDecimal.valueOf(170);
        BigDecimal transferAmountInSourceTargetCurrency = BigDecimal.valueOf(95);
        Currency transferCurrency = Currency.USD;

        // source account details
        BigDecimal initialSourceAccountAmount = BigDecimal.valueOf(1000);
        Currency sourceAccountCurrency = Currency.EUR;
        String sourceAccountId = UUID.randomUUID().toString();
        AccountState sourceAccountState = AccountState.ACTIVE;
        Client sourceAccountOwner = authenticatedClient;

        Account sourceAccount = Account.builder()
                .id(sourceAccountId)
                .client(sourceAccountOwner)
                .amount(initialSourceAccountAmount)
                .currency(sourceAccountCurrency)
                .status(sourceAccountState)
                .build();

        // target account details
        BigDecimal initialTargetAccountAmount = BigDecimal.valueOf(2000);
        Currency targetAccountCurrency = Currency.AZN;
        String targetAccountId = UUID.randomUUID().toString();
        AccountState targetAccountState = AccountState.ACTIVE;
        Client targetAccountOwner = new Client();

        Account targetAccount = Account.builder()
                .id(targetAccountId)
                .client(targetAccountOwner)
                .amount(initialTargetAccountAmount)
                .currency(targetAccountCurrency)
                .status(targetAccountState)
                .build();

        TransferRequest request = new TransferRequest(sourceAccount.getId(), targetAccount.getId(), transferAmount, transferCurrency);

        //when
        when(authenticationService.getAuthenticatedClient()).thenReturn(authenticatedClient);

        // fetch source account
        when(accountRepository.getByIdAndClient(request.getSource(), authenticatedClient)).thenReturn(Optional.of(sourceAccount));

        // fetch target account
        when(accountRepository.findById(request.getTarget())).thenReturn(Optional.of(targetAccount));


        when(currencyService.convert(transferCurrency, sourceAccountCurrency, transferAmount)).thenReturn(transferAmountInSourceAccountCurrency);
        when(currencyService.convert(transferCurrency, targetAccountCurrency, transferAmount)).thenReturn(transferAmountInSourceTargetCurrency);

        // then
        TransferResponse response = accountService.transfer(request);

        assertEquals(Response.SUCCESS.status(), response.getStatus());
        assertEquals(initialSourceAccountAmount.subtract(transferAmountInSourceAccountCurrency), sourceAccount.getAmount());
        assertEquals(initialTargetAccountAmount.add(transferAmountInSourceTargetCurrency), targetAccount.getAmount());
        verify(accountRepository, times(2)).save(any());
    }

    @Test
    void transfer_with_insufficient_funds() {
        // given
        Client authenticatedClient = new Client();

        BigDecimal transferAmount = BigDecimal.valueOf(1000);
        BigDecimal transferAmountInSourceAccountCurrency = BigDecimal.valueOf(1100);
        BigDecimal transferAmountInSourceTargetCurrency = BigDecimal.valueOf(95);
        Currency transferCurrency = Currency.USD;

        // source account details
        BigDecimal initialSourceAccountAmount = BigDecimal.valueOf(900);
        Currency sourceAccountCurrency = Currency.EUR;
        String sourceAccountId = UUID.randomUUID().toString();
        AccountState sourceAccountState = AccountState.ACTIVE;
        Client sourceAccountOwner = authenticatedClient;

        Account sourceAccount = Account.builder()
                .id(sourceAccountId)
                .client(sourceAccountOwner)
                .amount(initialSourceAccountAmount)
                .currency(sourceAccountCurrency)
                .status(sourceAccountState)
                .build();

        // target account details
        BigDecimal initialTargetAccountAmount = BigDecimal.valueOf(2000);
        Currency targetAccountCurrency = Currency.AZN;
        String targetAccountId = UUID.randomUUID().toString();
        AccountState targetAccountState = AccountState.ACTIVE;
        Client targetAccountOwner = new Client();

        Account targetAccount = Account.builder()
                .id(targetAccountId)
                .client(targetAccountOwner)
                .amount(initialTargetAccountAmount)
                .currency(targetAccountCurrency)
                .status(targetAccountState)
                .build();

        TransferRequest request = new TransferRequest(sourceAccount.getId(), targetAccount.getId(), transferAmount, transferCurrency);

        //when
        when(authenticationService.getAuthenticatedClient()).thenReturn(authenticatedClient);

        // fetch source account
        when(accountRepository.getByIdAndClient(request.getSource(), authenticatedClient)).thenReturn(Optional.of(sourceAccount));

        // fetch target account
        when(accountRepository.findById(request.getTarget())).thenReturn(Optional.of(targetAccount));

        when(currencyService.convert(transferCurrency, sourceAccountCurrency, transferAmount)).thenReturn(transferAmountInSourceAccountCurrency);
        when(currencyService.convert(transferCurrency, targetAccountCurrency, transferAmount)).thenReturn(transferAmountInSourceTargetCurrency);

        // then
        UniTechBaseException exception = assertThrows(
                UniTechBaseException.class,
                () -> accountService.transfer(request)
        );

        assertEquals(exception.getResponse(), Response.SOURCE_ACCOUNT_INSUFFICIENT_FUND);
        verify(accountRepository, times(0)).save(any());
    }

    @Test
    void transfer_to_same_account() {
        // given
        Client authenticatedClient = new Client();

        BigDecimal transferAmount = BigDecimal.valueOf(100);
        BigDecimal transferAmountInSourceAccountCurrency = BigDecimal.valueOf(170);
        BigDecimal transferAmountInSourceTargetCurrency = BigDecimal.valueOf(95);
        Currency transferCurrency = Currency.USD;

        // source account details
        BigDecimal initialSourceAccountAmount = BigDecimal.valueOf(1000);
        Currency sourceAccountCurrency = Currency.EUR;
        String sourceAccountId = UUID.randomUUID().toString();
        AccountState sourceAccountState = AccountState.ACTIVE;
        Client sourceAccountOwner = authenticatedClient;

        Account sourceAccount = Account.builder()
                .id(sourceAccountId)
                .client(sourceAccountOwner)
                .amount(initialSourceAccountAmount)
                .currency(sourceAccountCurrency)
                .status(sourceAccountState)
                .build();

        // target account details
        Account targetAccount = sourceAccount;

        TransferRequest request = new TransferRequest(sourceAccount.getId(), targetAccount.getId(), transferAmount, transferCurrency);

        //when
        when(authenticationService.getAuthenticatedClient()).thenReturn(authenticatedClient);

        // fetch source account
        when(accountRepository.getByIdAndClient(request.getSource(), authenticatedClient)).thenReturn(Optional.of(sourceAccount));

        // fetch target account
        when(accountRepository.findById(request.getTarget())).thenReturn(Optional.of(targetAccount));


        when(currencyService.convert(transferCurrency, sourceAccount.getCurrency(), transferAmount)).thenReturn(transferAmountInSourceAccountCurrency);
        when(currencyService.convert(transferCurrency, targetAccount.getCurrency(), transferAmount)).thenReturn(transferAmountInSourceTargetCurrency);

        // then
        UniTechBaseException exception = assertThrows(
                UniTechBaseException.class,
                () -> accountService.transfer(request)
        );

        assertEquals(exception.getResponse(), Response.IDENTICAL_SOURCE_TARGET_ACCOUNTS);
        verify(accountRepository, times(0)).save(any());
    }

    @Test
    void transfer_to_inactive_account() {
        // given
        Client authenticatedClient = new Client();

        BigDecimal transferAmount = BigDecimal.valueOf(100);
        BigDecimal transferAmountInSourceAccountCurrency = BigDecimal.valueOf(170);
        BigDecimal transferAmountInSourceTargetCurrency = BigDecimal.valueOf(95);
        Currency transferCurrency = Currency.USD;

        // source account details
        BigDecimal initialSourceAccountAmount = BigDecimal.valueOf(1000);
        Currency sourceAccountCurrency = Currency.EUR;
        String sourceAccountId = UUID.randomUUID().toString();
        AccountState sourceAccountState = AccountState.ACTIVE;
        Client sourceAccountOwner = authenticatedClient;

        Account sourceAccount = Account.builder()
                .id(sourceAccountId)
                .client(sourceAccountOwner)
                .amount(initialSourceAccountAmount)
                .currency(sourceAccountCurrency)
                .status(sourceAccountState)
                .build();

        // target account details
        BigDecimal initialTargetAccountAmount = BigDecimal.valueOf(2000);
        Currency targetAccountCurrency = Currency.AZN;
        String targetAccountId = UUID.randomUUID().toString();
        AccountState targetAccountState = AccountState.CLOSED;
        Client targetAccountOwner = new Client();

        Account targetAccount = Account.builder()
                .id(targetAccountId)
                .client(targetAccountOwner)
                .amount(initialTargetAccountAmount)
                .currency(targetAccountCurrency)
                .status(targetAccountState)
                .build();

        TransferRequest request = new TransferRequest(sourceAccount.getId(), targetAccount.getId(), transferAmount, transferCurrency);

        //when
        when(authenticationService.getAuthenticatedClient()).thenReturn(authenticatedClient);

        // fetch source account
        when(accountRepository.getByIdAndClient(request.getSource(), authenticatedClient)).thenReturn(Optional.of(sourceAccount));

        // fetch target account
        when(accountRepository.findById(request.getTarget())).thenReturn(Optional.of(targetAccount));


        when(currencyService.convert(transferCurrency, sourceAccountCurrency, transferAmount)).thenReturn(transferAmountInSourceAccountCurrency);
        when(currencyService.convert(transferCurrency, targetAccountCurrency, transferAmount)).thenReturn(transferAmountInSourceTargetCurrency);

        // then
        UniTechBaseException exception = assertThrows(
                UniTechBaseException.class,
                () -> accountService.transfer(request)
        );

        assertEquals(exception.getResponse(), Response.TARGET_ACCOUNT_INVALID_STATE);
        verify(accountRepository, times(0)).save(any());
    }

    @Test
    void transfer_from_inactive_account() {
        // given
        Client authenticatedClient = new Client();

        BigDecimal transferAmount = BigDecimal.valueOf(100);
        BigDecimal transferAmountInSourceAccountCurrency = BigDecimal.valueOf(170);
        BigDecimal transferAmountInSourceTargetCurrency = BigDecimal.valueOf(95);
        Currency transferCurrency = Currency.USD;

        // source account details
        BigDecimal initialSourceAccountAmount = BigDecimal.valueOf(1000);
        Currency sourceAccountCurrency = Currency.EUR;
        String sourceAccountId = UUID.randomUUID().toString();
        AccountState sourceAccountState = AccountState.FROZEN;
        Client sourceAccountOwner = authenticatedClient;

        Account sourceAccount = Account.builder()
                .id(sourceAccountId)
                .client(sourceAccountOwner)
                .amount(initialSourceAccountAmount)
                .currency(sourceAccountCurrency)
                .status(sourceAccountState)
                .build();

        // target account details
        BigDecimal initialTargetAccountAmount = BigDecimal.valueOf(2000);
        Currency targetAccountCurrency = Currency.AZN;
        String targetAccountId = UUID.randomUUID().toString();
        AccountState targetAccountState = AccountState.ACTIVE;
        Client targetAccountOwner = new Client();

        Account targetAccount = Account.builder()
                .id(targetAccountId)
                .client(targetAccountOwner)
                .amount(initialTargetAccountAmount)
                .currency(targetAccountCurrency)
                .status(targetAccountState)
                .build();

        TransferRequest request = new TransferRequest(sourceAccount.getId(), targetAccount.getId(), transferAmount, transferCurrency);

        //when
        when(authenticationService.getAuthenticatedClient()).thenReturn(authenticatedClient);

        // fetch source account
        when(accountRepository.getByIdAndClient(request.getSource(), authenticatedClient)).thenReturn(Optional.of(sourceAccount));

        // fetch target account
        when(accountRepository.findById(request.getTarget())).thenReturn(Optional.of(targetAccount));


        when(currencyService.convert(transferCurrency, sourceAccountCurrency, transferAmount)).thenReturn(transferAmountInSourceAccountCurrency);
        when(currencyService.convert(transferCurrency, targetAccountCurrency, transferAmount)).thenReturn(transferAmountInSourceTargetCurrency);

        // then
        UniTechBaseException exception = assertThrows(
                UniTechBaseException.class,
                () -> accountService.transfer(request)
        );

        assertEquals(exception.getResponse(), Response.SOURCE_ACCOUNT_INVALID_STATE);
        verify(accountRepository, times(0)).save(any());
    }

    @Test
    void transfer_to_non_existing_account() {
        // given
        Client authenticatedClient = new Client();

        BigDecimal transferAmount = BigDecimal.valueOf(100);
        Currency transferCurrency = Currency.USD;

        // source account details
        BigDecimal initialSourceAccountAmount = BigDecimal.valueOf(1000);
        Currency sourceAccountCurrency = Currency.EUR;
        String sourceAccountId = UUID.randomUUID().toString();
        AccountState sourceAccountState = AccountState.ACTIVE;
        Client sourceAccountOwner = authenticatedClient;

        Account sourceAccount = Account.builder()
                .id(sourceAccountId)
                .client(sourceAccountOwner)
                .amount(initialSourceAccountAmount)
                .currency(sourceAccountCurrency)
                .status(sourceAccountState)
                .build();

        // target account details


        TransferRequest request = new TransferRequest(sourceAccount.getId(), "non-existing-target-account-id", transferAmount, transferCurrency);

        //when
        when(authenticationService.getAuthenticatedClient()).thenReturn(authenticatedClient);

        // fetch source account
        when(accountRepository.getByIdAndClient(request.getSource(), authenticatedClient)).thenReturn(Optional.of(sourceAccount));

        // fetch target account
        when(accountRepository.findById(request.getTarget())).thenThrow(new UniTechBaseException(Response.TARGET_ACCOUNT_NOT_FOUND));

        // then
        UniTechBaseException exception = assertThrows(
                UniTechBaseException.class,
                () -> accountService.transfer(request)
        );

        assertEquals(exception.getResponse(), Response.TARGET_ACCOUNT_NOT_FOUND);
        verify(accountRepository, times(0)).save(any());
    }

    @Test
    void transfer_from_non_existing_account() {
        // given
        Client authenticatedClient = new Client();

        BigDecimal transferAmount = BigDecimal.valueOf(100);
        Currency transferCurrency = Currency.USD;

        // target account details
        BigDecimal initialTargetAccountAmount = BigDecimal.valueOf(2000);
        Currency targetAccountCurrency = Currency.AZN;
        String targetAccountId = UUID.randomUUID().toString();
        AccountState targetAccountState = AccountState.ACTIVE;
        Client targetAccountOwner = new Client();

        Account targetAccount = Account.builder()
                .id(targetAccountId)
                .client(targetAccountOwner)
                .amount(initialTargetAccountAmount)
                .currency(targetAccountCurrency)
                .status(targetAccountState)
                .build();

        TransferRequest request = new TransferRequest("non-existing-source-account-id", targetAccount.getId(), transferAmount, transferCurrency);

        //when
        when(authenticationService.getAuthenticatedClient()).thenReturn(authenticatedClient);

        // fetch source account
        when(accountRepository.getByIdAndClient(request.getSource(), authenticatedClient)).thenThrow(new UniTechBaseException(Response.SOURCE_ACCOUNT_NOT_FOUND));

        // fetch target account
        when(accountRepository.findById(request.getTarget())).thenReturn(Optional.of(targetAccount));


        // then
        UniTechBaseException exception = assertThrows(
                UniTechBaseException.class,
                () -> accountService.transfer(request)
        );

        assertEquals(exception.getResponse(), Response.SOURCE_ACCOUNT_NOT_FOUND);
        verify(accountRepository, times(0)).save(any());
    }




}