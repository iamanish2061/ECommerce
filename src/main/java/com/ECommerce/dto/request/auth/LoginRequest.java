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

        @NotBlank(message = "Password is required!")
        @Size(min = 8, max = 50, message = "Password must be at least 8 characters!")
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!_*])(?=\\S+$).{8,}$",
                message = "Password must contain at least one letter, one number, and one special character (@#$%^&+=!*_)!"
        )
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
