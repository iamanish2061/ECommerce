package com.ECommerce.service.auth;

import com.ECommerce.dto.request.EmailSenderRequest;
import com.ECommerce.dto.request.auth.LoginRequest;
import com.ECommerce.dto.request.auth.SignupRequest;
import com.ECommerce.dto.request.auth.UpdatePasswordRequest;
import com.ECommerce.dto.response.auth.AuthResponse;
import com.ECommerce.exception.ApplicationException;
import com.ECommerce.model.user.UserPrincipal;
import com.ECommerce.model.user.Users;
import com.ECommerce.redis.RedisService;
import com.ECommerce.repository.UserRepository;
import com.ECommerce.service.EmailService;
import com.ECommerce.service.JwtService;
import com.ECommerce.utils.CookieUtils;
import com.ECommerce.utils.HelperClass;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RedisService redisService;
    private final EmailService emailService;
    private final UserRepository userRepo;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private final BCryptPasswordEncoder encoder= new BCryptPasswordEncoder(12);

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
    public AuthResponse register(
            SignupRequest request, HttpServletResponse httpServletResponse
    ) throws ApplicationException {
        if(userRepo.findByUsername(request.username()).orElse(null) != null)
            throw new ApplicationException("Username is taken!", "USERNAME_EXISTS", HttpStatus.BAD_REQUEST);
        if(userRepo.findByEmail(request.email()).orElse(null) != null)
            throw new ApplicationException("Email already exists!", "EMAIL_EXISTS", HttpStatus.BAD_REQUEST);
        if(!verifyOtpCode(request.email(), request.code()))
            throw new ApplicationException("Invalid OTP code!", "INVALID_OTP_CODE", HttpStatus.BAD_REQUEST);
        if(!request.password().equals(request.rePassword()))
            throw new ApplicationException("Password do not match!", "PASSWORD_MISMATCH", HttpStatus.BAD_REQUEST);
        redisService.deleteCode(request.email());
        Users user = Users.builder()
                .fullName(request.fullname().trim())
                .username(request.username().trim())
                .password(encoder.encode(request.password().trim()))
                .email(request.email().trim())
                .createdAt(LocalDateTime.now())
                .tokenValidAfter(Instant.EPOCH)
                .build();
        Users savedUser = userRepo.save(user);
        String accessToken = jwtService.generateAccessToken(new UserPrincipal(savedUser));
        String refreshToken = jwtService.generateRefreshToken(new UserPrincipal(savedUser));
        savedUser.setRefreshToken(DigestUtils.sha256Hex(refreshToken));
        userRepo.save(savedUser);
        CookieUtils.setRefreshTokenCookie(refreshToken, httpServletResponse);
        return new AuthResponse(
                accessToken,
                savedUser.getId(),
                savedUser.getFullName(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getRole()
        );
    }


    @Transactional
    public AuthResponse login(
            LoginRequest request, HttpServletResponse httpServletResponse
    )throws ApplicationException{
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        if (!authentication.isAuthenticated()){
            throw new ApplicationException("Invalid Credentials!", "INVALID_CREDENTIALS", HttpStatus.BAD_REQUEST);
        }
        Users dbUser = userRepo.findByUsername(request.username()).orElse(null);
        // Generate tokens
        String accessToken = jwtService.generateAccessToken(new UserPrincipal(dbUser));
        String refreshToken = jwtService.generateRefreshToken(new UserPrincipal(dbUser));
        dbUser.setRefreshToken(DigestUtils.sha256Hex(refreshToken));
        dbUser.setTokenValidAfter(Instant.EPOCH);
        userRepo.save(dbUser);
        return new AuthResponse(
                accessToken,
                dbUser.getId(),
                dbUser.getFullName(),
                dbUser.getUsername(),
                dbUser.getEmail(),
                dbUser.getRole()
        );
    }


    public AuthResponse refreshToken(
            HttpServletRequest request, HttpServletResponse httpServletResponse
    ) throws ApplicationException{
        String refreshToken = CookieUtils.getRefreshTokenFromCookie(request)
                .orElseThrow(() -> new ApplicationException("Refresh token missing", "UNAUTHORIZED", HttpStatus.UNAUTHORIZED));
        // Validate & extract user email from refresh token
        String email = jwtService.extractUsername(refreshToken);
        Users user = userRepo.findByUsername(email)
                .orElseThrow(() -> new ApplicationException("User not found", "NOT_FOUND", HttpStatus.NOT_FOUND));
        // Verify stored hash matches this refresh token
        if (!DigestUtils.sha256Hex(refreshToken).equals(user.getRefreshToken())) {
            throw new ApplicationException("Invalid refresh token", "UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
        }
        // Check if token is expired
        if (jwtService.isTokenExpired(refreshToken)) {
            user.setRefreshToken(null);
            userRepo.save(user);
            throw new ApplicationException("Refresh token expired", "UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
        }
        // Generate NEW access + refresh tokens (rotation!)
        String newAccessToken = jwtService.generateAccessToken(new UserPrincipal(user));
        String newRefreshToken = jwtService.generateRefreshToken(new UserPrincipal(user));
        // Save new hashed refresh token (old one invalidated)
        user.setRefreshToken(DigestUtils.sha256Hex(newRefreshToken));
        userRepo.save(user);
        // Set new refresh token in HttpOnly cookie
        CookieUtils.setRefreshTokenCookie(newRefreshToken, httpServletResponse);
        return new AuthResponse(
                newAccessToken,
                user.getId(),
                user.getFullName(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );
    }


    public void logout(Authentication auth, HttpServletResponse httpServletResponse) {
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal principal) {
            Users user = principal.getUser();
            user.setRefreshToken(null);
            user.setTokenValidAfter(Instant.now());
            userRepo.save(user);
        }
        // Clear cookie
        CookieUtils.clearRefreshTokenCookie(httpServletResponse);
        // clear Spring Security context
        SecurityContextHolder.clearContext();
    }


    //forgot password
    public String findEmailByUsername(String username) throws ApplicationException{
        String email = userRepo.findByUsername(username)
                .map(Users::getEmail)
                .orElse(null);
        if (email == null)
            throw new ApplicationException("Email not found", "EMAIL_NOT_FOUND", HttpStatus.BAD_REQUEST);
        return email;
    }

    public String findMaskedEmailByUsername(String username) throws ApplicationException {
        return HelperClass.maskEmail(findEmailByUsername(username));
    }

    @Transactional
    public AuthResponse setTokenForUserContinuingWithoutResettingPassword(
            String username,
            String code,
            HttpServletResponse httpServletResponse
    ) throws ApplicationException {
        Users user = userRepo.findByUsername(username).orElse(null);
        if(user == null){
            throw new ApplicationException("User not found!", "USER_NOT_FOUND", HttpStatus.BAD_REQUEST);
        }
        if(!verifyOtpCode(user.getEmail(), code)){
            throw new ApplicationException("Invalid OTP code!", "INVALID_OTP_CODE", HttpStatus.BAD_REQUEST);
        }
        redisService.deleteCode(user.getEmail());
        String accessToken = jwtService.generateAccessToken(new UserPrincipal(user));
        String refreshToken = jwtService.generateRefreshToken(new UserPrincipal(user));
        user.setRefreshToken(DigestUtils.sha256Hex(refreshToken));
        userRepo.save(user);
        CookieUtils.setRefreshTokenCookie(refreshToken, httpServletResponse);
        return new AuthResponse(
                accessToken,
                user.getId(),
                user.getFullName(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );
    }

    @Transactional
    public AuthResponse updatePassword(
            UpdatePasswordRequest request, HttpServletResponse httpServletResponse
    ) throws ApplicationException{
        Users user= userRepo.findByUsername(request.username()).orElse(null);
        if(user == null)
            throw new ApplicationException("User not found!", "USER_NOT_FOUND", HttpStatus.BAD_REQUEST);
        if(!verifyOtpCode(user.getEmail(), request.code())){
            throw new ApplicationException("Invalid OTP code!", "INVALID_OTP_CODE", HttpStatus.BAD_REQUEST);
        }
        if(!request.password().equals(request.rePassword()))
            throw new ApplicationException("Password do not match!", "PASSWORD_MISMATCH", HttpStatus.BAD_REQUEST);
        redisService.deleteCode(user.getEmail());
        user.setUpdatedAt(LocalDateTime.now());
        user.setPassword(encoder.encode(request.password()));
        Users savedUser = userRepo.save(user);
        String accessToken = jwtService.generateAccessToken(new UserPrincipal(savedUser));
        String refreshToken = jwtService.generateRefreshToken(new UserPrincipal(savedUser));
        savedUser.setRefreshToken(DigestUtils.sha256Hex(refreshToken));
        userRepo.save(savedUser);
        CookieUtils.setRefreshTokenCookie(refreshToken, httpServletResponse);
        return new AuthResponse(
                accessToken,
                savedUser.getId(),
                savedUser.getFullName(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getRole()
        );
    }


}
