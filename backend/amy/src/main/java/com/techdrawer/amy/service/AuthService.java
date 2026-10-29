package com.techdrawer.amy.service;

import com.techdrawer.amy.dto.UserRegistrationDTO;
import com.techdrawer.amy.dto.UserResponseDTO;
import com.techdrawer.amy.model.Role;
import com.techdrawer.amy.model.User;
import com.techdrawer.amy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapperService userMapperService;

    // User registration
    public UserResponseDTO register(UserRegistrationDTO dto, Role role) {

        User user = new User();

        if (userRepository.existsByEmail(dto.getEmail())){
            throw new RuntimeException("Missing Credentials");
        }

        if (dto.getName() == null || dto.getName().isBlank()
                || dto.getEmail() == null || dto.getEmail().isBlank()
                || dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new IllegalArgumentException("Invalid registration data");
        }

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(role);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(null);

        User registeredArtist = userRepository.save(user);

        return userMapperService.mapToResponseDTO(registeredArtist);
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

}
