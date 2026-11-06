package com.techdrawer.amy.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ResetPasswordRequestDTO {

    @NotBlank(message = "Validation code is required")
    private String code;

    @NotBlank(message = "Your new password is required")
    private String password;

}
