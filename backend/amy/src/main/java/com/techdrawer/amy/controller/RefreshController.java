package com.techdrawer.amy.controller;

import com.techdrawer.amy.model.RefreshToken;
import com.techdrawer.amy.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/refresh")
@RequiredArgsConstructor
public class RefreshController {

    private final RefreshTokenService refreshTokenService;

    @PostMapping("/{id}")
    public ResponseEntity<RefreshToken> refreshToken(@PathVariable Long userId) {
        return new ResponseEntity<>(refreshTokenService.createRefreshToken(userId), HttpStatus.OK);
    }
}
