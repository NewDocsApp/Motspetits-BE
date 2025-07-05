package com.example.newdocsapp_backend.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "otp_verifications")
public class OTP {
    @Id
    @Column(name = "otp")
    private String otp;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "purpose", nullable = false)
    private String purpose;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    public OTP() {}

    public OTP(String otp, User user, String purpose, LocalDateTime expiryDate) {
        this.otp = otp;
        this.user = user;
        this.purpose = purpose;
        this.expiryDate = expiryDate;
    }

    public String getOtp() {
        return otp;
    }
    public void setOtp(String otp) {
        this.otp = otp;
    }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public String getPurpose() {
        return purpose;
    }
    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }
    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }
}
