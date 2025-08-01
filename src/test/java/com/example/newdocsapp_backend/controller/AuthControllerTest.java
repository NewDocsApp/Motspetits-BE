package com.example.newdocsapp_backend.controller;

import com.example.newdocsapp_backend.dto.request.LoginRequest;
import com.example.newdocsapp_backend.dto.response.AuthResponse;
import com.example.newdocsapp_backend.service.AuthService;
import com.example.newdocsapp_backend.service.UserService;
import com.example.newdocsapp_backend.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserService userService;

    private LoginRequest loginRequest;
    private AuthResponse loginResponse;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest("lqd.tik22.dinhcongthao@gmail.com", "123");
        loginResponse = new AuthResponse(
                "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIyMmMwZDllNS1jNWJhLTQyNWQtYTczYi01ZTI2N2M4Y2M4YWYiLCJlbWFpbCI6ImRjdEBnbWFpbC5jb20iLCJpYXQiOjE3NTM1MzIzMDUsImV4cCI6MTc1MzcwNTEwNX0.YuxihMKua7Ktwh-7JCJysV0k1MieuTy_0gY_ddf4WoJw16iZPGlPmEwZGvuV6TdPs0BoxxPkTtgp9EZnVpR1gw",
                "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIyMmMwZDllNS1jNWJhLTQyNWQtYTczYi01ZTI2N2M4Y2M4YWYiLCJlbWFpbCI6ImRjdEBnbWFpbC5jb20iLCJpYXQiOjE3NTM1MzIzMDUsImV4cCI6MTc1NDEzNzEwNX0.xZkOrYNEQTSSkmjxSQkR-D569apaAQumqmyVw-yx9dFeB0qXC3-uPvo4CLQezrBDgklX7QqEFgxrmnMwMWGEFQ"
        );
    }

    @Test
    public void testLoginSuccess() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(loginRequest);
        Mockito.when(authService.login(loginRequest.getEmail(), loginRequest.getPassword())).thenReturn(loginResponse);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").value(loginResponse.getAccessToken()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken").value(loginResponse.getRefreshToken()));
    }


    @Configuration
    static class TestSecurityConfig {
        @Bean
        SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(authorize -> authorize
                            .anyRequest().permitAll()
                    );
            return http.build();
        }
    }

}