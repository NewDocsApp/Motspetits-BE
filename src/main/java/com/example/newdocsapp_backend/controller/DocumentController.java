package com.example.newdocsapp_backend.controller;

import com.example.newdocsapp_backend.dto.request.DocumentRequest;
import com.example.newdocsapp_backend.models.Document;
import com.example.newdocsapp_backend.models.Role;
import com.example.newdocsapp_backend.service.DocumentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/documents")
public class DocumentController {
    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("/")
    public ResponseEntity<Document> createDocument(@RequestBody DocumentRequest documentRequest, HttpServletRequest request) {
        UUID ownerId = UUID.fromString((String) request.getAttribute("userId"));
        Document newDoc = documentService.createDocument(documentRequest.getTitle(), ownerId, documentRequest.getContent());

        return ResponseEntity.status(HttpStatus.CREATED).body(newDoc);
    }

    @GetMapping("/{id}")
    public Document getDocument(@PathVariable UUID id) {
        return documentService.getDocument(id);
    }

    @GetMapping("/")
    public List<Document> getAllDocuments() {
        return documentService.getAllDocument();
    }

    @GetMapping("/my-documents")
    public List<Document> getMyDocuments(HttpServletRequest request) {
        UUID ownerId = UUID.fromString((String) request.getAttribute("userId"));
        return documentService.getUserDocuments(ownerId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Document> updateDocument(@PathVariable UUID id, @RequestBody DocumentRequest documentRequest, HttpServletRequest request) {
        UUID ownerId = UUID.fromString((String) request.getAttribute("userId"));
        Document document = documentService.updateDocument(ownerId, id, documentRequest.getTitle(), documentRequest.getContent());
        return  ResponseEntity.ok(document);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteDocument(@PathVariable UUID id, HttpServletRequest request) {
        UUID ownerId = UUID.fromString((String) request.getAttribute("userId"));
        documentService.deleteDocument(id, ownerId);
        Map<String, String> response = new HashMap<>();
        try
        {
            response.put("message", "Document deleted successfully");
        }
        catch (Exception e)
        {
            response.put("message", "Error deleting document: " +  e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/share/{id}")
    public ResponseEntity<Map<String, ?>> shareDocument(@PathVariable UUID id, @RequestBody ShareRequest shareRequest, HttpServletRequest request) {
        UUID ownerId = UUID.fromString((String) request.getAttribute("userId"));
        documentService.shareDocument(id, ownerId, shareRequest.getTargetUserId(), shareRequest.getRole());
        Map<String, String> response = new HashMap<>();
        try
        {
            response.put("message", "Document share successfully");
        }
        catch (Exception e)
        {
            response.put("message", "Error saving document: " +  e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
}

class ShareRequest {
    private UUID targetUserId;
    private Role role;

    public UUID getTargetUserId() { return targetUserId; }
    public void setTargetUserId(UUID targetUserId) { this.targetUserId = targetUserId; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
