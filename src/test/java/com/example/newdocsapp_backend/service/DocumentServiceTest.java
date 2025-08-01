package com.example.newdocsapp_backend.service;

import com.example.newdocsapp_backend.models.Document;
import com.example.newdocsapp_backend.models.DocumentCollaborator;
import com.example.newdocsapp_backend.repository.DocumentCollaboratorRepository;
import com.example.newdocsapp_backend.repository.DocumentRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;
    @Mock
    private ObjectMapper objectMapper;
    @Mock // Nếu bạn có DocumentCollaboratorRepository
    private DocumentCollaboratorRepository collaboratorRepository;

    @InjectMocks
    private DocumentService documentService;

    private Document document1;
    private Document document2;
    private UUID user1;
    private UUID user2;

    @BeforeEach
    void setUp() throws IOException { // Thêm throws IOException nếu mockObjectMapper gọi nó
        user1 = UUID.randomUUID();
        user2 = UUID.randomUUID();

        // Chuỗi JSON hợp lệ để sử dụng trong test
        String validJsonContent1 = "{\"name\": \"doc1_content\", \"version\": 1}";
        String validJsonContent2 = "{\"name\": \"doc2_content\", \"version\": 1}";

        document1 = new Document();
        document1.setId(UUID.randomUUID());
        document1.setTitle("title1");
        // Khi set content, bạn có thể truyền JsonNode trực tiếp nếu constructor/setter cho phép
        // Hoặc truyền String và mock ObjectMapper.readTree()
        document1.setOwner(user1);
        document1.setCreatedAt(LocalDateTime.now());
        document1.setUpdatedAt(LocalDateTime.now());


        document2 = new Document();
        document2.setId(UUID.randomUUID());
        document2.setTitle("title2");
        document2.setOwner(user1);
        document2.setCreatedAt(LocalDateTime.now());
        document2.setUpdatedAt(LocalDateTime.now());

        // MOCK HÀNH VI CỦA OBJECTMAPPER
        // Khi service gọi objectMapper.readTree(anyString()), chúng ta muốn nó trả về một JsonNode
        // Tạo một JsonNode từ chuỗi JSON hợp lệ để mock
        JsonNode mockedJsonNode1 = new ObjectMapper().readTree(validJsonContent1);
        JsonNode mockedJsonNode2 = new ObjectMapper().readTree(validJsonContent2);


        // Nếu bạn muốn một mock chung cho bất kỳ chuỗi nào:
        when(objectMapper.readTree(anyString())).thenReturn(mockedJsonNode1); // Trả về cùng một JsonNode mock cho mọi chuỗi
        document1.setContent(mockedJsonNode1);
        document2.setContent(mockedJsonNode2);


    // Bây giờ set JsonNode cho document1 và document2 (để khớp với những gì converter làm)

    }

    @Test
    void whenGetAllDocuments_thenFindAllDocuments() {
        when(documentRepository.findAll()).thenReturn(Arrays.asList(document1, document2));

        List<Document> documents = documentService.getAllDocument();

        assertThat(documents).hasSize(2);
        assertThat(documents).containsExactly(document1, document2);
        verify(documentRepository, times(1)).findAll();
    }

    @Test
    void whenGetDocumentsByOwner_thenReturnCorrectDocuments() {

        when(documentRepository.findByOwner(user1)).thenReturn(Arrays.asList(document1, document2));


        List<Document> user1Docs = documentService.getUserDocuments(user1);
        assertThat(user1Docs).hasSize(2);
        assertThat(user1Docs).containsExactly(document1, document2);
        verify(documentRepository, times(1)).findByOwner(user1);

    }

    @Test
    void whenGetDocumentById_thenReturnCorrectDocument() {
        when( documentRepository.findById(document1.getId())).thenReturn(Optional.of(document1));

        Document foundDoc = documentService.getDocument(document1.getId());
        assertThat(foundDoc).isEqualTo(document1);
        assertThat(foundDoc.getId()).isEqualTo(document1.getId());
        verify(documentRepository, times(1)).findById(document1.getId());
    }

    @Test
    void whenCreateDocument_thenDocumentIsSaved() throws IOException {

            UUID newOwnerId = UUID.randomUUID();
            String newDocContentString = "{\"key\": \"value\", \"data\": 123}"; // Đây phải là một chuỗi JSON hợp lệ

            // Mock hành vi readTree() cho chuỗi content mới này
            JsonNode newMockedJsonNode = new ObjectMapper().readTree(newDocContentString);
            when(objectMapper.readTree(newDocContentString)).thenReturn(newMockedJsonNode);


            // Tạo một Document *mong đợi* sẽ được trả về sau khi save
            Document expectedSavedDoc = new Document();
            expectedSavedDoc.setId(UUID.randomUUID());
            expectedSavedDoc.setTitle("New Document Title");
            expectedSavedDoc.setOwner(newOwnerId);
            expectedSavedDoc.setContent(newMockedJsonNode); // Đảm bảo set JsonNode đã mock
            expectedSavedDoc.setCreatedAt(LocalDateTime.now());
            expectedSavedDoc.setUpdatedAt(LocalDateTime.now());


            // Mock hành vi của repository.save() để trả về Document đã được mong đợi
            when(documentRepository.save(any(Document.class))).thenReturn(expectedSavedDoc);
            // Mock hành vi của collaboratorRepository.save() nếu nó được gọi
            when(collaboratorRepository.save(any(DocumentCollaborator.class))).thenReturn(any(DocumentCollaborator.class));


            // Gọi phương thức service bạn muốn kiểm thử
            Document actualCreatedDoc = documentService.createDocument(
                    expectedSavedDoc.getTitle(),
                    expectedSavedDoc.getOwner(),
                    newDocContentString // Truyền chuỗi JSON hợp lệ vào service
            );

            // Xác nhận kết quả
            assertThat(actualCreatedDoc).isEqualTo(expectedSavedDoc);
            assertThat(actualCreatedDoc.getContent()).isEqualTo(expectedSavedDoc.getContent()); // Kiểm tra JsonNode
            assertThat(actualCreatedDoc.getContent().get("key").asText()).isEqualTo("value"); // Kiểm tra nội dung JsonNode

            // Xác minh các cuộc gọi mock
            verify(objectMapper, times(1)).readTree(newDocContentString); // Verify objectMapper.readTree()
            verify(documentRepository, times(1)).save(any(Document.class)); // Verify documentRepository.save()
            verify(collaboratorRepository, times(1)).save(any(DocumentCollaborator.class)); // Verify collaboratorRepository.save()
    }
}
