package com.ECommerce.model;

import lombok.*;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@Generated
public class AuthResponse {

    private boolean success;
    private String message;


}
