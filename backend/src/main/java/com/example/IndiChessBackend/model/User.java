package com.example.IndiChessBackend.model;

import jakarta.persistence.*;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long userId;

    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 50, message = "Username must be between 4 and 50 characters")
    @Column(name = "user_name", unique = true, nullable = false)
    String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Column(name = "email_id", unique = true, nullable = false)
    String emailId;

    @Size(min = 6, max = 512, message = "Password must be at least 6 characters")
    @Column(nullable = true) // Nullable for OAuth users
    String password;

    @Column(name = "provider")
    private String provider; // LOCAL, GOOGLE, GITHUB

    @Column(name = "provider_id")
    private String providerId; // OAuth provider's user ID

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
