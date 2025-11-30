package com.ECommerce.controller.admin;

import com.ECommerce.dto.response.ApiResponse;
import com.ECommerce.dto.response.admin.GetAllUserResponse;
import com.ECommerce.dto.response.admin.SingleUserDetailResponse;
import com.ECommerce.exception.ApplicationException;
import com.ECommerce.service.admin.UserService;
import com.ECommerce.validation.ValidId;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin")
public class UserController {

    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<GetAllUserResponse>>> getAllUsers(){
        //pagination
        List<GetAllUserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.ok(users, "Fetched info of all users"));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<ApiResponse<SingleUserDetailResponse>> getSingleUser(
            @ValidId @PathVariable Long id
    )throws ApplicationException {
        SingleUserDetailResponse response = userService.getSingleUserInfo(id);
        return ResponseEntity.ok(ApiResponse.ok(response, "User detail fetched successfully"));
    }


}
