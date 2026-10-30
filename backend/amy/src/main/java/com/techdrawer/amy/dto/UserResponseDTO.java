package com.techdrawer.amy.dto;

import com.techdrawer.amy.model.Role;

import java.util.UUID;

public record UserResponseDTO(

        UUID publicId,
        String name,
        String email,
        Role role,
        Boolean isActivated,
        Boolean isValidated

) {
}
