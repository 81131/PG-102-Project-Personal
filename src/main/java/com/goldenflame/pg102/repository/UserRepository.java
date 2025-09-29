package com.goldenflame.pg102.repository;

import com.goldenflame.pg102.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    long countByRole_Name(String roleName);
}

