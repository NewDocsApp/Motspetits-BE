package com.example.newdocsapp_backend.service;

import com.example.newdocsapp_backend.dto.response.AuthResponse;
import com.example.newdocsapp_backend.models.OTP;
import com.example.newdocsapp_backend.models.RefreshToken;
import com.example.newdocsapp_backend.models.User;
import com.example.newdocsapp_backend.repository.OtpRepository;
import com.example.newdocsapp_backend.repository.RefreshTokenRepository;
import com.example.newdocsapp_backend.repository.UserRepository;
import com.example.newdocsapp_backend.utils.JwtUtil;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OtpRepository otpRepository;
    private final JavaMailSender mailSender;
    @Value("${otp.expiration}")
    private Long otpExpiration;
    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpiration;
    public AuthService(UserRepository userRepository,
                       JwtUtil jwtUtil,
                       RefreshTokenRepository refreshTokenRepository,
                       OtpRepository otpRepository,
                       JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;
        this.otpRepository = otpRepository;
        this.mailSender = mailSender;
    }

    @Transactional
    public User register(String username, String email, String password, String fullname) {
        if(userRepository.findByEmail(email) != null)
        {
            throw new IllegalArgumentException("Email already exists");
        }

        String passwordHash = passwordEncoder.encode(password);
        User user = new User(username, fullname, email, passwordHash);
        user = userRepository.save(user);

        try {
            sendOtp(user, "ACCOUNT_VERIFICATION");
        } catch (MailException e) {
            logger.error("Failed to send OTP email to {}: {}", email, e.getMessage());
            throw new RuntimeException("Failed to send OTP. Please try again later.");
        }

        return user;
    }

    @Transactional
    public void verifyOtp(String otp, String purpose) {
        OTP otpVerification = otpRepository.findByOtpAndPurpose(otp, purpose);
        if(otpVerification == null)
        {
            throw new IllegalArgumentException("Invalid OTP");
        }
        User user = otpVerification.getUser();
        if(purpose.equals("ACCOUNT_VERIFICATION"))
        {
            user.setVerified(true);
            userRepository.save(user);
        }
        otpRepository.delete(otpVerification);
    }

    @Transactional
    public AuthResponse login(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user == null || !passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid username or password.");
        }

        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getEmail());
        Date expiration = new Date(System.currentTimeMillis() + refreshTokenExpiration);
        RefreshToken token = new RefreshToken(refreshToken, user, expiration);
        refreshTokenRepository.save(token);

        return new AuthResponse(accessToken, refreshToken);
    }

    @Transactional
    public String refreshAccessToken(String refreshToken) {
        logger.info("Refreshing access token for user {}", refreshToken);
        RefreshToken token = refreshTokenRepository.findByToken(refreshToken);
        if (token == null ) {
            throw new IllegalArgumentException("No refresh token.");
        }

        if(token.getExpiryDate().before(new Date()))
        {
            throw new IllegalArgumentException("Invalid refresh token.");
        }

        User user = token.getUser();
        return jwtUtil.generateAccessToken(user.getId(), user.getEmail());
    }

    @Transactional
    public void logout(String refreshToken) {
        RefreshToken token = refreshTokenRepository.findByToken(refreshToken);
        if(token != null)
        {
            refreshTokenRepository.delete(token);
        }
    }

    @Transactional
    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("Invalid email address.");
        }

        otpRepository.deleteByExpiryDateBefore(LocalDateTime.now());
        sendOtp(user, "RESET_PASSWORD");
    }

    @Transactional
    public void resetPassword(String otp, String newPassword) {
        OTP otpVerification = otpRepository.findByOtpAndPurpose(otp, "RESET_PASSWORD");
        if(otpVerification != null || otpVerification.getExpiryDate().isBefore(LocalDateTime.now()))
        {
            throw new IllegalArgumentException("Invalid OTP.");
        }

        User user = userRepository.findByEmail(otp);
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        refreshTokenRepository.deleteByUserId(user.getId());
        otpRepository.delete(otpVerification);
    }

    private void sendOtp(User user, String purpose) {
        String otp = generateOtp();
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(otpExpiration/1000);
        OTP otpVerification = new OTP(otp, user, purpose, expiryDate);
        otpRepository.save(otpVerification);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject(purpose.equals("RESET_PASSWORD") ? "Password Reset OTP" : "Account Verification OTP");
        mailMessage.setText("Your OTP is: " + otp + "\nThis OTP will expire in 5 minutes.");
        mailSender.send(mailMessage);

    }
    private String generateOtp()
    {
        SecureRandom random = new SecureRandom();
        int otp = 10000 + random.nextInt(90000);
        return String.valueOf(otp);
    }

}
