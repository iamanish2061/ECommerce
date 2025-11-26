package com.ECommerce.service.auth;

import com.ECommerce.model.UserPrincipal;
import com.ECommerce.model.Users;
import com.ECommerce.redis.RedisService;
import com.ECommerce.repository.UserRepository;
import com.ECommerce.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class AuthService {
    @Autowired
    private RedisService redisService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    private BCryptPasswordEncoder encoder= new BCryptPasswordEncoder(12);


    public Users register(Users user){

        if(userRepo.findByUsername(user.getUsername()).orElse(null) != null)
            throw new RuntimeException("Username already exists!!");

        if(userRepo.findByEmail(user.getUsername()).orElse(null) != null)
            throw new RuntimeException("Email already exists!!");

        if(!verifyCode(user.getEmail(), user.getCode()))
            throw new RuntimeException("Email and code mismatched");
        redisService.deleteCode(user.getEmail());
        user.setPassword(encoder.encode(user.getPassword()));

        //baki xa sab set garna
        return userRepo.save(user);
    }

    public Map<String, String> login(Users user){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

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

    public Map<String, String> refresh(String refreshToken) {
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


    public boolean doesUserNameExist(String username) {
        return userRepo.existsByUsername(username);
    }

    public boolean doesEmailExist(String email) {
        return userRepo.existsByEmail(email);
    }

    public boolean sendCode(String email) {
        Integer otpCode = new Random().nextInt(900000) +100000;

        redisService.setCode(email, otpCode.toString(), 600L);
        String subject = "Verifying Email";
        String emailBody = "Your otp code is: "+otpCode+". Please do not share with anyone! If you find this irrelevant, please ignore it!";
        return emailService.sendEmail(new EmailService.EmailSender(email, subject,emailBody));
    }

    public boolean verifyCode(String email, String code) {
        String generatedCode = redisService.getCode(email);
        return generatedCode != null && generatedCode.equals(code);
    }
}
