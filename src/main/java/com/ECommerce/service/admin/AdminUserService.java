package com.ECommerce.service.admin;

import com.ECommerce.dto.response.user.GetAllUserResponse;
import com.ECommerce.dto.response.user.SingleUserDetailResponse;
import com.ECommerce.exception.ApplicationException;
import com.ECommerce.model.cartandorders.OrderModel;
import com.ECommerce.model.user.Role;
import com.ECommerce.model.user.UserStatus;
import com.ECommerce.model.user.Users;
import com.ECommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class AdminUserService {

    private final UserRepository userRepository;

    public List<GetAllUserResponse> getAllUsers() {
        List<Users> dbUsers = userRepository.findAll();
        return dbUsers.stream()
                .map(user-> new GetAllUserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getRole(),
                        user.getStatus()
                ))
                .toList();
    }

    public SingleUserDetailResponse getSingleUserInfo(Long id) throws ApplicationException{
        Users user = userRepository.findById(id).orElse(null);
        if(user == null)
            throw new ApplicationException("User not found!", "USER_NOT_FOUND", HttpStatus.BAD_REQUEST);

        return new SingleUserDetailResponse(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getStatus(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    public boolean updateRole(Long id, Role role) throws ApplicationException{
        Users user = userRepository.findById(id).orElse(null);
        if(user == null)
            throw new ApplicationException("User not found!", "USER_NOT_FOUND", HttpStatus.BAD_REQUEST);
        user.setRole(role);
        userRepository.save(user);
        return true;
    }

    public boolean updateStatus(Long id, UserStatus status) throws ApplicationException{
        Users user = userRepository.findById(id).orElse(null);
        if(user == null)
            throw new ApplicationException("User not found!", "USER_NOT_FOUND", HttpStatus.BAD_REQUEST);
        user.setStatus(status);
        userRepository.save(user);
        return true;
    }

}
