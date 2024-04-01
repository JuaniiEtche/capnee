package com.gidas.capneebe.security.dtos.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponseDTO {
    private String accessToken;
}
