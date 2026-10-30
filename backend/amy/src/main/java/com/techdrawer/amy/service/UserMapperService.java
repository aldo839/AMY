package com.techdrawer.amy.service;

import com.techdrawer.amy.dto.UserResponseDTO;
import com.techdrawer.amy.model.User;
import org.springframework.stereotype.Service;

@Service
public class UserMapperService {

    public UserResponseDTO mapToResponseDTO (User user) {

        return new UserResponseDTO(
                user.getPublicId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getIsActivated(),
                user.getIsValidated()
        );
    }
}
