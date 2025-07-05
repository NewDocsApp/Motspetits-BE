package com.example.newdocsapp_backend.controller;

import com.example.newdocsapp_backend.dto.request.DocumentRequest;
import com.example.newdocsapp_backend.models.Document;
import com.example.newdocsapp_backend.service.DocumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/documents")
public class DocumentController {
    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping
    public Document createDocument(@RequestBody DocumentRequest documentRequest) {
        return documentService.createDocument(documentRequest.getTitle(), documentRequest.getOwner(), documentRequest.getContent());

    }

    @GetMapping("/{id}")
    public Document getDocument(@PathVariable UUID id) {
        return documentService.getDocument(id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Document> updateDocument(@PathVariable UUID id, @RequestBody Map<String, Object> updates) {
        return documentService.updateDocument(id, updates)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
}
