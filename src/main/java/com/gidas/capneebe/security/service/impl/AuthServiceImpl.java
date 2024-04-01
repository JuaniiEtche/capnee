package com.gidas.capneebe.security.service.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.gidas.capneebe.global.exceptions.customs.BadCredentialsException;
import com.gidas.capneebe.security.dtos.response.LoginResponseDTO;
import com.gidas.capneebe.security.jwt.JwtIssuer;
import com.gidas.capneebe.security.principal.UserPrincipal;
import com.gidas.capneebe.security.principal.UserPrincipalAuthenticationToken;
import com.gidas.capneebe.security.service.contracts.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtIssuer jwtIssuer;
    private final AuthenticationManager authenticationManager;

    public LoginResponseDTO attemptUserPasswordLogin(String user, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user, password)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtIssuer.generateJWT((UserPrincipal) authentication.getPrincipal());
            return LoginResponseDTO.builder()
                    .accessToken(token)
                    .build();

        }  catch (Exception ex) {
            throw new BadCredentialsException("Invalid credentials");
        }
    }

}
