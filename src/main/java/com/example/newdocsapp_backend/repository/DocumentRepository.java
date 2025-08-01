package com.example.newdocsapp_backend.repository;

import com.example.newdocsapp_backend.models.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {
    List<Document> findAll();
    List<Document> findByOwner(UUID owner);
    Optional<Document> findById(UUID id);

}
