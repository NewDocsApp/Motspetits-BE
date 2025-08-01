package com.example.newdocsapp_backend.integration;

import com.example.newdocsapp_backend.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DocumentIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testCreateDocument() {
        ResponseEntity<String> response = restTemplate.postForEntity("api/v1/documents", null, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Should create document successfully");
    }
}

