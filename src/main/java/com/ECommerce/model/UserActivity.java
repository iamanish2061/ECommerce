package com.ECommerce.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_activity",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "product_id", "action_type"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false)
    private ActionType actionType;

    @Column(nullable = false)
    private int score;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum ActionType {
        VIEW,       // score = 1
        CART_ADD,   // score = 5
        PURCHASE    // score = 20
    }
}