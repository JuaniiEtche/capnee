package com.gidas.capneebe.security.models.repositories;

import com.gidas.capneebe.global.enums.Rol;
import com.gidas.capneebe.security.models.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Page<User> findUsersByRolContainingAndFirstNameContainingAndLastNameContaining(Rol rol, String firstName, String lastName, Pageable pageable);

    Optional<User> findByUser(String user);

}

