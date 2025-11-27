package com.ECommerce.service.auth;

import com.ECommerce.dto.request.EmailSenderRequest;
import com.ECommerce.dto.request.auth.LoginRequest;
import com.ECommerce.dto.request.auth.RefreshTokenRequest;
import com.ECommerce.dto.request.auth.SignupRequest;
import com.ECommerce.dto.response.auth.LoginResponse;
import com.ECommerce.dto.response.auth.RefreshTokenResponse;
import com.ECommerce.dto.response.auth.SignupResponse;
import com.ECommerce.exception.ApplicationException;
import com.ECommerce.model.UserPrincipal;
import com.ECommerce.model.Users;
import com.ECommerce.redis.RedisService;
import com.ECommerce.repository.UserRepository;
import com.ECommerce.service.EmailService;
import com.ECommerce.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RedisService redisService;
    private final EmailService emailService;
    private final UserRepository userRepo;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private BCryptPasswordEncoder encoder= new BCryptPasswordEncoder(12);

    public boolean doesUserNameExist(String username) {
        return userRepo.existsByUsername(username);
    }

    public boolean doesEmailExist(String email) {
        return userRepo.existsByEmail(email);
    }

    public boolean sendOtpCode(String email) {
        Integer otpCode = new Random().nextInt(900000) +100000;
        redisService.setCode(email, otpCode.toString(), 600L);
        String subject = "Verifying Email";
        String emailBody = "Your otp code is: "+otpCode+". Please do not share with anyone! If you find this irrelevant, please ignore it!";
        return emailService.sendEmail(new EmailSenderRequest(email, subject, emailBody));
    }

    public boolean verifyOtpCode(String email, String code) {
        String generatedCode = redisService.getCode(email);
        return generatedCode != null && generatedCode.equals(code);
    }


    public SignupResponse register(SignupRequest request) throws ApplicationException {

        if(userRepo.findByUsername(request.username()).orElse(null) != null)
            throw new ApplicationException("Username is taken!", "USERNAME_EXISTS", HttpStatus.BAD_REQUEST);

        if(userRepo.findByEmail(request.email()).orElse(null) != null)
            throw new ApplicationException("Email already exists!", "EMAIL_EXISTS", HttpStatus.BAD_REQUEST);

        if(!verifyOtpCode(request.getEmail(), user.getCode()))
            throw new ApplicationException("Invalid OTP code!", "INVALID_OTP_CODE", HttpStatus.BAD_REQUEST);
        redisService.deleteCode(request.getEmail());
        user.setPassword(encoder.encode(request.getPassword()));



        //baki xa sab set garna
        return userRepo.save();
    }

    public LoginResponse login(LoginRequest request){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        if (!authentication.isAuthenticated()){
            Map<String,String> result =  new HashMap<>();
            result.put("status","failed");
            result.put("message", "Invalid Credentials!");
            return result;
        }

        Users dbUser = userRepo.findByUsername(user.getUsername()).orElse(null);

        // Generate tokens
        String accessToken = jwtService.generateAccessToken(new UserPrincipal(dbUser));
        String refreshToken = jwtService.generateRefreshToken(new UserPrincipal(dbUser));

        // Save refresh token in DB
        dbUser.setRefreshToken(refreshToken);
        userRepo.save(dbUser);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        return tokens;
    }

    public RefreshTokenResponse refresh(RefreshTokenRequest request) {
        String refreshToken = request.refreshToken();
        Users user = userRepo.findByRefreshToken(refreshToken).orElse(null);
        if (user == null || jwtService.validateToken(refreshToken, new UserPrincipal(user))) throw new RuntimeException("Invalid refresh token");

        String newAccessToken = jwtService.generateAccessToken(new UserPrincipal(user));
        String newRefreshToken = jwtService.generateRefreshToken(new UserPrincipal(user));

        user.setRefreshToken(newRefreshToken);
        userRepo.save(user);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", newAccessToken);
        tokens.put("refreshToken", newRefreshToken);
        return tokens;
    }



}
