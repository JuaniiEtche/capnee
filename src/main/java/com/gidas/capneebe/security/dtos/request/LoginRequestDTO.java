package com.gidas.capneebe.security.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Schema(name = "Login Request User-Password")
public class LoginRequestDTO {

    private final String user;

    private final String password;
}
