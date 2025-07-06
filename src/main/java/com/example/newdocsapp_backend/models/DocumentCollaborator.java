package com.example.newdocsapp_backend.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "document_collaborators")
public class DocumentCollaborator {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "document_id")
    private UUID documentId;

    @Column(name = "user_id")
    private UUID userId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "shared_at")
    private LocalDateTime sharedAt;

    public DocumentCollaborator() {
        this.sharedAt = LocalDateTime.now();
    }

    public DocumentCollaborator(UUID document, UUID user, Role role) {
        this.documentId = document;
        this.userId = user;
        this.role = role;
        this.sharedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getDocument() {
        return documentId;
    }
    public void setDocument(UUID document) {
        this.documentId = document;
    }

    public UUID getUser() {
        return userId;
    }
    public void setUser(UUID user) {
        this.userId = user;
    }

    public Role getRole() {
        return role;
    }
    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getSharedAt() {
        return sharedAt;
    }
    public void setSharedAt(LocalDateTime sharedAt) {
        this.sharedAt = sharedAt;
    }


}

