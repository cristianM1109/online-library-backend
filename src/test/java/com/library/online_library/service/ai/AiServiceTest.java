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
    private RestTemplate restTemplate; 

    @InjectMocks
    private AiService aiService; 

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); 
    }

    @Test
    void generateInsight_ShouldReturnValidResponse() {
        
        Book book = new Book();
        book.setTitle("Test Book");
        book.setAuthor("John Doe");
        book.setDescription("A fascinating book about AI.");

        String mockResponse = "{ \"choices\": [{ \"message\": { \"content\": \"This is an AI-generated insight.\" } }] }";
        ResponseEntity<String> mockEntity = ResponseEntity.ok(mockResponse);

        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(mockResponse);

        String result = aiService.generateInsight(book);

        assertNotNull(result);
        assertEquals("This is an AI-generated insight.", result);

        verify(restTemplate, times(1)).postForObject(anyString(), any(HttpEntity.class), eq(String.class));
    }
}
