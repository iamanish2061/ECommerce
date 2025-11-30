package com.ECommerce.controller.auth;

import com.ECommerce.dto.request.auth.UpdatePasswordRequest;
import com.ECommerce.dto.response.ApiResponse;
import com.ECommerce.dto.response.auth.AuthResponse;
import com.ECommerce.exception.ApplicationException;
import com.ECommerce.service.auth.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class ForgotPasswordController {

    private final AuthService authService;

    @GetMapping("check-username-exists")
    public ResponseEntity<ApiResponse<?>> checkUsernameExists(
        @RequestParam
        @NotBlank(message = "Username is required!")
        @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters!")
        @Pattern(regexp = "^[A-Za-z0-9_]+$", message = "Username can only contain letters, numbers, and underscores!")
        String username
    ){
        if(!authService.doesUserNameExist(username)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Username not found!", "USERNAME_NOT_FOUND"));
        }
        return ResponseEntity.ok(ApiResponse.ok("Valid Username and Email."));
    }

    @GetMapping("send-otp-code-for-forgot-password")
    public ResponseEntity<ApiResponse<?>> sendCodeForForgotPassword(
            @NotBlank(message = "Email is required!")
            @Email(message = "Please provide a valid email address!")
            @Size(max = 100, message = "Email is too long!")
            String email
    ){
        if(!authService.doesEmailExist(email)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Email does not exist!", "EMAIL_NOT_FOUND"));
        }
        if(!authService.sendOtpCode(email)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).
                    body(ApiResponse.error("Failed to send Code", "FAILED_TO_SEND_CODE"));
        }
        return ResponseEntity.ok(ApiResponse.ok("Code sent successfully"));
    }

    //do you want to reset password noW? yes ... no
    //for no
    @GetMapping("/continue-without-login")
    public ResponseEntity<ApiResponse<AuthResponse>> setTokenForUserContinuingWithoutResettingPassword(
        @RequestParam
        @NotBlank(message = "Username is required!")
        @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters!")
        @Pattern(regexp = "^[A-Za-z0-9_]+$", message = "Username can only contain letters, numbers, and underscores!")
        String username,
        @NotBlank(message = "Email is required!")
        @Email(message = "Please provide a valid email address!")
        @Size(max = 100, message = "Email is too long!")
        String email,
        HttpServletResponse httpServletResponse
    ) throws ApplicationException {
        AuthResponse authResponse = authService.setTokenForUserContinuingWithoutResettingPassword(username, email, httpServletResponse);
        return ResponseEntity.ok(ApiResponse.ok(authResponse, "Token set successfully"));
    }

    @PostMapping("update-password")
    public ResponseEntity<ApiResponse<?>> updatePassword(
            @Valid @RequestBody UpdatePasswordRequest request, HttpServletResponse httpServletResponse
    ) throws ApplicationException {
        AuthResponse apiResponse = authService.updatePassword(request, httpServletResponse);
        return ResponseEntity.ok(ApiResponse.ok(apiResponse, "Password Updated successfully."));
    }



}
