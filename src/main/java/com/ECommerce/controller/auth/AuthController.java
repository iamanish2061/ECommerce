package com.ECommerce.controller.auth;

import com.ECommerce.dto.request.auth.LoginRequest;
import com.ECommerce.dto.request.auth.RefreshTokenRequest;
import com.ECommerce.dto.request.auth.SignupRequest;
import com.ECommerce.dto.request.auth.VerifyOtpCodeRequest;
import com.ECommerce.dto.response.ApiResponse;
import com.ECommerce.dto.response.auth.LoginResponse;
import com.ECommerce.dto.response.auth.RefreshTokenResponse;
import com.ECommerce.dto.response.auth.SignupResponse;
import com.ECommerce.exception.ApplicationException;
import com.ECommerce.model.Users;
import com.ECommerce.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/check-username-availability")
    public ResponseEntity<ApiResponse> checkUserNameAvailability(
            @RequestParam //validation
            String username
    ){
        if(authService.doesUserNameExist(username)){
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error("Username is taken.", "USERNAME_EXISTS"));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("Username is available."));
    }

    @GetMapping("/check-email-and-send-otpCode")
    public ResponseEntity<ApiResponse> sendOtpCode(
            @RequestBody //validation
            String email
    ){
        if(authService.doesEmailExist(email)){
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error("Email already exists", "EMAIL_EXISTS"));
        }
        if(!authService.sendOtpCode(email)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).
                    body(ApiResponse.error("Failed to send Code", "FAILED_TO_SEND_CODE"));
        }
        return ResponseEntity.status(HttpStatus.OK).
                body(ApiResponse.ok("Code sent successfully"));
    }

    @GetMapping("/verify-otpCode")
    public ResponseEntity<ApiResponse> verifyOtpCode(@RequestBody VerifyOtpCodeRequest request){
        if(authService.verifyOtpCode(request.email(), request.code())){
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.ok("OTP verified."));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Invalid OTP code.", "INVALID_CODE"));
    }




    @PostMapping("/register")
    public ResponseEntity<SignupResponse> register(@RequestBody SignupRequest request) throws ApplicationException {
        return ResponseEntity.ok(authService.register(request));
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refresh(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refresh(request));
    }

}


