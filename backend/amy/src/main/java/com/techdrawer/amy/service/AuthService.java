package com.techdrawer.amy.service;

import com.techdrawer.amy.dto.AccountActivationDTO;
import com.techdrawer.amy.dto.UserRegistrationDTO;
import com.techdrawer.amy.dto.UserResponseDTO;
import com.techdrawer.amy.exception.ExpiredCodeException;
import com.techdrawer.amy.exception.MissingArgumentException;
import com.techdrawer.amy.exception.UserAlreadyExistException;
import com.techdrawer.amy.exception.UserNotFoundException;
import com.techdrawer.amy.model.Role;
import com.techdrawer.amy.model.User;
import com.techdrawer.amy.model.Validation;
import com.techdrawer.amy.repository.UserRepository;
import com.techdrawer.amy.repository.ValidationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapperService userMapperService;
    private final ValidationService validationService;
    private final ValidationRepository validationRepository;

    // User registration
    public UserResponseDTO register(UserRegistrationDTO dto, Role role) {

        User user = new User();

        if (userRepository.existsByEmail(dto.getEmail())){
            throw new UserAlreadyExistException("Email already registered");
        }

        if (dto.getName() == null || dto.getName().isBlank()
                || dto.getEmail() == null || dto.getEmail().isBlank()
                || dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new MissingArgumentException("Invalid registration data");
        }

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(role);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(null);

        User registeredUser = userRepository.save(user);

        validationService.activationCode(registeredUser);

        return userMapperService.mapToResponseDTO(registeredUser);
    }

    public UserResponseDTO registerArtist(UserRegistrationDTO dto) {
        return register(dto, Role.ARTIST);
    }

    public UserResponseDTO registerGallery(UserRegistrationDTO dto) {
        return register(dto, Role.GALLERY);
    }

    public UserResponseDTO registerCollector(UserRegistrationDTO dto) {
        return register(dto, Role.COLLECTOR);
    }

    public UserResponseDTO registerAdmin(UserRegistrationDTO dto) {
        return register(dto, Role.ADMIN);
    }


    // Activate User Account
    public UserResponseDTO activate(AccountActivationDTO dto) {

        if (dto == null || dto.getCode().isBlank()) {
            throw new MissingArgumentException("Missing activation code");
        }

        Validation validation = validationRepository.findByCode(dto.getCode())
                .orElseThrow(() -> new UserNotFoundException("Activation code not found"));

        if (validation.getExpiration().isBefore(Instant.now())) {
            throw new ExpiredCodeException("Activation code has expired");
        }

        User user = validation.getUser();

        user.setIsActivated(true);
        user.setUpdatedAt(LocalDateTime.now());

        User activatedUser = userRepository.save(user);

        return userMapperService.mapToResponseDTO(activatedUser);
    }

}
