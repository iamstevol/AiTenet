package com.example.jai.controller;

import com.example.jai.dto.request.PromptRequest;
import com.example.jai.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AIController {

    private final ChatService chatService;

    @PostMapping("/chat/{sessionId}")
    public String chat(@PathVariable String sessionId, @RequestBody PromptRequest request) {

        return chatService.processChat(request.getMessage(), sessionId, request.getModel());
    }
}