package com.gidas.capneebe.security.components;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.gidas.capneebe.security.dtos.MainUser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Component
@Slf4j
public class JwtProvider {
    @Value("${security.jwt.secret-key}")
    private String secret;

    Integer expiration = 3600000;

    public String generateToken(Authentication authentication){
        MainUser mainUser = (MainUser) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(mainUser.getUsername())
                .claim("roles", this.getRolesFromToken(mainUser))
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + expiration))
                .signWith(this.getKey(secret))
                .compact();
    }

    public String getUsernameFromToken(String token){
        return JWT.require(Algorithm.HMAC256(secret))
                .build()
                .verify(token)
                .getClaim("email").asString();
    }

    public boolean validateToken(String token){
        try {
            JWT.require(Algorithm.HMAC256(secret)).build().verify(token);
            return true;
        } catch (Exception e){
            log.error("Error validating token: {}", e.getMessage());
        }
        return false;
    }

    private List<String> getRolesFromToken(MainUser mainUser){
        return mainUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    private Key getKey(String secret){
        byte[] secretBytes = Decoders.BASE64URL.decode(secret);
        return Keys.hmacShaKeyFor(secretBytes);
    }
}
