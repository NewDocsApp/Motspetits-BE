package com.example.newdocsapp_backend.repository;

import com.example.newdocsapp_backend.models.DocumentChange;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DocumentChangeRepository extends JpaRepository<DocumentChange, UUID> {
    List<DocumentChange> findByDocumentId(UUID documentId);
}
