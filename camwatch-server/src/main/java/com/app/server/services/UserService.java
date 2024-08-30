package com.app.server.services;

import java.util.Optional;
import java.util.UUID;

import com.app.server.models.UserModel;

public interface UserService {
    public Optional<UserModel> findUserById(UUID id);
}
