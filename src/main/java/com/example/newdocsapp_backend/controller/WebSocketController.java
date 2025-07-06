package com.example.newdocsapp_backend.controller;

import com.example.newdocsapp_backend.models.*;
import com.example.newdocsapp_backend.repository.DocumentChangeRepository;
import com.example.newdocsapp_backend.repository.DocumentCollaboratorRepository;
import com.example.newdocsapp_backend.repository.DocumentRepository;
import com.example.newdocsapp_backend.utils.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
public class WebSocketController {
    private final DocumentRepository documentRepository;
    private final DocumentChangeRepository documentChangeRepository;
    private final DocumentCollaboratorRepository documentCollaboratorRepository;
    private final JwtUtil jwtUtil;

    public WebSocketController(DocumentRepository documentRepository,
                               DocumentChangeRepository documentChangeRepository,
                               DocumentCollaboratorRepository documentCollaboratorRepository,
                               JwtUtil jwtUtil) {
        this.documentRepository = documentRepository;
        this.documentChangeRepository = documentChangeRepository;
        this.documentCollaboratorRepository = documentCollaboratorRepository;
        this.jwtUtil = jwtUtil;
    }

    @MessageMapping("/document/{documentId}")
    @SendTo("/topic/document/{documentId}")
    @Transactional
    public String handleUpdateDocument(@DestinationVariable UUID documentId, String delta, String token) {
        if(!jwtUtil.validToken(token))
        {
            throw new SecurityException("Invalid token");
        }
        UUID userId = jwtUtil.getUserIdFromToken(token);

        DocumentCollaborator documentCollab = documentCollaboratorRepository.findDocumentCollaboratorByDocumentIdAndUserId(UUID.fromString(documentId.toString()), userId)
                .orElseThrow(() -> new SecurityException("Document not found"));
        if(documentCollab.getRole() != Role.editor)
        {
            throw new SecurityException("User does not have edit permission");
        }

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found"));

        User user = new User();
        user.setId(userId);

        DocumentChange documentChange = new DocumentChange(user, document, delta);
        documentChangeRepository.save(documentChange);

        documentRepository.save(document);
        return delta;
    }
}
