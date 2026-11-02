package com.techdrawer.amy.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LogoutRequestDTO {

    @NotBlank(message = "refreshToken is required")
    private String refreshToken;

}
