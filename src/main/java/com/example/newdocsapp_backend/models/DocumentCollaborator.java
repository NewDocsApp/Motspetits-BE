package com.example.newdocsapp_backend.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "document_collaborators")
public class DocumentCollaborator {
    @EmbeddedId
    private DocumentCollaboratorId id;

    @ManyToOne
    @MapsId("documentId")
    @JoinColumn(name = "document_id")
    private Document document;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String role;

    @Column(name = "shared_at")
    private LocalDateTime sharedAt;

    public DocumentCollaborator() {
        this.id = new DocumentCollaboratorId();
        this.sharedAt = LocalDateTime.now();
    }

    public DocumentCollaborator(Document document, User user, String role) {
        this.id = new DocumentCollaboratorId(document.getId(), user.getId());
        this.document = document;
        this.user = user;
        this.role = role;
        this.sharedAt = LocalDateTime.now();
    }

    public DocumentCollaboratorId getId() {
        return id;
    }
    public void setId(DocumentCollaboratorId id) {
        this.id = id;
    }

    public Document getDocument() {
        return document;
    }
    public void setDocument(Document document) {
        this.document = document;
        if(this.id == null) this.id = new DocumentCollaboratorId();
        this.id.setDocumentId(document.getId());
    }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
        if (this.id == null) this.id = new DocumentCollaboratorId();
        this.id.setUserId(user.getId());
    }

    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getSharedAt() {
        return sharedAt;
    }
    public void setSharedAt(LocalDateTime sharedAt) {
        this.sharedAt = sharedAt;
    }
}
