package com.gidas.capneebe.security.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MainUser implements UserDetails {
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

   public static MainUser build(AppUser user){
       List<GrantedAuthority> authorities = user.getRoles()
               .stream()
               .map(rol -> {
                   return new SimpleGrantedAuthority(user.getRoles().get(0));
               }).collect(Collectors.toList());
       return new MainUser(user.getUsername(), user.getPassword(), authorities);
   }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
