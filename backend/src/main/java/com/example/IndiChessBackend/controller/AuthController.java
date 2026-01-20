package com.example.IndiChessBackend.controller;

import com.example.IndiChessBackend.model.User;
import com.example.IndiChessBackend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@org.springframework.web.bind.annotation.CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AuthController {

    private final AuthService authservice;

    @PostMapping("/signup")
    public ResponseEntity<User> handleSignup(@Valid @RequestBody User user) {
        User savedUser = authservice.save(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody com.example.IndiChessBackend.model.DTO.LoginDto user,
            jakarta.servlet.http.HttpServletResponse response) {

        String token = authservice.verify(user);

        // Set HttpOnly Cookie
        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Set to false for localhost http dev
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 10); // 10 hours
        response.addCookie(cookie);

        return new ResponseEntity<>(token, HttpStatus.OK);
    }

}
