package com.example.jai.engines;

import com.example.jai.service.ChatMemoryService;
import com.example.jai.service.ModelEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OllamaEngine implements ModelEngine {

    private final ChatMemoryService chatMemoryService;

    @Override
    public boolean supports(String modelName) {
        return "ollama".equalsIgnoreCase(modelName);
    }

    public String chat(String prompt, String sessionId) throws Exception {

        chatMemoryService.saveMessage(sessionId, "USER: " + prompt);

        List<String> history = chatMemoryService.getMessages(sessionId);
        log.info("Chat history for session {}: {}", sessionId, history);

        StringBuilder promptBuilder = new StringBuilder();

        for (String msg : history) {
            promptBuilder.append(msg).append("\n");
        }

        promptBuilder.append("AI:");

        String finalPrompt = promptBuilder.toString();
        log.info("Final prompt: " + finalPrompt);

        URL url = new URL("http://localhost:11434/api/generate");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> payload = Map.of(
                "model", "llama3",
                "prompt", finalPrompt,
                "stream", false
        );

        String body = mapper.writeValueAsString(payload);

        try(OutputStream os = conn.getOutputStream()) {
            os.write(body.getBytes());
        }

        BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream())
        );

        String response = br.lines().collect(Collectors.joining());
        chatMemoryService.saveMessage(sessionId, "AI: " + response);
        return response;
    }
}
