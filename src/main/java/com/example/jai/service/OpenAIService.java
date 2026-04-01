package com.example.jai.service;

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
public class OpenAIService {

    private final ChatMemoryService chatMemoryService;
    private final String API_KEY = "REMOVED_KEY-oijMFoEfE4HJkPnAcdzggy2U8V92RWaM7oVg5UZ72jECInzrR4mQ8Tn2n-HBodxzsrJM_JjWm9T3BlbkFJWhaVF_FRWzTO74f0RtuHVWdDYJNOHEFj1-J1Os3D-K9iazDTXN_kZzMh8TeNaxNu7gYWLtrngA";

    public String openApiChat(String userMessage) throws Exception {

        URL url = new URL("https://api.openai.com/v1/chat/completions");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        String body = """
        {
          "model": "gpt-4o-mini",
          "messages": [
            {"role": "user", "content": "%s"}
          ]
        }
        """.formatted(userMessage);

        try(OutputStream os = conn.getOutputStream()) {
            os.write(body.getBytes());
        }

        BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream())
        );

        return br.lines().collect(Collectors.joining());
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
