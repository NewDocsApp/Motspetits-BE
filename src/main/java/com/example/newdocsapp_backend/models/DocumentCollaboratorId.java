package com.example.newdocsapp_backend.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class DocumentCollaboratorId implements Serializable {
    @Column(name = "document_id", columnDefinition = "UUID")
    private UUID documentId;

    @Column(name = "user_id", columnDefinition = "UUID")
    private UUID userId;

    public DocumentCollaboratorId() {}

    public DocumentCollaboratorId(UUID documentId, UUID userId) {
        this.documentId = documentId;
        this.userId = userId;
    }

    public UUID getDocumentId() {
        return documentId;
    }
    public void setDocumentId(UUID documentId) {
        this.documentId = documentId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocumentCollaboratorId that = (DocumentCollaboratorId) o;
        return Objects.equals(documentId, that.documentId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(documentId, userId);
    }
}
