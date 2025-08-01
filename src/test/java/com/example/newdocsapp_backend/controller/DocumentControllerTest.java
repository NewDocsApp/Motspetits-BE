package com.example.newdocsapp_backend.controller;

import com.example.newdocsapp_backend.security.JwtFilter;
import com.example.newdocsapp_backend.security.SecurityConfig;
import com.example.newdocsapp_backend.utils.JwtUtil;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import com.example.newdocsapp_backend.models.Document;
import com.example.newdocsapp_backend.service.DocumentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.mockito.Mockito.when;

@WebMvcTest(DocumentController.class)
public class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DocumentService documentService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private JwtFilter jwtFilter;

    @Autowired
    private ObjectMapper objectMapper;

    private Document doc1, doc2;
    private UUID owner1Id, owner2Id;
    private JsonNode content1, content2;

    @BeforeEach
    void setUp() throws IOException {
        owner1Id = UUID.randomUUID();
        owner2Id = UUID.randomUUID();

        // Tạo JsonNode hợp lệ để sử dụng trong test
        content1 = objectMapper.readTree("{\"text\":\"doc content one\"}");
        content2 = objectMapper.readTree("{\"text\":\"doc content two\"}");

        doc1 = new Document("Title One", owner1Id, content1);
        doc1.setCreatedAt(LocalDateTime.now().minusDays(2));
        doc1.setUpdatedAt(LocalDateTime.now().minusDays(1));

        doc2 = new Document("Title Two", owner1Id, content2);
        doc2.setCreatedAt(LocalDateTime.now().minusDays(1));
        doc2.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void whenGetAllDocumentsWithNoOwnerId_thenReturnAllDocuments() throws Exception {
        Document doc1 = new Document();
        doc1.setId(UUID.randomUUID());
        doc1.setTitle("Doc 1");
        Document doc2 = new Document();
        doc2.setId(UUID.randomUUID());
        doc2.setTitle("Doc 2");

        Mockito.when(documentService.getAllDocument()).thenReturn(Arrays.asList(doc1, doc2));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/documents/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIyMmMwZDllNS1jNWJhLTQyNWQtYTczYi01ZTI2N2M4Y2M4YWYiLCJlbWFpbCI6ImRjdEBnbWFpbC5jb20iLCJpYXQiOjE3NTM2Mjk4MTksImV4cCI6MTc1MzgwMjYxOX0.ARIfQmheAAS9kMWSMs5kjhexkGvjHm5OD6DiKujRj5VyWuYGIa1xnFQUoIoyx8ejfaf8K7uFPwvLmTnV8jPU0w"))
                        .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void whenGetAllDocumentsByOwnerId_thenReturnFilteredDocuments() throws Exception {
        UUID ownerId = UUID.randomUUID();
        Document doc1 = new Document();
        doc1.setId(UUID.randomUUID());
        doc1.setTitle("Doc by Owner");
        doc1.setOwner(ownerId);

        when(documentService.getUserDocuments(ownerId)).thenReturn(Arrays.asList(doc1));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/documents/my-documents")
                        .header("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIyMmMwZDllNS1jNWJhLTQyNWQtYTczYi01ZTI2N2M4Y2M4YWYiLCJlbWFpbCI6ImRjdEBnbWFpbC5jb20iLCJpYXQiOjE3NTM2Mjk4MTksImV4cCI6MTc1MzgwMjYxOX0.ARIfQmheAAS9kMWSMs5kjhexkGvjHm5OD6DiKujRj5VyWuYGIa1xnFQUoIoyx8ejfaf8K7uFPwvLmTnV8jPU0w")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void whenCreateDocument_thenDocumentIsCreatedWithOwnerId() throws Exception {
        UUID newOwnerId = UUID.randomUUID();
        String newTitle = "New Doc Created by Test";
        String newContentString = "{\"data\":\"some new json data\"}";

        // Document mà Controller sẽ gửi trong request body
        Document requestDoc = new Document();
        requestDoc.setTitle(newTitle);
        requestDoc.setOwner(newOwnerId); // Controller có thể set owner từ authenticated user
        requestDoc.setContent(objectMapper.readTree(newContentString)); // Chuyển String thành JsonNode

        // Document mà Service sẽ trả về (có ID)
        Document createdDocFromService = new Document();
        createdDocFromService.setId(UUID.randomUUID());
        createdDocFromService.setTitle(newTitle);
        createdDocFromService.setOwner(newOwnerId);
        createdDocFromService.setContent(objectMapper.readTree(newContentString));
        createdDocFromService.setCreatedAt(LocalDateTime.now());
        createdDocFromService.setUpdatedAt(LocalDateTime.now());

        // Mock hành vi của service
        // Đảm bảo các tham số khớp với cách controller gọi service
        when(documentService.createDocument(
                eq(newTitle), // Dùng eq() cho các đối tượng cụ thể
                any(UUID.class), // OwnerId có thể được lấy từ security context
                eq(newContentString) // Content là String khi vào service
        )).thenReturn(createdDocFromService);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/documents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDoc))) // Chuyển requestDoc thành JSON string
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated()) // Mong đợi 201 Created
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(newTitle))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.data").value("some new json data")); // Kiểm tra nội dung JSON
    }
    @Configuration
    static class TestSecurityConfig {
        @Bean
        SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(authorize -> authorize
                            .anyRequest().permitAll()
                    );
            return http.build();
        }
    }
}