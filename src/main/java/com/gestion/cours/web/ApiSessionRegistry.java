package com.gestion.cours.web;

import com.gestion.cours.model.User;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class ApiSessionRegistry {
    private final ConcurrentMap<String, User> sessions = new ConcurrentHashMap<>();

    public String create(User user) {
        String token = UUID.randomUUID().toString();
        sessions.put(token, user);
        return token;
    }

    public User find(String token) {
        return sessions.get(token);
    }

    public void remove(String token) {
        sessions.remove(token);
    }
}
