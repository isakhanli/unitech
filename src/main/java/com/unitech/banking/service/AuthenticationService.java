package com.unitech.banking.service;

import com.unitech.banking.model.dto.BaseResponse;
import com.unitech.banking.model.dto.LoginRequest;
import com.unitech.banking.model.dto.LoginResponse;
import com.unitech.banking.model.dto.RegisterRequest;
import com.unitech.banking.model.entity.Client;

public interface AuthenticationService {
    BaseResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    Client getAuthenticatedClient();
}
