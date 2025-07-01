package com.example.newdocsapp_backend.service;

import com.example.newdocsapp_backend.models.User;
import com.example.newdocsapp_backend.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(String username, String fullname ,String email, String password) {
        User user = new User(username, fullname, email, password);
        return  userRepository.save(user);
    }
    
}
