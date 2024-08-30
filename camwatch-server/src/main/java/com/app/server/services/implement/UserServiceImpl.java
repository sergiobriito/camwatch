package com.app.server.services.implement;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.app.server.models.UserModel;
import com.app.server.repositories.UserRepository;
import com.app.server.services.UserService;

@Service
public class UserServiceImpl implements UserService {

    UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<UserModel> findUserById(UUID id) {
        return userRepository.findUserById(id);
    }
}
