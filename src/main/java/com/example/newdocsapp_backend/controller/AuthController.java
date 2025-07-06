package com.example.newdocsapp_backend.controller;

import com.example.newdocsapp_backend.dto.request.LoginRequest;
import com.example.newdocsapp_backend.dto.request.RegisterRequest;
import com.example.newdocsapp_backend.dto.response.AuthResponse;
import com.example.newdocsapp_backend.models.User;
import com.example.newdocsapp_backend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterRequest request) {
        User user = authService.register(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getFullname()
        );
        return ResponseEntity.ok(user);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody VerifyOtpRequest verifyOtpRequest) {
        authService.verifyOtp(verifyOtpRequest.getOtp(), verifyOtpRequest.getPurpose());
        return ResponseEntity.ok("OTP VERIFIED");
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshRequest request) {
        String accessToken = authService.refreshAccessToken(request.getToken());
        return ResponseEntity.ok(new AuthResponse(accessToken, request.getToken()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        AuthResponse reponse = authService.login(
                request.getEmail(),
                request.getPassword()
        );
        return ResponseEntity.ok(reponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody String token) {
        authService.logout(token);
        return ResponseEntity.ok("Logged out successfully");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody String email) {
        authService.requestPasswordReset(email);
        return ResponseEntity.ok("OTP sent!");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        authService.resetPassword(resetPasswordRequest.getOtp(), resetPasswordRequest.getNewPassword());
        return ResponseEntity.ok("Password reset successfully");
    }
}

class VerifyOtpRequest {
    private String otp;
    private String purpose;

    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
}

class ResetPasswordRequest {
    private String otp;
    private String newPassword;
    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}

class RefreshRequest {
    private String token;
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
