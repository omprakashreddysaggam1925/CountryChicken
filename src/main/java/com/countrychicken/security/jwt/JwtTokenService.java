package com.countrychicken.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtTokenService {

    private final SecretKey signingKey;
    private final Duration accessTokenTtl;

    public JwtTokenService(
            @Value("${security.jwt.secret}") String encodedSecret,
            @Value("${security.jwt.access-token-ttl}") Duration accessTokenTtl
    ) {
        if (encodedSecret == null || encodedSecret.isBlank()) {
            throw new IllegalStateException("JWT_SECRET must be configured");
        }
        byte[] decodedSecret = Decoders.BASE64.decode(encodedSecret);
        if (decodedSecret.length < 32) {
            throw new IllegalStateException("JWT_SECRET must decode to at least 256 bits");
        }
        this.signingKey = Keys.hmacShaKeyFor(decodedSecret);
        this.accessTokenTtl = accessTokenTtl;
    }

    public String issueAccessToken(UUID userId, String email) {
        Instant issuedAt = Instant.now();
        return Jwts.builder()
                .subject(userId.toString())
                .claim("email", email)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(issuedAt.plus(accessTokenTtl)))
                .signWith(signingKey)
                .compact();
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token);
    }
}
