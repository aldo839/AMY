package com.techdrawer.amy.controller;

import com.techdrawer.amy.dto.*;
import com.techdrawer.amy.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register-artist")
    public ResponseEntity<UserResponseDTO> registerArtist(@RequestBody @Valid UserRegistrationDTO dto) {
        return new ResponseEntity<>(authService.registerArtist(dto), HttpStatus.CREATED);
    }

    @PostMapping("/register-gallery")
    public ResponseEntity<UserResponseDTO> registerGallery(@RequestBody @Valid UserRegistrationDTO dto) {
        return new ResponseEntity<>(authService.registerGallery(dto), HttpStatus.CREATED);
    }

    @PostMapping("/register-collector")
    public ResponseEntity<UserResponseDTO> registerCollector(@RequestBody @Valid UserRegistrationDTO dto) {
        return new ResponseEntity<>(authService.registerCollector(dto), HttpStatus.CREATED);
    }

    @PostMapping("/register-admin")
    public ResponseEntity<UserResponseDTO> registerAdmin(@RequestBody @Valid UserRegistrationDTO dto) {
        return new ResponseEntity<>(authService.registerAdmin(dto), HttpStatus.CREATED);
    }


    @PostMapping("/activate")
    public ResponseEntity<UserResponseDTO> activate(@RequestBody @Valid AccountActivationDTO dto) {
        return new ResponseEntity<>(authService.activate(dto), HttpStatus.ACCEPTED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO dto) {
        return new ResponseEntity<>(authService.login(dto), HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody @Valid LogoutRequestDTO dto) {

        authService.logout(dto.getRefreshToken());
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
