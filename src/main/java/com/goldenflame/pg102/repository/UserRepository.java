package com.goldenflame.pg102.repository;

import com.goldenflame.pg102.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Custom method to find a user by their username
    Optional<User> findByUsername(String username);
}