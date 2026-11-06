package com.techdrawer.amy.service;

import com.techdrawer.amy.config.JwtUtils;
import com.techdrawer.amy.dto.*;
import com.techdrawer.amy.exception.ExpiredCodeException;
import com.techdrawer.amy.exception.MissingArgumentException;
import com.techdrawer.amy.exception.UserAlreadyExistException;
import com.techdrawer.amy.exception.UserNotFoundException;
import com.techdrawer.amy.model.RefreshToken;
import com.techdrawer.amy.model.Role;
import com.techdrawer.amy.model.User;
import com.techdrawer.amy.model.Validation;
import com.techdrawer.amy.repository.RefreshTokenRepository;
import com.techdrawer.amy.repository.UserRepository;
import com.techdrawer.amy.repository.ValidationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtils jwtUtils;
    private final RefreshTokenRepository refreshTokenRepository;

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

        validationRepository.delete(validation);

        return userMapperService.mapToResponseDTO(activatedUser);
    }


    // User login
    public LoginResponseDTO login(LoginRequestDTO dto) {

        if (dto.getEmail().isBlank() || dto.getPassword().isBlank()){
            throw new MissingArgumentException("Missing email or password");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        assert userDetails != null;
        String accessToken = jwtUtils.generateToken(userDetails);

        User loggedUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        UserResponseDTO user = userMapperService.mapToResponseDTO(loggedUser);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(loggedUser.getId());

        return new LoginResponseDTO(user, accessToken, refreshToken.getToken());
    }


    // User logout
    public void logout(String tokenValue) {

        RefreshToken refreshToken = refreshTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new UserNotFoundException("Token not found, please login"));

        refreshTokenRepository.delete(refreshToken);
    }


    // Fetch current user
    public User getCurrentUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadCredentialsException("User is not authenticated");
        }

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }


    // Reset password
    public void resetPasswordRequest(ForgetPasswordRequestDTO dto) {

        if (dto.getEmail().isBlank()) {
            throw new MissingArgumentException("Missing information's");
        }

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        validationService.passwordResetCode(user);
    }

    public void updatePassword(ResetPasswordRequestDTO dto) {

        if (dto.getCode().isBlank() || dto.getPassword().isBlank()) {
            throw new MissingArgumentException("Missing information's");
        }

        Validation validation = validationRepository.findByCode(dto.getCode())
                .orElseThrow(() -> new UserNotFoundException("Code not found"));

        User user = validation.getUser();

        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        userRepository.save(user);

        validationRepository.delete(validation);
    }

}
