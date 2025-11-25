package com.ECommerce.controller;

import com.ECommerce.model.Users;
import com.ECommerce.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth/")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Users> register(@RequestBody Users user) {
        return ResponseEntity.ok(authService.register(user));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Users user) {
        return ResponseEntity.ok(authService.login(user));
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        return ResponseEntity.ok(authService.refresh(refreshToken));
    }

    @PostMapping("/check-username")
    public ResponseEntity<?> checkUserName(@RequestBody String username){
        if(authService.doesUserNameExist(username)){
            return ResponseEntity.ok("Username already exist!");
        }
        return ResponseEntity.ok("success");
    }



}

