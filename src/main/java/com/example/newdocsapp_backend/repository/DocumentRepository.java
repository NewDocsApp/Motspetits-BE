package com.example.newdocsapp_backend.repository;

import com.example.newdocsapp_backend.models.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {
}
