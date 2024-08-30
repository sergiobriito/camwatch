package com.app.server.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.server.models.UserModel;

@Repository
public interface UserRepository extends JpaRepository<UserModel,UUID>{
    @Query(value = "SELECT user_id, username, email FROM USERS WHERE user_id = :id",
            nativeQuery = true)
    Optional<UserModel> findUserById(@Param("id") UUID id);
}
