package com.example.newdocsapp_backend.repository;

import com.example.newdocsapp_backend.models.DocumentCollaborator;
import com.example.newdocsapp_backend.models.DocumentCollaboratorId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentCollaboratorRepository extends JpaRepository<DocumentCollaborator, DocumentCollaboratorId> {
    Optional<DocumentCollaborator> findDocumentCollaboratorByDocumentIdAndUserId(UUID documentId, UUID userId);
    List<DocumentCollaborator> findByDocumentId(UUID documentId);
    boolean existsByDocumentIdAndUserId(UUID documentId, UUID userId);
    void deleteByDocumentId(UUID documentId);
}
