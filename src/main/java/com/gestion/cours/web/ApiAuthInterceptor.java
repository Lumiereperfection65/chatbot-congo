package com.gestion.cours.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestion.cours.model.User;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Component
public class ApiAuthInterceptor implements HandlerInterceptor {
    private final ApiSessionRegistry sessions;
    private final ObjectMapper objectMapper;

    public ApiAuthInterceptor(ApiSessionRegistry sessions, ObjectMapper objectMapper) {
        this.sessions = sessions;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring("Bearer ".length()).trim();
            User user = sessions.find(token);
            if (user != null) {
                request.setAttribute("authenticatedUser", user);
                request.setAttribute("apiToken", token);
                return true;
            }
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(),
                Collections.singletonMap("error", "Authentification requise ou session expirée."));
        return false;
    }
}
