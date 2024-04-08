package com.gidas.capneebe.security.controllers;

import com.gidas.capneebe.security.dtos.request.LoginFaceRequestDTO;
import com.gidas.capneebe.security.dtos.request.LoginRequestDTO;
import com.gidas.capneebe.security.dtos.response.LoginResponseDTO;
import com.gidas.capneebe.security.service.contracts.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.login.LoginException;

@RestController
@RequestMapping("/auth")
@SecurityRequirement(name = "Bearer Authentication")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login/user-password")
    @Operation(description = "Login with user and password.", summary = "Login with user and password")
    public ResponseEntity<LoginResponseDTO> loginUser(@RequestBody LoginRequestDTO userLoginRequestDTO) throws LoginException {
        return new ResponseEntity<>(authService.attemptUserPasswordLogin(userLoginRequestDTO.getUser(), userLoginRequestDTO.getPassword()), HttpStatus.OK);
    }

    @PostMapping("/login/face")
    @Operation(description = "Login with face", summary = "Login with face")
    public ResponseEntity<LoginResponseDTO> loginUserFace(@RequestBody LoginFaceRequestDTO userLoginRequestDTO) throws LoginException {
        return new ResponseEntity<>(authService.attemptFaceLogin(userLoginRequestDTO.getFaceImage()), HttpStatus.OK);
    }
}
