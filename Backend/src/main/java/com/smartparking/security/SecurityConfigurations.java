package com.smartparking.security;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import static org.springframework.security.config.Customizer.withDefaults;

import java.util.List;

@Configuration
// this class configures the security settings for the application, disabling
// CSRF protection and defining authorization rules for HTTP requests
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfigurations {
    @Autowired
    private JWTAuthenticationFilter jwtAuthenticationFilter; // injects the JWT authentication filter to handle
                                                             // authentication logic

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // allows all OPTIONS requests
                        .requestMatchers("/users/register", "/users/login", "/error", "/resetPassword/**",
                                "/users/resetTokenPassword", "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**",
                                "/actuator/**")
                        .permitAll()
                        .anyRequest().authenticated())
                .cors(withDefaults());
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // returns a password encoder that uses the BCrypt hashing algorithm
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration confSource = new CorsConfiguration();
        confSource.setAllowedOrigins(List.of(
                "http://127.0.0.1:5501",
                "http://localhost:5500",
                "https://gray-smoke-0c9a20a03.2.azurestaticapps.net"));
        confSource.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        confSource.setAllowedHeaders(List.of("*"));
        confSource.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", confSource);
        return source;
    }
}// security configurations class