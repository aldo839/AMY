package com.techdrawer.amy.dto;

public record LoginResponseDTO (

        UserResponseDTO userResponseDTO,
        String accessToken,
        String refreshToken

) {
}
