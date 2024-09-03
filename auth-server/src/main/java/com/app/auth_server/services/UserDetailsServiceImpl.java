package com.app.auth_server.services;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.app.auth_server.configs.security.JwtService;
import com.app.auth_server.dtos.LoginRequestDto;
import com.app.auth_server.dtos.RegisterRequestDto;
import com.app.auth_server.dtos.UserAuthResponseDto;
import com.app.auth_server.models.UserModel;
import com.app.auth_server.repositories.UserRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository, @Lazy AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserModel userModel = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return org.springframework.security.core.userdetails.User
                .withUsername(userModel.getUsername())
                .password(userModel.getPassword())
                .roles("USER")
                .build();
    }

    public UserModel saveUser(RegisterRequestDto registerRequestDto){
        UserModel userModel = new UserModel();
		BeanUtils.copyProperties(registerRequestDto, userModel);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		userModel.setPassword(passwordEncoder.encode(userModel.getPassword()));
        ZonedDateTime nowInBrazil = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"));
        LocalDateTime now = nowInBrazil.toLocalDateTime();
        userModel.setCreated_at(now);
        userModel.setUpdated_at(now);
        return userRepository.save(userModel);
    };

    public UserAuthResponseDto login(LoginRequestDto loginRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserModel userModel = findByEmail(loginRequestDto.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));

        String jwtToken = jwtService.generateToken(userModel);
        return UserAuthResponseDto.builder()
            .token(jwtToken)
            .id(userModel.getUser_id())
            .build();
    };

    public void logout() {
        SecurityContextHolder.clearContext();
    }


    public Optional<UserModel> findByEmail(String email){
        return userRepository.findByEmail(email);
    };


}

