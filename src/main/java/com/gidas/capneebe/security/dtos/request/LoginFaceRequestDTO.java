package com.gidas.capneebe.security.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Schema(name = "Login Request Face")
@AllArgsConstructor
@NoArgsConstructor
public class LoginFaceRequestDTO {
    private String faceImage;
}
