package com.ECommerce.service.admin;

import com.ECommerce.dto.response.admin.GetAllUserResponse;
import com.ECommerce.dto.response.admin.SingleUserDetailResponse;
import com.ECommerce.exception.ApplicationException;
import com.ECommerce.model.Role;
import com.ECommerce.model.UserStatus;
import com.ECommerce.model.Users;
import com.ECommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {

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
                user.getCreatedAt()
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
