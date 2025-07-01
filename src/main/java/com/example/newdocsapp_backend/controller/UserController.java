package com.example.newdocsapp_backend.controller;


import com.example.newdocsapp_backend.dto.UserRequest;
import com.example.newdocsapp_backend.models.User;
import com.example.newdocsapp_backend.service.DocumentService;
import com.example.newdocsapp_backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/")
    public User createUser(@RequestBody UserRequest newUser) {
        return userService.createUser(newUser.getUsername(), newUser.getFullname(), newUser.getEmail(), newUser.getPassword());
    }
}
