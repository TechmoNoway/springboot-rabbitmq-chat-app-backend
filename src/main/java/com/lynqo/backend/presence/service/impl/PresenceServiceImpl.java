package com.lynqo.backend.presence.service.impl;

import com.lynqo.backend.presence.service.PresenceService;
import lombok.RequiredArgsConstructor;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PresenceServiceImpl implements PresenceService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void setUserStatus(int userId, String status) {
        redisTemplate.opsForValue().set(String.valueOf(userId), status, Duration.ofDays(3));
    }

    public String getUserStatus(int userId) {
        Object status = redisTemplate.opsForValue().get(String.valueOf(userId));
        return status != null ? status.toString() : null;
    }
}
