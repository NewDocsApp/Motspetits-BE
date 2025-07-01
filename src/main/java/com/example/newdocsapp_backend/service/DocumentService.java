package com.example.newdocsapp_backend.service;

import com.example.newdocsapp_backend.models.Document;
import com.example.newdocsapp_backend.models.User;
import com.example.newdocsapp_backend.repository.DocumentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class DocumentService {
    private final DocumentRepository documentRepository;
    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public Document createDocument(String title, User owner, String content) {
        Document document = new Document(title, owner, content);
        return documentRepository.save(document);
    }

    public Document getDocument(UUID id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found!"));
    }

    public List<Document> getAllDocument() {
        return documentRepository.findAll();
    }

    public Optional<Document> updateDocument(UUID id, Map<String, Object> updated) {
        return documentRepository.findById(id).map(document -> {
            if(updated.containsKey("title")) {
                document.setTitle((String) updated.get("title"));
            }
            if (updated.containsKey("content")) {
                document.setContent((String) updated.get("content"));
            }
            return documentRepository.save(document);
        });
    }

    public void deleteDocument(UUID id) {
        documentRepository.deleteById(id);
    }
}
