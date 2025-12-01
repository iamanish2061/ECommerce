package com.ECommerce.controller.auth;

import com.ECommerce.dto.request.auth.UpdatePasswordRequest;
import com.ECommerce.dto.response.ApiResponse;
import com.ECommerce.dto.response.auth.AuthResponse;
import com.ECommerce.exception.ApplicationException;
import com.ECommerce.service.auth.AuthService;
import com.ECommerce.validation.ValidUsername;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/api/auth")
public class ForgotPasswordController {

    private final AuthService authService;

    @GetMapping("/username-exists")
    public ResponseEntity<ApiResponse<String>> checkUsernameExists (
        @ValidUsername @RequestParam String username
    )throws ApplicationException{
        if(!authService.doesUserNameExist(username)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Username not found!", "USER_NOT_FOUND"));
        }
        String encodedEmail = authService.findMaskedEmailByUsername(username);
        return ResponseEntity.ok(ApiResponse.ok(encodedEmail, "Username is valid!"));
    }

    @GetMapping("/send-otp-code-to-recover")
    public ResponseEntity<ApiResponse<String>> sendCodeForForgotPassword(
        @ValidUsername @RequestParam String username
    ) throws ApplicationException {
        String email = authService.findEmailByUsername(username);
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
        @ValidUsername @RequestParam String username,

        @NotBlank(message = "Verification code is required!")
        @Pattern(regexp = "^\\d{6}$", message = "Invalid OTP Code!")
        @RequestParam String code,
        HttpServletResponse httpServletResponse
    ) throws ApplicationException {
        AuthResponse authResponse = authService.setTokenForUserContinuingWithoutResettingPassword(username, code, httpServletResponse);
        return ResponseEntity.ok(ApiResponse.ok(authResponse, "Token set successfully"));
    }

    @PostMapping("/update-password")
    public ResponseEntity<ApiResponse<AuthResponse>> updatePassword(
            @Valid @RequestBody UpdatePasswordRequest request, HttpServletResponse httpServletResponse
    ) throws ApplicationException {
        AuthResponse apiResponse = authService.updatePassword(request, httpServletResponse);
        return ResponseEntity.ok(ApiResponse.ok(apiResponse, "Password Updated successfully."));
    }



}
