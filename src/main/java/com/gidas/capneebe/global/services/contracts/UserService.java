package com.gidas.capneebe.global.services.contracts;

import com.gidas.capneebe.global.dtos.responses.StatusResponseDTO;
import com.gidas.capneebe.security.dtos.request.UserRequestDTO;
import com.gidas.capneebe.security.dtos.response.UserResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    List<UserResponseDTO> getUsuario();

    UserResponseDTO getUsuario(long id);

    StatusResponseDTO addUser(UserRequestDTO UserCompany);

    StatusResponseDTO updateUser(long id, UserRequestDTO User);

    StatusResponseDTO removeUser(long id);

}
