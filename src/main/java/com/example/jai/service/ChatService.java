package com.example.jai.service;

import com.example.jai.engines.OllamaEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final List<ModelEngine> modelEngines;

    public String processChat(String message, String sessionId, String model) throws Exception {

        if (model == null || model.isEmpty()) {
            return findEngine("ollama").chat(message, sessionId);
        }

        ModelEngine engine = modelEngines.stream()
                .filter(modelEngine -> modelEngine.supports(model))
                .findFirst()
                .orElse(null);

        return engine.chat(message, sessionId);
    }

    private ModelEngine findEngine(String name) {
        return modelEngines.stream()
                .filter(e -> e.supports(name))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Default engine not found"));
    }
}
