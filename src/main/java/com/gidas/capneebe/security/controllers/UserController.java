package com.gidas.capneebe.security.controllers;

import com.gidas.capneebe.global.config.swagger.responses.SwaggerResponseCode;
import com.gidas.capneebe.global.dtos.responses.StatusResponseDTO;
import com.gidas.capneebe.global.services.contracts.UserService;
import com.gidas.capneebe.security.dtos.request.UserRequestDTO;
import com.gidas.capneebe.security.dtos.response.UserResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@SecurityRequirement(name = "Bearer Authentication")
@Slf4j
@RequiredArgsConstructor
@Tag(
        name = "User",
        description = "Basic Crud Operations of User"
)
public class UserController extends SwaggerResponseCode {

    private final UserService userService;

    @PostMapping("/new")
    @Operation(summary = "Add User", description = "Add a new User entity")
    public ResponseEntity<StatusResponseDTO> addUser(@RequestBody UserRequestDTO UserDTO) {
        return ResponseEntity.ok( userService.addUser(UserDTO));
    }
    @PutMapping("/update-by-id")
    @Operation(summary = "Update User", description = "Update an existing User entity by its ID")
    public ResponseEntity<StatusResponseDTO> updateUser(@RequestParam long id,@RequestBody UserRequestDTO UserDTO) {
        return ResponseEntity.ok(userService.updateUser(id,UserDTO));
    }
    @GetMapping("/get-by-id")
    @Operation(summary = "Get User", description = "Retrieve a user by its ID")
    public ResponseEntity<UserResponseDTO> getUser(@RequestParam long id) {
        return ResponseEntity.ok(userService.getUsuario(id));
    }
    @GetMapping("/get-all")
    @Operation(summary = "Get Users", description = "Retrieve all users")
    public ResponseEntity<List<UserResponseDTO>> getUsers() {
        return ResponseEntity.ok(userService.getUsuario());
    }

    @DeleteMapping("/delete-by-id")
    @Operation(summary = "Remove User", description = "Remove a user by its ID")
    public ResponseEntity<StatusResponseDTO> removeUser(@RequestParam long id) {
        return ResponseEntity.ok(userService.removeUser(id));
    }
}
