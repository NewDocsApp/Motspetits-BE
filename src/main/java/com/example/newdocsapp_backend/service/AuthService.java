package com.example.newdocsapp_backend.service;

import com.example.newdocsapp_backend.models.User;
import com.example.newdocsapp_backend.repository.UserRepository;
import com.example.newdocsapp_backend.utils.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    public AuthService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public User register(String username, String email, String password, String fullname) {
        if(userRepository.findByEmail(email) != null)
        {
            throw new IllegalArgumentException("Email already exists");
        }

        String passwordHash = passwordEncoder.encode(password);
        User user = new User(username, fullname, email, passwordHash);
        return userRepository.save(user);
    }

    @Transactional
    public String login(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user == null || !passwordEncoder.matches(password, user.getPassword_hash())) {
            throw new IllegalArgumentException("Invalid username or password.");
        }
        return jwtUtil.generateToken(user.getId(), user.getEmail());
    }
}
