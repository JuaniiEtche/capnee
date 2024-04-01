package com.gidas.capneebe.security;

import com.gidas.capneebe.security.components.JwtEntryPoint;
import com.gidas.capneebe.security.components.JwtTokenFilter;
import com.gidas.capneebe.security.service.impl.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserDetailsServiceImpl userDetailService;
    private final JwtEntryPoint jwtEntryPoint;
    private final JwtTokenFilter jwtTokenFilter;
    private final PasswordEncoder passwordEncoder;

    AuthenticationManager authenticationManager;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(userDetailService).passwordEncoder(passwordEncoder);
        authenticationManager = builder.build();
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(new AntPathRequestMatcher("/swagger-ui/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/.well-known/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/v3/api-docs/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/actuator/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/auth/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/**")).permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationManager(authenticationManager)
                .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtEntryPoint))
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .cors(cors -> cors.configurationSource(request -> {
                            CorsConfiguration config = new CorsConfiguration();
                            config.setAllowedOrigins(Arrays.asList("*"));
                            config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                            config.setAllowedHeaders(Arrays.asList("*"));
                            return config;
                        })
                );

        return http.build();
    }

}
