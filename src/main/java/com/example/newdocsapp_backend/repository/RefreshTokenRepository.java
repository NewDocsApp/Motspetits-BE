package com.example.newdocsapp_backend.repository;

import com.example.newdocsapp_backend.models.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,String> {
    RefreshToken findByToken(String token);
    void deleteByUserId(UUID userId);
}
