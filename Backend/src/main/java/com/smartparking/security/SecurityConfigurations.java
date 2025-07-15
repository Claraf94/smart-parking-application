package com.smartparking.security;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static org.springframework.security.config.Customizer.withDefaults;

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
                        .requestMatchers("/users/register", "/users/login", "/error", "/resetPassword/**", "/users/resetTokenPassword", "/v3/api-docs/**","/swagger-ui.html", "/swagger-ui/**", "/actuator/**")
                        .permitAll()
                        .requestMatchers("/v3/api-docs/*", "/swagger-ui/*", "/swagger-ui.html").permitAll()
                        .anyRequest().authenticated());

        http.cors(withDefaults());
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // returns a password encoder that uses the BCrypt hashing algorithm
    }
}// security configurations class