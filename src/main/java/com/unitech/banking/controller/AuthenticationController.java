package com.unitech.banking.controller;

import com.unitech.banking.model.dto.BaseResponse;
import com.unitech.banking.model.dto.LoginRequest;
import com.unitech.banking.model.dto.LoginResponse;
import com.unitech.banking.model.dto.RegisterRequest;
import com.unitech.banking.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth/")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<BaseResponse> register(@RequestBody @Valid RegisterRequest request){
        var response = authenticationService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> register(@RequestBody @Valid LoginRequest request){
        var response = authenticationService.login(request);
        return ResponseEntity.ok(response);
    }
}
