package com.gidas.capneebe.security.dtos;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AppUser {
    private String username;
    private String password;
    private List<String> roles;
}
