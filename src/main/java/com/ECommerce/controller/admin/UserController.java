package com.ECommerce.controller.admin;

import com.ECommerce.dto.response.ApiResponse;
import com.ECommerce.dto.response.admin.GetAllUserResponse;
import com.ECommerce.dto.response.admin.SingleUserDetailResponse;
import com.ECommerce.exception.ApplicationException;
import com.ECommerce.service.admin.UserService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin")
public class UserController {

    private final UserService userService;

    @GetMapping("/get-all-users")
    public ResponseEntity<ApiResponse<List<GetAllUserResponse>>> getAllUsers(){
        List<GetAllUserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.ok(users, "Fetched info of all users"));
    }

    @GetMapping("/get-details-single-user")
    public ResponseEntity<ApiResponse<SingleUserDetailResponse>> getSingleUser(
            @RequestParam("id")
            @NotNull(message = "User ID is required")
            @Positive(message = "User ID must be a positive number")
            Long id
    )throws ApplicationException {
        SingleUserDetailResponse response = userService.getSingleUserInfo(id);
        return ResponseEntity.ok(ApiResponse.ok(response, "Information of user fetched successfully"));
    }


}
