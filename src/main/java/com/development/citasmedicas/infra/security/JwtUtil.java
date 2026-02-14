package com.development.citasmedicas.infra.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtUtil {
    @Value("${api.jwt.secret}")
    private String secret;

    private SecretKey secretKey;

    private SecretKey getSecretKey() {
        if (secretKey == null) {
            secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        }
        return secretKey;
    }

    public String generateToken(String username, String extraClaim) {

        return Jwts.builder()
                .subject(username)
                .issuer("citas-medicas")
                .claim("role", extraClaim)
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plusSeconds(3600)))
                .signWith(getSecretKey())
                .compact();
    }

    private Claims getAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String getUsername(String token) {
        return getClaim(token, Claims::getSubject);
    }

    public Date getExpirationDate(String token) {
        return getClaim(token, Claims::getExpiration);
    }

    public String getRole(String token) {
        return getClaim(token, c -> c.get("role").toString());
    }

    public boolean validateToken(String token, String username) {
        return getUsername(token).equals(username) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        var expirationToken = getExpirationDate(token);
        return expirationToken.before(new Date());
    }
}
