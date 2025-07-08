package com.smartparking.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable() // Desativa proteção CSRF (necessário para APIs REST)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/users/register", "/actuator/**").permitAll() // Libera o endpoint de registro
                .anyRequest().authenticated() // Demais endpoints exigem autenticação
            );

        return http.build();
    }
}