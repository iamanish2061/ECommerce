package com.ECommerce.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity

@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.ROLE_USER;

    private String profileUrl;
    private UserStatus status = UserStatus.ACTIVE;

    private String refreshToken;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // for sending notifications only to logged-in users
    private boolean online = false;

    public enum Role {
        ROLE_USER,
        ROLE_ADMIN,
        ROLE_STAFF,
        ROLE_DRIVER
    }

    public enum UserStatus{
        ACTIVE,
        SUSPENDED
    }

}



