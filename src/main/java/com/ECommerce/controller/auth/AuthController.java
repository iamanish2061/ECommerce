package com.ECommerce.controller.auth;

import com.ECommerce.dto.request.auth.LoginRequest;
import com.ECommerce.dto.request.auth.SignupRequest;
import com.ECommerce.dto.request.auth.VerifyOtpCodeRequest;
import com.ECommerce.dto.response.ApiResponse;
import com.ECommerce.dto.response.auth.AuthResponse;
import com.ECommerce.exception.ApplicationException;
import com.ECommerce.service.auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/check-username-availability")
    public ResponseEntity<ApiResponse<?>> checkUserNameAvailability(
            @NotBlank(message = "Username is required!")
            @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters!")
            @Pattern(regexp = "^[A-Za-z0-9_]+$", message = "Username can only contain letters, numbers, and underscores!")
            @RequestParam
            String username
    ){
        if(authService.doesUserNameExist(username)){
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error("Username is taken.", "USERNAME_EXISTS"));
        }
        return ResponseEntity.ok(ApiResponse.ok("Username is available."));
    }

    @GetMapping("/check-email-and-send-otpCode")
    public ResponseEntity<ApiResponse<?>> sendOtpCode(
            @NotBlank(message = "Email is required!")
            @Email(message = "Please provide a valid email address!")
            @Size(max = 100, message = "Email is too long!")
            @RequestBody
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
        return ResponseEntity.ok(ApiResponse.ok("Code sent successfully"));
    }

    @GetMapping("/verify-otpCode")
    public ResponseEntity<ApiResponse<?>> verifyOtpCode(
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
            @RequestBody HttpServletRequest request, HttpServletResponse httpServletResponse
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


