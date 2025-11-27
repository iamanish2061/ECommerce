package com.ECommerce.dto.request.auth;

import com.ECommerce.model.Role;

public record LoginRequest(

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
