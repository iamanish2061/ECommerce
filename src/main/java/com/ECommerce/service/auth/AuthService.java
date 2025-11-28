package com.ECommerce.service.auth;

import com.ECommerce.dto.request.EmailSenderRequest;
import com.ECommerce.dto.request.auth.LoginRequest;
import com.ECommerce.dto.request.auth.RefreshTokenRequest;
import com.ECommerce.dto.request.auth.SignupRequest;
import com.ECommerce.dto.response.auth.AuthResponse;
import com.ECommerce.dto.response.auth.RefreshTokenResponse;
import com.ECommerce.dto.response.auth.SignupResponse;
import com.ECommerce.exception.ApplicationException;
import com.ECommerce.model.UserPrincipal;
import com.ECommerce.model.Users;
import com.ECommerce.redis.RedisService;
import com.ECommerce.repository.UserRepository;
import com.ECommerce.service.EmailService;
import com.ECommerce.service.JwtService;
import com.ECommerce.utils.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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


    @Transactional
    public AuthResponse register(SignupRequest request, HttpServletResponse response) throws ApplicationException {

        if(userRepo.findByUsername(request.username()).orElse(null) != null)
            throw new ApplicationException("Username is taken!", "USERNAME_EXISTS", HttpStatus.BAD_REQUEST);

        if(userRepo.findByEmail(request.email()).orElse(null) != null)
            throw new ApplicationException("Email already exists!", "EMAIL_EXISTS", HttpStatus.BAD_REQUEST);

        if(!verifyOtpCode(request.email(), request.code()))
            throw new ApplicationException("Invalid OTP code!", "INVALID_OTP_CODE", HttpStatus.BAD_REQUEST);

        if(!request.doesPasswordMatch())
            throw new ApplicationException("Password do not match!", "PASSWORD_MISMATCH", HttpStatus.BAD_REQUEST);


        redisService.deleteCode(request.email());

        Users user = Users.builder()
                .fullName(request.fullname())
                .username(request.username())
                .password(encoder.encode(request.password()))
                .email(request.email())
                .createdAt(LocalDateTime.now())
                .build();

        Users savedUser = userRepo.save(user);

        String accessToken = jwtService.generateAccessToken(new UserPrincipal(savedUser));
        String refreshToken = jwtService.generateRefreshToken(new UserPrincipal(savedUser));

        savedUser.setRefreshToken(encoder.encode(refreshToken));
        userRepo.save(savedUser);

        //add refresh token to header
        CookieUtils.setRefreshTokenCookie(response, refreshToken);

        return new AuthResponse(accessToken, savedUser.getId(), savedUser.getFullName(), savedUser.getUsername(), savedUser.getEmail());

    }


    public AuthResponse login(LoginRequest request, HttpServletResponse httpServletResponse){
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

    public AuthResponse refreshToken(HttpServletRequest request) throws ApplicationException{
        String refreshToken = CookieUtils.getRefreshTokenFromCookie(request)
                .orElseThrow(() -> new ApplicationException("Refresh token missing", "UNAUTHORIZED", HttpStatus.UNAUTHORIZED));

        // 1. Validate & extract user email from refresh token
        String email = jwtService.extractUsername(refreshToken);
        Users user = userRepo.findByUsername(email)
                .orElseThrow(() -> new ApplicationException("User not found", "NOT_FOUND", HttpStatus.NOT_FOUND));

        // 2. Verify stored hash matches this refresh token
        if (!encoder.matches(refreshToken, user.getRefreshTokenHash())) {
            throw new ApplicationException("Invalid refresh token", "UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
        }

        // 3. Check if token is expired
        if (jwtService.isTokenExpired(refreshToken)) {
            user.setRefreshTokenHash(null);
            userRepo.save(user);
            throw new ApplicationException("Refresh token expired", "UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
        }

        // 4. Generate NEW access + refresh tokens (rotation!)
        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        // 5. Save new hashed refresh token (old one invalidated)
        user.setRefreshTokenHash(encoder.encode(newRefreshToken));
        userRepo.save(user);

        // 6. Set new refresh token in HttpOnly cookie
        CookieUtils.setRefreshTokenCookie(newRefreshToken);

        return new AuthResponse(
                newAccessToken,
                user.getId(),
                user.getFullName(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    public void logout(Authentication auth, HttpServletResponse response) {
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal principal) {
            Users user = principal.getUser();
            user.setRefreshTokenHash(null);
            userRepo.save(user);
        }

        // Clear cookie
        CookieUtils.clearRefreshTokenCookie(response);

        // Optional: clear Spring Security context
        SecurityContextHolder.clearContext();
    }


}
