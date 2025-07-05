package com.example.newdocsapp_backend.repository;

import com.example.newdocsapp_backend.models.OTP;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface OtpRepository extends JpaRepository<OTP, String> {
    OTP findByOtpAndPurpose(String otp, String purpose);

    void deleteByExpiryDateBefore(LocalDateTime expiryDateBefore);
}
