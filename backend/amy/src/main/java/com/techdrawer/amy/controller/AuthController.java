package com.techdrawer.amy.controller;

import com.techdrawer.amy.dto.UserRegistrationDTO;
import com.techdrawer.amy.dto.UserResponseDTO;
import com.techdrawer.amy.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
