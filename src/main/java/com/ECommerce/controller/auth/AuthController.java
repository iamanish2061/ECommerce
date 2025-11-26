package com.ECommerce.controller.auth;

import com.ECommerce.model.AuthResponse;
import com.ECommerce.model.Users;
import com.ECommerce.service.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Users> register(@RequestBody Users user) {
        //validation
        return ResponseEntity.ok(authService.register(user));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Users user) {
        //validation
        return ResponseEntity.ok(authService.login(user));
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        return ResponseEntity.ok(authService.refresh(refreshToken));
    }

    @PostMapping("/check-username")
    public ResponseEntity<?> checkUserName(@RequestBody String username){
        //validation
        if(authService.doesUserNameExist(username)){
            return ResponseEntity.ok(new AuthResponse(false, "Username already exist"));
        }
        return ResponseEntity.ok(new AuthResponse(true, "Username is valid"));
    }

    @PostMapping("/check-email-and-send-code")
    public ResponseEntity<?> sendCode(@RequestBody String email){
        //validation
        if(authService.doesEmailExist(email)){
            return ResponseEntity.ok(new AuthResponse(false,"Email already exist!"));
        }

        if(authService.sendCode(email)){
            return ResponseEntity.ok(new AuthResponse(true, "Code sent successfully"));
        }
        return ResponseEntity.ok(new AuthResponse(false,"Error while sending code!"));
    }

    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody String email, String code){
        //validation
        if(authService.verifyCode(email, code)){
            return ResponseEntity.ok(new AuthResponse(true, "Code is verified!"));
        }
        return ResponseEntity.ok(new AuthResponse(false, "Invalid Code!"));
    }


}


