package com.example.newdocsapp_backend.repository;

import com.example.newdocsapp_backend.models.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {
    List<Document> findByOwner(UUID owner);

}
