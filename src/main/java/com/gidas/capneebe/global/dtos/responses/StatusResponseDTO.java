package com.gidas.capneebe.global.dtos.responses;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatusResponseDTO {
    private int status;
    private String message;
}
