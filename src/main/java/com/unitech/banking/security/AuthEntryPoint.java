package com.unitech.banking.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unitech.banking.model.dto.LoginResponse;
import com.unitech.banking.model.enums.Response;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Log4j2
@Component
public class AuthEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        LoginResponse failedLoginResponse = LoginResponse.builder()
                .status(Response.INVALID_CREDENTIALS.status())
                .build();

        response.getWriter().write(mapper.writeValueAsString(failedLoginResponse));
    }
}
