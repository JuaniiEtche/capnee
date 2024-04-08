package com.gidas.capneebe.security.service.impl;

import com.gidas.capneebe.global.exceptions.customs.NotFoundException;
import com.gidas.capneebe.security.models.entities.Usuario;
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
        Usuario usuario = userRepository.findByUsuario(username).orElseThrow(()-> new NotFoundException(username));
        return UserPrincipal.builder()
                .userId(String.valueOf(usuario.getId()))
                .user(usuario.getUsuario())
                .fullName(usuario.getFullName())
                .authorities(List.of(new SimpleGrantedAuthority(
                        usuario.getRol() != null ? usuario.getRol().toString() : null)))
                .password(usuario.getContraseña())
                .build();
    }

}
