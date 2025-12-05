package com.eureka.project.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eureka.project.dto.UsersByCategoriesDTO;
import com.eureka.project.services.UserService;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/by-categories")
    public List<UsersByCategoriesDTO> getUsersByCategories() {
        return userService.getUsersByCategories();
    }
}