package com.ECommerce.controller.auth;

import com.ECommerce.dto.request.auth.LoginRequest;
import com.ECommerce.dto.request.auth.SignupRequest;
import com.ECommerce.dto.request.auth.VerifyOtpCodeRequest;
import com.ECommerce.dto.response.ApiResponse;
import com.ECommerce.dto.response.auth.AuthResponse;
import com.ECommerce.exception.ApplicationException;
import com.ECommerce.service.auth.AuthService;
import com.ECommerce.validation.ValidEmail;
import com.ECommerce.validation.ValidUsername;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/username-availability")
    public ResponseEntity<ApiResponse<String>> checkUserNameAvailability(
            @ValidUsername @RequestParam String username
    ){
        if(authService.doesUserNameExist(username)){
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error("Username is taken.", "USERNAME_EXISTS"));
        }
        return ResponseEntity.ok(ApiResponse.ok("Username is available."));
    }

    @GetMapping("/send-otp-code")
    public ResponseEntity<ApiResponse<String>> sendOtpCode(
            @ValidEmail @RequestParam String email
    ){
        if(authService.doesEmailExist(email)){
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error("Email already exists", "EMAIL_EXISTS"));
        }
        if(!authService.sendOtpCode(email)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).
                    body(ApiResponse.error("Failed to send Code", "FAILED_TO_SEND_CODE"));
        }
        return ResponseEntity.ok(ApiResponse.ok("Code sent successfully"));
    }

    @PostMapping("/verify-otp-code")
    public ResponseEntity<ApiResponse<String>> verifyOtpCode(
            @Valid @RequestBody VerifyOtpCodeRequest request
    ){
        if(authService.verifyOtpCode(request.email(), request.code())){
            return ResponseEntity.ok(ApiResponse.ok("OTP verified."));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Invalid OTP code.", "INVALID_CODE"));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody SignupRequest request, HttpServletResponse httpResponse
    ) throws ApplicationException {
        AuthResponse authResponse = authService.register(request, httpResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(authResponse, "Account created successfully"));
    }


    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request, HttpServletResponse httpResponse
    )throws ApplicationException {
        AuthResponse authResponse = authService.login(request, httpResponse);
        return ResponseEntity.ok(ApiResponse.ok(authResponse, "Logged in successfully!"));
    }


    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            HttpServletRequest request, HttpServletResponse httpServletResponse
    )throws ApplicationException {
        AuthResponse authResponse = authService.refreshToken(request, httpServletResponse);
        return ResponseEntity.ok(
                ApiResponse.ok(authResponse, "Token refreshed successfully!")
        );
    }


    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout(
            HttpServletResponse httpServletResponse, Authentication authentication
    ) {
        authService.logout(authentication, httpServletResponse);
        return ResponseEntity.ok(
                ApiResponse.ok("Logged out successfully"));
    }

}


