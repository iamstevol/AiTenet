package com.example.jai.service;

import com.example.jai.engines.OllamaEngine;

import java.util.List;

public class ChatService {

    private List<ModelEngine> modelEngines;

    public String chat(String message, String sessionId, String model) {

        ModelEngine engine = modelEngines.stream()
                .filter(modelEngine -> modelEngine.supports(model))
                .findFirst()
                .orElse(null);

        engine.chat(message, sessionId);
    }
}
