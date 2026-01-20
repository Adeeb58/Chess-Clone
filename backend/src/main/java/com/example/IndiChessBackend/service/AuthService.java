package com.example.IndiChessBackend.service;

import com.example.IndiChessBackend.exception.UserAlreadyExistsException;
import com.example.IndiChessBackend.model.User;
import com.example.IndiChessBackend.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final org.springframework.security.authentication.AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public User save(User user) {
        // Check for duplicate username
        if (userRepo.findByUsername(user.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("username", user.getUsername());
        }

        // Check for duplicate email
        if (userRepo.findByEmailId(user.getEmailId()).isPresent()) {
            throw new UserAlreadyExistsException("email", user.getEmailId());
        }

        // Set provider to LOCAL for traditional signup
        user.setProvider("LOCAL");

        // Encode password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepo.save(user);
    }

    public String verify(com.example.IndiChessBackend.model.DTO.LoginDto user) {
        System.out.println("🔍 Login attempt for username: " + user.getUsername());

        // Check if user exists in database
        java.util.Optional<User> dbUser = userRepo.findByUsername(user.getUsername());
        if (dbUser.isEmpty()) {
            System.out.println("❌ User not found in database: " + user.getUsername());
            throw new BadCredentialsException("Invalid username or password");
        }
        System.out.println("✅ User found in database: " + user.getUsername());

        try {
            org.springframework.security.core.Authentication authentication = authManager.authenticate(
                    new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                            user.getUsername(),
                            user.getPassword()));

            if (authentication.isAuthenticated()) {
                System.out.println("✅ Authentication successful for: " + user.getUsername());
                return jwtService.generateToken(user.getUsername());
            }
        } catch (org.springframework.security.core.AuthenticationException e) {
            System.out.println("❌ Authentication failed: " + e.getMessage());
            throw new BadCredentialsException("Invalid username or password");
        }

        throw new BadCredentialsException("Invalid username or password");
    }
}
