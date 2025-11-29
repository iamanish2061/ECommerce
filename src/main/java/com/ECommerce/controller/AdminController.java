package com.ECommerce.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class AdminController {

    @GetMapping("/get-users")
    public List<AdminUserRequest> getUsers(){

        return new ArrayList<>();
    }

}
