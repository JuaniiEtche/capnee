package com.gidas.capneebe.security.service.impl;

import com.gidas.capneebe.global.exceptions.customs.NotFoundException;
import com.gidas.capneebe.security.models.entities.User;
import com.gidas.capneebe.security.models.repositories.UserRepository;
import com.gidas.capneebe.security.principal.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements org.springframework.security.core.userdetails.UserDetailsService{

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUser(username).orElseThrow(()-> new NotFoundException(username));
        return UserPrincipal.builder()
                .userId(String.valueOf(user.getId()))
                .user(user.getUser())
                .fullName(user.getFullName())
                .authorities(List.of(new SimpleGrantedAuthority(
                        user.getRol() != null ? user.getRol().toString() : null)))
                .password(user.getPassword())
                .build();
    }

}
