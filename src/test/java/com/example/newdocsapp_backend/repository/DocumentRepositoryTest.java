package com.example.newdocsapp_backend.repository;

import com.example.newdocsapp_backend.models.Document;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
public class DocumentRepositoryTest {

    @Autowired
    private DocumentRepository documentRepository;

    private Document doc1;
    private Document doc2, doc3;
    private UUID user1;
    private UUID user2;
    private JsonNode content1, content2;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws IOException {
        documentRepository.deleteAll();

        user1 = UUID.randomUUID();
        user2 = UUID.randomUUID();

        // Tạo JsonNode hợp lệ cho nội dung
        content1 = objectMapper.readTree("{\"text\":\"hello world\", \"version\": 1}");
        content2 = objectMapper.readTree("{\"text\":\"another doc\", \"format\":\"markdown\"}");

        // Tạo các đối tượng Document
        doc1 = new Document();
        doc1.setId(UUID.randomUUID()); // Gán ID tạm thời cho test, DB sẽ tự sinh nếu cấu hình
        doc1.setTitle("My First Doc");
        doc1.setContent(content1);
        doc1.setOwner(user1);
        doc1.setCreatedAt(LocalDateTime.now().minusDays(2));
        doc1.setUpdatedAt(LocalDateTime.now().minusDays(1));

        doc2 = new Document();
        doc2.setId(UUID.randomUUID());
        doc2.setTitle("Shared Project Doc");
        doc2.setContent(content2);
        doc2.setOwner(user2);
        doc2.setCreatedAt(LocalDateTime.now().minusDays(1));
        doc2.setUpdatedAt(LocalDateTime.now());

        doc3 = new Document();
        doc3.setId(UUID.randomUUID());
        doc3.setTitle("Private Notes");
        doc3.setContent(content1); // Dùng lại content1
        doc3.setOwner(user2);
        doc3.setCreatedAt(LocalDateTime.now());
        doc3.setUpdatedAt(LocalDateTime.now());

        // Lưu các Document vào cơ sở dữ liệu test
        doc1 = documentRepository.save(doc1); // Gán lại để lấy ID do DB sinh nếu có
        doc2 = documentRepository.save(doc2);
        doc3 = documentRepository.save(doc3);
    }

    @Test
    void whenFindById_thenReturnDocument() {
        Optional<Document> foundDoc = documentRepository.findById(doc1.getId());
        assertThat(foundDoc).isPresent();
        assertThat(foundDoc.get().getTitle()).isEqualTo("My First Doc");
        assertThat(foundDoc.get().getOwner()).isEqualTo(user1);
        assertThat(foundDoc.get().getContent()).isEqualTo(content1);
    }

    @Test
    void whenFindByOwnerId_thenReturnDocuments() {
        List<Document> owner1Docs = documentRepository.findByOwner(user1);
        assertThat(owner1Docs).hasSize(2);
        assertThat(owner1Docs).containsExactlyInAnyOrder(doc1, doc2);

        List<Document> owner2Docs = documentRepository.findByOwner(user2);
        assertThat(owner2Docs).hasSize(1);
        assertThat(owner2Docs).containsExactly(doc3);
    }

}
