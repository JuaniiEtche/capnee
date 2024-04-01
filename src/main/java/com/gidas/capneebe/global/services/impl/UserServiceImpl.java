package com.gidas.capneebe.global.services.impl;

import com.gidas.capneebe.global.dtos.responses.StatusResponseDTO;
import com.gidas.capneebe.global.enums.Rol;
import com.gidas.capneebe.global.exceptions.customs.NotFoundException;
import com.gidas.capneebe.global.services.contracts.UserService;
import com.gidas.capneebe.security.dtos.request.UserRequestDTO;
import com.gidas.capneebe.security.dtos.response.UserResponseDTO;
import com.gidas.capneebe.security.models.entities.User;
import com.gidas.capneebe.security.models.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public List<UserResponseDTO> getUsers() {

        List<UserResponseDTO> Users = new ArrayList<>();
        for (com.gidas.capneebe.security.models.entities.User User : userRepository.findAll()) {
            Users.add(UserResponseDTO.builder()
                    .id(User.getId())
                    .user(User.getUser())
                    .firstName(User.getFirstName())
                    .lastName(User.getLastName())
                    .rol(String.valueOf(User.getRol()).toUpperCase())
                    .fileNumber(User.getFileNumber())
                    .build());
        }

        return Users;
    }
    public UserResponseDTO getUser(long id) {
        User User = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return UserResponseDTO.builder()
                .id(User.getId())
                .user(User.getUser())
                .firstName(User.getFirstName())
                .lastName(User.getLastName())
                .fileNumber(User.getFileNumber())
                .rol(String.valueOf(User.getRol()).toUpperCase())
                .build();
    }

    public StatusResponseDTO addUser(UserRequestDTO UserCompany) {

        if (UserCompany.getFirstName() == null || UserCompany.getFirstName().isEmpty() ||
                UserCompany.getLastName() == null || UserCompany.getLastName().isEmpty()) {
            throw new NotFoundException("First name and last name are required");
        }

        validateRole(UserCompany.getRol());

        User user = User.builder()
                .user(UserCompany.getUserName())
                .firstName(UserCompany.getFirstName())
                .lastName(UserCompany.getLastName())
                .rol(Rol.valueOf(UserCompany.getRol().toUpperCase()))
                .fileNumber(UserCompany.getFileNumber())
                .build();

        String password = BCrypt.hashpw(UserCompany.getPassword() ,BCrypt.gensalt());
        user.setPassword(password);

        userRepository.save(user);
        return StatusResponseDTO.builder()
                .status(201)
                .message("User added successfully")
                .build();
    }

    public StatusResponseDTO updateUser(long id, UserRequestDTO User) {
        User recruiter = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        validateRole(User.getRol());

        recruiter.setFirstName(User.getFirstName());
        recruiter.setLastName(User.getLastName());
        recruiter.setPassword(BCrypt.hashpw(User.getPassword(), BCrypt.gensalt()));
        recruiter.setRol(Rol.valueOf(User.getRol().toUpperCase()));
        recruiter.setFileNumber(User.getFileNumber());

        userRepository.save(recruiter);
        return StatusResponseDTO.builder()
                .status(200)
                .message("User updated successfully")
                .build();
    }

    public StatusResponseDTO removeUser(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        userRepository.deleteById(id);

        return StatusResponseDTO.builder()
                .status(200)
                .message("User removed successfully")
                .build();
    }

    private void validateRole(String role) {
        try {
            Rol.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Role not found");
        }
    }
}
