package com.unitech.banking.service;

import com.unitech.banking.exception.UniTechBaseException;
import com.unitech.banking.model.dto.LoginRequest;
import com.unitech.banking.model.dto.LoginResponse;
import com.unitech.banking.model.dto.RegisterRequest;
import com.unitech.banking.model.enums.Response;
import com.unitech.banking.repository.ClientRepository;
import com.unitech.banking.security.JwtService;
import com.unitech.banking.service.impl.AuthenticationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationServiceImplTest {
    @Mock
    private PinService pinService;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_success() {
        // given
        RegisterRequest request = new RegisterRequest("12345678", "password");

        // when
        when(pinService.isValid(request.getPin())).thenReturn(true);
        when(clientRepository.existsByPin(request.getPin())).thenReturn(false);
        authenticationService.register(request);

        // then
        verify(clientRepository, times(1)).save(any());
    }

    @Test
    void register_throws_invalidPin() {
        // given
        RegisterRequest request = new RegisterRequest("12345678", "password");

        // when
        when(pinService.isValid(request.getPin())).thenReturn(false);
        when(clientRepository.existsByPin(request.getPin())).thenReturn(false);

        // then
        UniTechBaseException exception = assertThrows(
                UniTechBaseException.class,
                () -> authenticationService.register(request)
        );

        assertEquals(Response.INVALID_PIN, exception.getResponse());
        verify(clientRepository, never()).save(any());
    }

    @Test
    void register_throws_duplicatePin() {
        // given
        RegisterRequest request = new RegisterRequest("12345678", "password");

        // when
        when(pinService.isValid(request.getPin())).thenReturn(true);
        when(clientRepository.existsByPin(request.getPin())).thenReturn(true);

        // then
        UniTechBaseException exception = assertThrows(
                UniTechBaseException.class,
                () -> authenticationService.register(request)
        );

        assertEquals(Response.DUPLICATE_PIN, exception.getResponse());
        verify(clientRepository, never()).save(any());
    }


    @Test
    void login_success() {
        //given
        String pin = "12345678";
        String password = "password";
        String accessToken = "accessToken";
        LoginRequest request = new LoginRequest(pin, password);

        // when
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(UsernamePasswordAuthenticationToken.authenticated("pin", null, List.of()));
        when(jwtService.generateToken(pin)).thenReturn(accessToken);

        // then
        LoginResponse response = authenticationService.login(request);

        assertEquals(Response.SUCCESS.status(), response.getStatus());
        assertEquals(accessToken, response.getAccessToken());
    }

    @Test
    void login_failed() {
        //given
        String pin = "12345678";
        String password = "password";
        LoginRequest request = new LoginRequest(pin, password);

        // when
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new UniTechBaseException(Response.INVALID_CREDENTIALS));

        // then
        UniTechBaseException exception = assertThrows(
                UniTechBaseException.class,
                () -> authenticationService.login(request)
        );

        assertEquals(Response.INVALID_CREDENTIALS, exception.getResponse());
    }

}