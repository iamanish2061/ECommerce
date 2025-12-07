package com.ECommerce.controller.admin;

import com.ECommerce.dto.response.ApiResponse;
import com.ECommerce.dto.response.user.GetAllUserResponse;
import com.ECommerce.dto.response.user.SingleUserDetailResponse;
import com.ECommerce.exception.ApplicationException;
import com.ECommerce.model.user.Role;
import com.ECommerce.model.user.UserStatus;
import com.ECommerce.service.admin.AdminUserService;
import com.ECommerce.validation.ValidId;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final AdminUserService userService;

    @GetMapping()
    public ResponseEntity<ApiResponse<List<GetAllUserResponse>>> getAllUsers(){
        //pagination
        List<GetAllUserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.ok(users, "Fetched info of all users"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SingleUserDetailResponse>> getSingleUser(
        @ValidId @PathVariable Long id
    )throws ApplicationException {
        SingleUserDetailResponse response = userService.getSingleUserInfo(id);
        return ResponseEntity.ok(ApiResponse.ok(response, "User detail fetched successfully"));
    }

    @PutMapping("/role/{id}")
    public ResponseEntity<ApiResponse<?>> updateRole(
        @ValidId @PathVariable Long id,
        @NotBlank(message = "Role is required!")
        @RequestParam Role role
    )throws ApplicationException{
        if(userService.updateRole(id, role)){
            return ResponseEntity.ok(ApiResponse.ok("Role updated successfully"));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to update role!", "FAILED_TO_UPDATE_ROLE"));
    }

    @PutMapping("/status/{id}")
    public ResponseEntity<ApiResponse<?>> updateStatus(
            @ValidId @PathVariable Long id,
            @NotBlank(message = "Status is required!")
            @RequestParam UserStatus status
    )throws ApplicationException{
        if(userService.updateStatus(id, status)){
            return ResponseEntity.ok(ApiResponse.ok("Status updated successfully"));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to update status!", "FAILED_TO_UPDATE_STATUS"));
    }

}
