package com.library.online_library.serviceAI;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.library.online_library.model.Book;

@Service
public class AiService {

    @Value("${openai.api.key}")
    private String openAiApiKey;

    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

    private final RestTemplate restTemplate;

    public AiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String generateInsight(Book book) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "gpt-3.5-turbo");

        JSONArray messages = new JSONArray();

        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", "You are a helpful AI assistant that generates engaging insights about books.");
        messages.put(systemMessage);

        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", "Generate a short, engaging tagline for the book titled '"
                + book.getTitle() + "' by " + book.getAuthor()
                + ". The book is about: " + book.getDescription());
        messages.put(userMessage);

        requestBody.put("messages", messages);
        requestBody.put("max_tokens", 100);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiApiKey);

        HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), headers);

        String response = restTemplate.postForObject(OPENAI_URL, request, String.class);

        JSONObject jsonResponse = new JSONObject(response);
        return jsonResponse.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content").trim();
    }
}
