package com.example.jai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMemoryService {

    private final StringRedisTemplate redisTemplate;

    public void saveMessage(String sessionId, String message) {
        redisTemplate.opsForList().rightPush(sessionId, message);
        redisTemplate.expire(sessionId, Duration.ofMinutes(30));
    }

    public List<String> getMessages(String sessionId) {
        return redisTemplate.opsForList().range(sessionId, 0, -1);
    }
}
