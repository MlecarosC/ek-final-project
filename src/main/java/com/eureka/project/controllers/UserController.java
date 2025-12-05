package com.eureka.project.controllers;

import java.util.List;

import org.apache.catalina.connector.Response;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eureka.project.dto.UserRequestDTO;
import com.eureka.project.dto.UsersByCategoriesDTO;
import com.eureka.project.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/by-categories")
    public ResponseEntity<List<UsersByCategoriesDTO>> getUsersByCategories() {
        List<UsersByCategoriesDTO> users = userService.getUsersByCategories();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/create")
    public ResponseEntity<UserRequestDTO> save(@RequestBody @Valid UserRequestDTO user) {
        return new ResponseEntity<>(userService.save(user), HttpStatus.CREATED);
    }
}