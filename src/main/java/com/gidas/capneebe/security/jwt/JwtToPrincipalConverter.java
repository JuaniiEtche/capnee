package com.gidas.capneebe.security.jwt;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.gidas.capneebe.security.principal.UserPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JwtToPrincipalConverter {

    public UserPrincipal convert(DecodedJWT jwt) {
        return UserPrincipal.builder()
                .userId(jwt.getSubject())
                .user(jwt.getClaim(JwtIssuer.JWT_CLAIM_USER).asString())
                .authorities(extractAuthoritiesFromClaim(jwt))
                .fullName(jwt.getClaim(JwtIssuer.JWT_CLAIM_NAME).asString())
                .build();
    }

    private List<SimpleGrantedAuthority> extractAuthoritiesFromClaim(DecodedJWT jwt) {
        Claim claim = jwt.getClaim(JwtIssuer.JWT_CLAIM_AUTHORITIES);
        if(claim.isNull() || claim.isMissing()) return List.of();
        return claim.asList(SimpleGrantedAuthority.class);
    }

}
