package com.ECommerce.service.user;

import com.ECommerce.model.user.UserPrincipal;
import com.ECommerce.model.user.Users;
import com.ECommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = userRepo.findByUsername(username).orElse(null);

        if(user == null){
            throw new UsernameNotFoundException("User not found!");
        }
        return new UserPrincipal(user);
    }
}
