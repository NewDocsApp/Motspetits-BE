package com.example.newdocsapp_backend.service;

import com.example.newdocsapp_backend.repository.OtpRepository;
import com.example.newdocsapp_backend.repository.RefreshTokenRepository;
import com.example.newdocsapp_backend.repository.UserRepository;
import com.example.newdocsapp_backend.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private OtpRepository otpRepository;

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private AuthService authService;

    @Test
    public void testLoginNullInput() {
        assertThrows(NullPointerException.class, () -> {
            authService.login(null, null);
        }, "Should throw IllegalArgumentException for null input");
    }

    @Test
    public void testLoginWithValidInput() {
        authService.login("lqd.tik22.dinhcongthao@gmail.com", "1234");
    }
}
