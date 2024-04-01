package com.gidas.capneebe.security.dtos.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {

    private long id;

    private String user;

    private String firstName;

    private String lastName;

    private String rol;

    private long fileNumber;
}
