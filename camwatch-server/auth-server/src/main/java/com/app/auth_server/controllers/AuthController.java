package com.app.auth_server.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import com.app.auth_server.dtos.LoginRequestDto;
import com.app.auth_server.dtos.RegisterRequestDto;
import com.app.auth_server.models.UserModel;
import com.app.auth_server.services.UserDetailsServiceImpl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/cam-watch-auth")
public class AuthController {

    private final UserDetailsServiceImpl userDetailsServiceImpl;

    public AuthController(UserDetailsServiceImpl userDetailsServiceImpl) {
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequestDto loginRequestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(userDetailsServiceImpl.login(loginRequestDto));
    }

    @Transactional
    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody RegisterRequestDto registerRequestDto) {
        if (userDetailsServiceImpl.findByEmail(registerRequestDto.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
        }
        UserModel saved = userDetailsServiceImpl.saveUser(registerRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved.getUser_id().toString());
    }

    @PostMapping("/logout")
    public ResponseEntity<Object> logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
            userDetailsServiceImpl.logout();
        }
        return ResponseEntity.status(HttpStatus.OK).body("Logged out successfully");
    }
}
