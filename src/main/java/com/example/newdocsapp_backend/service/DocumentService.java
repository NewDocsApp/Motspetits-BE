package com.example.newdocsapp_backend.service;

import com.example.newdocsapp_backend.models.Document;
import com.example.newdocsapp_backend.models.DocumentCollaborator;
import com.example.newdocsapp_backend.models.Role;
import com.example.newdocsapp_backend.models.User;
import com.example.newdocsapp_backend.repository.DocumentCollaboratorRepository;
import com.example.newdocsapp_backend.repository.DocumentRepository;
import com.example.newdocsapp_backend.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final ObjectMapper objectMapper;
    private final DocumentCollaboratorRepository collaboratorRepository;
    private final UserRepository userRepository;

    public DocumentService(DocumentRepository documentRepository,
                           ObjectMapper objectMapper,
                           DocumentCollaboratorRepository collaboratorRepository, UserRepository userRepository) {
        this.documentRepository = documentRepository;
        this.objectMapper = objectMapper;
        this.collaboratorRepository = collaboratorRepository;
        this.userRepository = userRepository;
    }

    public Document createDocument(String title, UUID owner, String content) {
        try {
            JsonNode jsonContent = objectMapper.readTree(content);
            Document document = new Document(title, owner, jsonContent.toString());
            document.setCreatedAt(LocalDateTime.now());
            document = documentRepository.save(document);
            DocumentCollaborator collaborator = new DocumentCollaborator(document.getId(), owner, Role.editor);
            collaboratorRepository.save(collaborator);
            return document;
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid JSON content: " + e.getMessage());
        }
    }

    public Document getDocument(UUID id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found!"));
    }

    public List<Document> getAllDocument() {
        return documentRepository.findAll();
    }

    public List<Document> getUserDocuments(UUID userId) {
        return documentRepository.findByOwner(userId);
    }

    public Document updateDocument(UUID userId, UUID documentId, String title, String content) {
        DocumentCollaborator collaborator = collaboratorRepository.findDocumentCollaboratorByDocumentIdAndUserId(documentId, userId)
                .orElseThrow(() -> new IllegalArgumentException("User not authorized to update this document"));
        if(collaborator.getRole() != Role.editor) {
            throw new IllegalArgumentException("User do not have permission to update this document");
        }
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found: " + documentId));
        if (!document.getOwner().equals(userId)) {
            throw new IllegalArgumentException("User not authorized to update this document");
        }
        try {
            JsonNode jsonContent = objectMapper.readTree(content);
            document.setTitle(title);
            document.setContent(jsonContent.toString());
            document.setUpdatedAt(LocalDateTime.now());
            return documentRepository.save(document);
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid JSON content: " + e.getMessage());
        }
    }

    public void deleteDocument(UUID documentId, UUID userId) {
        boolean isOwner = collaboratorRepository.existsByDocumentIdAndUserId(documentId, userId);
        if (!isOwner) {
            throw new IllegalArgumentException("Only document owner can share");
        }
        Document document = documentRepository.findById(documentId)
                        .orElseThrow(() -> new RuntimeException("Document not found!"));
        if(!document.getOwner().equals(userId)) {
            throw new IllegalArgumentException("User not authorized to delete this document");
        }
        documentRepository.deleteById(documentId);
    }

    public void shareDocument(UUID documentId, UUID userId, UUID collaboratorId, Role role) {
        boolean isOwner =  collaboratorRepository.existsByDocumentIdAndUserId(documentId, userId);
        if (!isOwner) {
            throw new IllegalArgumentException("Only document owner can share");
        }
        documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found: " + documentId));
        if(!userRepository.existsById(collaboratorId)) {
            throw new IllegalArgumentException("User do not have permission to share this document");
        }

        DocumentCollaborator collaborator =  collaboratorRepository.findDocumentCollaboratorByDocumentIdAndUserId(documentId, collaboratorId)
                .orElse(new  DocumentCollaborator(documentId, collaboratorId, role));
        collaborator.setRole(role);
        collaboratorRepository.save(collaborator);
    }
}
