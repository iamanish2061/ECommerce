package com.ECommerce.dto.request.auth;

import com.ECommerce.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginRequest(

        @NotBlank(message = "Username is required!")
        @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters!")
        @Pattern(regexp = "^[A-Za-z0-9_]+$", message = "Username can only contain letters, numbers, and underscores!")
        String username,


        String password,
        Role role
) {
    public Role assignRole(){
        if(role == null){
            return Role.ROLE_USER;
        }
        return role;
    }
}
