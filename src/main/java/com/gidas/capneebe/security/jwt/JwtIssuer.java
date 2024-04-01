package com.gidas.capneebe.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.gidas.capneebe.security.principal.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtIssuer {

    private final JwtProperties jwtProperties;

    static final String JWT_CLAIM_SUB = "sub";
    static final String JWT_CLAIM_USER = "user";
    static final String JWT_CLAIM_AUTHORITIES = "authorities";
    static final String JWT_CLAIM_NAME = "name";
    static final String JWT_CLAIM_COMPANY = "company";
    static final String JWT_CLAIM_ISSUED_AT = "iat";
    static final String JWT_CLAIM_EXPIRATION_TIME = "exp";
    static final String JWT_CLAIM_PICTURE= "picture";

    public String issue(String subject, String name, String user, List<String> roles) {
        return JWT.create()
                .withClaim(JWT_CLAIM_SUB, subject)
                .withClaim(JWT_CLAIM_AUTHORITIES, roles)
                .withClaim(JWT_CLAIM_USER, user)
                .withClaim(JWT_CLAIM_NAME, name)
                .withClaim(JWT_CLAIM_ISSUED_AT, Instant.now())
                .withClaim(JWT_CLAIM_EXPIRATION_TIME, Instant.now().plus(Duration.of(jwtProperties.getExpireAfterHours(), ChronoUnit.HOURS)))
                .sign(Algorithm.HMAC256(jwtProperties.getSecretKey()));
    }

    public String generateJWT(UserPrincipal principal) {
        return this.issue(
                principal.getUserId(),
                principal.getFullName(),
                principal.getUser(),
                principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList()
        );
    }


}
