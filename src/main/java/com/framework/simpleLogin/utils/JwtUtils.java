package com.framework.simpleLogin.utils;

import com.framework.simpleLogin.dto.UserDTO;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtils {
    private static final String SECRETKEY = Encryption.SHA256("Azuremy");
    private static final long EXPIRATION = 1000 * 60 * 60 * 24 * 7;

    private final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    public JwtUtils() {
    }

    private Key getSecretKey() {
        byte[] keyBytes = SECRETKEY.getBytes();

        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateTokenForUser(UserDTO dto) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", dto.getId());
        claims.put("username", dto.getUsername());
        claims.put("email", dto.getEmail());

        return generateToken(claims);
    }

    public String generateToken(Map<String, Object> claims) {
        Date currentDate = new Date();
        Date expirationDate = new Date(currentDate.getTime() + EXPIRATION);

        return Jwts.builder()
                .claims(claims)
                .issuedAt(currentDate)
                .expiration(expirationDate)
                .signWith(getSecretKey())
                .compact();

    }

    public Map<String, Object> getClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token) {
        try{
            Jwts.parser()
                    .verifyWith((SecretKey) getSecretKey())
                    .build()
                    .parse(token);

            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}
