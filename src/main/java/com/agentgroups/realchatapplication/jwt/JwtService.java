package com.agentgroups.realchatapplication.jwt;

import com.agentgroups.realchatapplication.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration-ms}")
    private String jwtExpirationMs;// 1 hour

    public String generateToken(User user) {
        return generateToken(new HashMap<>(), user);
    }
    public String generateToken(
            HashMap<String, Object> claims,
            User user
    ) {
        Map<String, Object> extraClaims = new HashMap<>(claims);
        extraClaims.put("userId", user.getId());

        return Jwts.builder()
                .claims(extraClaims)
                .subject(user.getUsername())
                .issuedAt(new java.util.Date(System.currentTimeMillis()))
                .setExpiration(new java.util.Date(System.currentTimeMillis() + Long.parseLong(jwtExpirationMs)))
                .signWith(getSignInKey())
                .compact();
    }

    public Long extractUserId(String jwtToken) {
        String userIdStr = extractClaim(jwtToken, claims -> claims.get("userId", String.class));
        return userIdStr != null ? Long.parseLong(userIdStr) : null;
    }

    private <T> T extractClaim(String jwtToken, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(jwtToken);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String jwtToken) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(jwtToken)
                .getPayload();
    }

    private SecretKey getSignInKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public boolean isTokenValid(String jwtToken, User userDetails) {
        final Long userIdFromToken = extractUserId(jwtToken);
        final Long userId=userDetails.getId();

        return (userIdFromToken != null && userIdFromToken.equals(userId) && !isTokenExpired(jwtToken));
    }

    private boolean isTokenExpired(String jwtToken) {
        return extractExpiration(jwtToken).before(new Date());
    }

    private Date extractExpiration(String jwtToken) {
        return extractClaim(jwtToken, Claims::getExpiration);
    }
}
