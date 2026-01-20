package com.example.IndiChessBackend.repo;

import com.example.IndiChessBackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> getUserByUsername(String username);

    Optional<User> findByEmailId(String emailId);
}