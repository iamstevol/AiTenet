package com.example.jai.controller;

import com.example.jai.service.ChatMemoryService;
import com.example.jai.service.OpenAIService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AIController {

    private final OpenAIService openAIService;

    @PostMapping("/chat/{sessionId}")
    public String chat(@PathVariable String sessionId, @RequestBody Map<String, String> request) throws Exception {
        String message = request.get("message");
        String response = openAIService.chat(message, sessionId);

        return response;
    }
}