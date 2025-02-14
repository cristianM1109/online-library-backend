package com.library.online_library.serviceAI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.library.online_library.model.Book;

class AiServiceTest {

    @Mock
    private RestTemplate restTemplate; // 🔹 Mock-uim RestTemplate pentru a evita apelurile reale

    @InjectMocks
    private AiService aiService; // 🔹 Injectăm mock-ul în serviciu

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // 🔹 Inițializăm Mockito
    }

    @Test
    void generateInsight_ShouldReturnValidResponse() {
        // 1. Creăm un obiect Book dummy
        Book book = new Book();
        book.setTitle("Test Book");
        book.setAuthor("John Doe");
        book.setDescription("A fascinating book about AI.");

        // 2. Simulăm răspunsul JSON de la OpenAI
        String mockResponse = "{ \"choices\": [{ \"message\": { \"content\": \"This is an AI-generated insight.\" } }] }";
        ResponseEntity<String> mockEntity = ResponseEntity.ok(mockResponse);

        // 3. Mock-uim `RestTemplate.postForObject()` pentru a returna mockEntity
        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(mockResponse);

        // 4. Apelăm metoda de testat
        String result = aiService.generateInsight(book);

        // 5. Verificăm rezultatul
        assertNotNull(result);
        assertEquals("This is an AI-generated insight.", result);

        // 6. Verificăm că `postForObject` a fost apelat o singură dată
        verify(restTemplate, times(1)).postForObject(anyString(), any(HttpEntity.class), eq(String.class));
    }
}
