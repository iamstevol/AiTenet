package com.example.jai.engines;

import com.example.jai.config.EnvConfig;
import com.example.jai.service.ChatMemoryService;
import com.example.jai.service.ModelEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAIEngine implements ModelEngine {

    private final ChatMemoryService chatMemoryService;
    private final EnvConfig envConfig;


    @Override
    public boolean supports(String modelName) {
        return "openAI".equalsIgnoreCase(modelName);
    }

    public String chat (String prompt, String sessionId) {

        try {
            URL url = new URL("https://api.openai.com/v1/chat/completions");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + envConfig.getApiKey());
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String body = """
                    {
                      "model": "gpt-4o-mini",
                      "messages": [
                        {"role": "user", "content": "%s"}
                      ]
                    }
                    """.formatted(prompt);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes());
            }

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream())
            );

            return br.lines().collect(Collectors.joining());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Error calling OpenAI API: " + e.getMessage());
        }
    }
}
