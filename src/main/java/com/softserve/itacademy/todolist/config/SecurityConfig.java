package com.softserve.itacademy.todolist.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity()
@RequiredArgsConstructor
public class SecurityConfig {
    // todo: check https://spring.io/blog/2022/02/21/spring-security-without-the-websecurityconfigureradapter


    @Bean
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(eh -> eh
                        .authenticationEntryPoint(restAuthenticationEntryPoint()))
                .httpBasic(hb -> hb
                        .authenticationEntryPoint(restAuthenticationEntryPoint()) // Handles auth error
                )
                .sessionManagement(sm -> sm
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // no session
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/auth/**", "/home", "/", "/static/**", "/error").permitAll()
                        .requestMatchers("/api/v1/users").hasRole("ADMIN")
                        .anyRequest().authenticated()
                );

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    AuthenticationEntryPoint restAuthenticationEntryPoint() {
        return (request, response, authException) -> {
            log.warn("Authentication for '{} {}' failed with error: {}",
                    request.getMethod(), request.getRequestURL(), authException.getMessage());
            response.sendError(
                    UNAUTHORIZED.value(), authException.getMessage());
        };
    }
}
