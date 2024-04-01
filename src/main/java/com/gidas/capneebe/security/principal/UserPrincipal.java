package com.gidas.capneebe.security.principal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * This class has the User Credentials and is used, among others, to keep the necessary info to generate a JWT
 * Attributes: userId, password, isLocked, authorities.
 */
@Getter
@Builder
public class UserPrincipal implements UserDetails {

    private final String userId;

    private final String user;

    private final String fullName;

    @JsonIgnore //In case UserPrincipal in printed, password is secured
    private final String password;

    private final boolean locked;

    private final Collection<? extends GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return user;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return !locked;
    }
}
