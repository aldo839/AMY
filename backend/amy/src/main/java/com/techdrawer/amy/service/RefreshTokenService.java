package com.techdrawer.amy.service;

import com.techdrawer.amy.exception.UserNotFoundException;
import com.techdrawer.amy.model.RefreshToken;
import com.techdrawer.amy.model.User;
import com.techdrawer.amy.repository.RefreshTokenRepository;
import com.techdrawer.amy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken createRefreshToken(Long userId){

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        RefreshToken refreshToken = refreshTokenRepository.findByUser(user)
                .orElse(new RefreshToken());

        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiration(Instant.now().plus(7, ChronoUnit.DAYS));
        refreshToken.setUser(user);

        return refreshTokenRepository.save(refreshToken);
    }

}
