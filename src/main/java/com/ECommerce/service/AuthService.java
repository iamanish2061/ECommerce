package com.ECommerce.service;

import com.ECommerce.model.UserPrincipal;
import com.ECommerce.model.Users;
import com.ECommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    private BCryptPasswordEncoder encoder= new BCryptPasswordEncoder(12);

    //user
    public Users register(Users user){

        //validation

        if(userRepo.findByUsername(user.getUsername()).orElse(null) != null)
            throw new RuntimeException("Username already exists!!");

        if(userRepo.findByEmail(user.getUsername()).orElse(null) != null)
            throw new RuntimeException("Email already exists!!");

        //verify code and email
        // remove code from redis or wherever it is saved
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    public Map<String, String> login(Users user){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        // authenticate is the unimplemented method of AuthenticationManager interface whose one of the implementations is done is ProviderManager class
        // provider manager.authenticate() is called -> then it iterates through the available provider
        // i am using DAO so its dynamically added to the list of providerManager
        //          now authentication is done by dao class's object
        //        in that method :the flow is
        //        1. load the user by username from database
        //        (method present in UserDetailsService) and i have implemented that on CustomUserDetailsService
        //        2. verifying password by encoding entered one to db's password
        //        3. returning an authenticated token with authorities


        if (!authentication.isAuthenticated()) throw new RuntimeException("Invalid credentials");

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
        return (userRepo.findByUsername(username).orElse(null) == null) ? false: true;
    }
}
