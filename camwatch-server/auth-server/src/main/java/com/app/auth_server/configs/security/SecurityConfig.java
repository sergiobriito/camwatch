package com.app.auth_server.configs.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    public SecurityConfig() {}

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.POST, "/api/v1/cam-watch-auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/cam-watch-auth/logout").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/cam-watch-auth/register").permitAll()
                .anyRequest().authenticated()
            );
        return http.build();
    }

}
