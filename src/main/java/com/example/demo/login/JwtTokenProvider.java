package com.example.demo.login;

import com.example.demo.user.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long expirationMs;
    private final String cookieName;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs,
            @Value("${jwt.cookie-name}") String cookieName) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
        this.cookieName = cookieName;
    }

    public String createToken(User user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(user.getUsername())
                .claim("userId", user.getId())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    public ResponseCookie createTokenCookie(User user) {
        return ResponseCookie.from(cookieName, createToken(user))
                .httpOnly(true)
                .path("/")
                .maxAge(Duration.ofMillis(expirationMs))
                .build();
    }
}
