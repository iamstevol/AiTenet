package com.example.jai.service;

public interface ModelEngine {
    boolean supports(String modelName);

    String chat(String message, String sessionId) throws Exception;
}
