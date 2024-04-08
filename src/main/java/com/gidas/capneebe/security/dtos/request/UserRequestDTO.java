package com.gidas.capneebe.security.dtos.request;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class UserRequestDTO {

    private String firstName;

    private String lastName;

    private String userName;

    private String rol;

    private String password;

    private long fileNumber;

    private List<String> images;
}
