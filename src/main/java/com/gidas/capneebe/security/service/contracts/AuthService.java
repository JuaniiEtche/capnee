package com.gidas.capneebe.security.service.contracts;

import com.gidas.capneebe.security.dtos.response.LoginResponseDTO;

import javax.security.auth.login.LoginException;

public interface AuthService {
    LoginResponseDTO attemptUserPasswordLogin(String user, String password) throws LoginException;
}
