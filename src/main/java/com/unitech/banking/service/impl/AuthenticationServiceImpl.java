package com.unitech.banking.service.impl;

import com.unitech.banking.exception.UniTechBaseException;
import com.unitech.banking.model.dto.BaseResponse;
import com.unitech.banking.model.dto.LoginRequest;
import com.unitech.banking.model.dto.LoginResponse;
import com.unitech.banking.model.dto.RegisterRequest;
import com.unitech.banking.model.entity.Account;
import com.unitech.banking.model.entity.Client;
import com.unitech.banking.model.enums.AccountState;
import com.unitech.banking.model.enums.Currency;
import com.unitech.banking.model.enums.Response;
import com.unitech.banking.repository.ClientRepository;
import com.unitech.banking.security.JwtService;
import com.unitech.banking.service.AuthenticationService;
import com.unitech.banking.service.PinService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Log4j2
@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final PinService pinService;
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final Random random = new Random();

    @Override
    public BaseResponse register(RegisterRequest request) {
        log.info("Received register request: {}", request);

        if (!pinService.isValid(request.getPin())){
            log.info("Register request: {} declined, since provided pin is invalid", request);
            throw new UniTechBaseException(Response.INVALID_PIN);
        }

        boolean existingPin = clientRepository.existsByPin(request.getPin());
        if (existingPin){
            log.info("Register request: {} declined, since client with given pin already registered", request);
            throw new UniTechBaseException(Response.DUPLICATE_PIN);
        }

        Client client = Client.builder()
                .pin(request.getPin())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        // The task description did not provide details on the process of creating and populating accounts.
        // Therefore, was included random account generation during the registration process
        client.setAccountList(generateRandomAccounts(client));

        clientRepository.save(client);

        log.info("Register request: {} processed successfully", request);

        return BaseResponse.builder()
                .status(Response.SUCCESS.status())
                .build();
    }

    @Override
    public LoginResponse login(LoginRequest request){
        log.info("Received login request: {}", request);

        authenticate(request.getPin(), request.getPassword());

        String accessToken = jwtService.generateToken(request.getPin());

        log.info("Login request: {} processed successfully", request);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .status(Response.SUCCESS.status())
                .build();
    }

    @Override
    public Client getAuthenticatedClient() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String pin =  (String) authentication.getPrincipal();

        Client client = clientRepository
                .findByPin(pin)
                .orElseThrow(() -> {
                    log.info("Failed to fetch client by pin: {} from the database.");
                    throw new UniTechBaseException(Response.INTERNAL_ERROR);
                });

        return client;
    }

    private void authenticate(String pin, String password){
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(pin, password));

        if (!authentication.isAuthenticated()) {
            log.info("Unsuccessful login request: {}, with invalid credentials", pin);
            throw new UniTechBaseException(Response.INVALID_CREDENTIALS);
        }
    }

    private List<Account> generateRandomAccounts(Client client){
        Currency[] availableCurrencies = Currency.values();

        int numOfAccountsToGenerate = random.nextInt(availableCurrencies.length) + 1;
        Set<Currency> randomlySelectedCurrencies = random.ints(0, availableCurrencies.length)
                .distinct()
                .limit(numOfAccountsToGenerate)
                .mapToObj(index -> availableCurrencies[index])
                .collect(Collectors.toSet());

        List<Account> accountList = new ArrayList<>();
        randomlySelectedCurrencies.forEach((currency -> {
            BigDecimal amount = BigDecimal.valueOf(random.nextDouble() * 10000);

            Account account = Account.builder()
                    .id(UUID.randomUUID().toString())
                    .amount(amount)
                    .currency(currency)
                    .client(client)
                    .status(AccountState.ACTIVE)
                    .build();

            accountList.add(account);
        }));

        return accountList;
    }

}
