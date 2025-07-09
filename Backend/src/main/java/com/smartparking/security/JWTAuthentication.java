package com.smartparking.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
// This class handles JWT authentication logic, such as generating security tokens with claims and expiration times.
public class JWTAuthentication{
    private final String secretKey = "smartparkingsecretkey9876543210_";
    private final long expirationTime = 1000 * 60 * 60 * 3; //3 hours
    private Key getKey(){
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateSecurityToken(String username, List<String> roles) {
        //lsecurityConfigurationsogic to generate JWT token using the key and username
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getKey(), SignatureAlgorithm.HS256)//assures the token is signed with integrity and authenticity
                .compact();
    }

    public String getUsernameFromToken(String token) {
        //logic to extract username from the JWT token
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public List<String> getRolesFromToken(String token) {
        //logic to extract roles from the JWT token
        List<String> roles = Jwts.parserBuilder()
                            .setSigningKey(getKey())
                            .build()
                            .parseClaimsJws(token)
                            .getBody()
                            .get("roles", List.class);
        return roles != null ? roles : List.of(); //return an empty list if roles are not present
    }

    public boolean isTokenExpired(String token) {
        //logic to check if the JWT token is expired or still valid considering the expired time
        Date expirationDate = Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expirationDate.before(new Date());
    }

    public boolean validateToken(String token, String username) {
        //logic to validate the JWT token
        try {
            var claims = Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
            // Check if the username in the token matches the provided username and if the token is not expired
            return claims.getSubject().equals(username) && !isTokenExpired(token);
        } catch(ExpiredJwtException e) {
            System.out.println("JWT token expired: " + e.getMessage());
            return false;
        }catch (Exception e) {
            System.out.println("Invalid JWT token: " + e.getMessage());
            return false;
        }
    }
}//jwt authentication class
