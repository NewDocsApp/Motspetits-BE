package com.example.newdocsapp_backend.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "document_changes")
public class DocumentChange {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @Column(columnDefinition = "JSONB")
    private String delta;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    public DocumentChange() {
        this.timestamp = LocalDateTime.now();
    }

    public DocumentChange(User user, Document document, String delta) {
        this.id = UUID.randomUUID();
        this.user = user;
        this.document = document;
        this.delta = delta;
        this.timestamp = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public Document getDocument() {
        return document;
    }
    public void setDocument(Document document) {
        this.document = document;
    }

    public String getDelta() {
        return delta;
    }
    public void setDelta(String delta) {
        this.delta = delta;
    }

}
