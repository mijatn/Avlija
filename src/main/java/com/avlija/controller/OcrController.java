package com.avlija.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/ocr")
@CrossOrigin(origins = "*")
public class OcrController {

    private static final Logger log = LoggerFactory.getLogger(OcrController.class);

    private static final String ANTHROPIC_URL = "https://api.anthropic.com/v1/messages";
    private static final String MODEL = "claude-haiku-4-5-20251001";
    private static final String PROMPT =
        "This is a restaurant receipt. Extract: " +
        "1) bill number printed after \"RACUN br.\" or \"RACUN BR\" or \"Racun br\", " +
        "2) total amount on the line containing \"TOTAL\" or \"UKUPNO\" or \"Ukupno\" (number only, no currency), " +
        "3) date in DD.MM.YYYY format, " +
        "4) waiter ID number after \"Konobar\" or \"KONOBAR\" or a similar label. " +
        "Return ONLY these four values separated by a pipe character: billNum|total|date|waiterId " +
        "— use NULL for any value not found. No other text.";

    @Value("${anthropic.api.key:}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/scan")
    public Map<String, Object> scan(@RequestBody Map<String, String> body) {
        Map<String, Object> result = new HashMap<String, Object>();

        if (apiKey == null || apiKey.isEmpty()) {
            result.put("error", "Anthropic API key not configured on server");
            return result;
        }

        String base64Image = body.get("image");
        if (base64Image == null || base64Image.isEmpty()) {
            result.put("error", "No image provided");
            return result;
        }

        try {
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", MODEL);
            requestBody.put("max_tokens", 20);

            ArrayNode messages = requestBody.putArray("messages");
            ObjectNode message = messages.addObject();
            message.put("role", "user");

            ArrayNode content = message.putArray("content");

            ObjectNode imageContent = content.addObject();
            imageContent.put("type", "image");
            ObjectNode source = imageContent.putObject("source");
            source.put("type", "base64");
            source.put("media_type", "image/jpeg");
            source.put("data", base64Image);

            ObjectNode textContent = content.addObject();
            textContent.put("type", "text");
            textContent.put("text", PROMPT);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-api-key", apiKey);
            headers.set("anthropic-version", "2023-06-01");

            HttpEntity<String> entity = new HttpEntity<String>(requestBody.toString(), headers);
            ResponseEntity<String> response = restTemplate.exchange(
                ANTHROPIC_URL, HttpMethod.POST, entity, String.class);

            JsonNode responseJson = objectMapper.readTree(response.getBody());
            String text = responseJson.path("content").get(0).path("text").asText().trim();

            log.debug("OCR result: {}", text);
            result.put("text", text);

        } catch (Exception e) {
            log.error("OCR scan failed", e);
            result.put("error", e.getMessage());
        }

        return result;
    }
}