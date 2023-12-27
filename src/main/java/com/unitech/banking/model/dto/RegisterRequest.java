package com.unitech.banking.model.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Size;

@ToString
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    // Basic validations are used here,
    // but in a real application, we'd implement more robust checks.

    @Size(min = 8, max = 8, message = "Bad pin")
    private String pin;

    @ToString.Exclude
    private String password;
}
