package com.techdrawer.amy.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtils {

    @Value("${app.jwt-secret-key}")
    private String SECRET;

    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(SECRET)
        );
    }

    public String generateToken(UserDetails userDetails) {

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(15, ChronoUnit.MILLIS)))
                .signWith(getSignKey())
                .compact();
    }

    public String extractUsername(String token) {

        return extractClaims(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {

        return extractClaims(token, Claims::getExpiration);
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {

        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {

        return Jwts
                .parser()
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Boolean isExpired(String token) {

        return extractExpiration(token).before(Date.from(Instant.now()));
    }

    public Boolean isValid(String token, UserDetails userDetails) {

        String username = userDetails.getUsername();
        return (extractUsername(token).equals(username) && !isExpired(token));
    }

}
