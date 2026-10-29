package com.techdrawer.amy.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserRegistrationDTO {

    @NotBlank(message = "Name is require")
    @Size(min = 2, max = 50, message = "Name must have between 2 and 50 characters")
    private String name;

    @NotBlank(message = "Name is require")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Name is require")
    @Size(min = 8, message = "Password must have minimum 8 characters")
    private String password;

}
