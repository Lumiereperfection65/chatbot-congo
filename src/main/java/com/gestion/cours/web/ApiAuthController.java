package com.gestion.cours.web;

import com.gestion.cours.dao.UserDAO;
import com.gestion.cours.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {
    private final UserDAO userDAO = new UserDAO();
    private final ApiSessionRegistry sessions;

    public ApiAuthController(ApiSessionRegistry sessions) {
        this.sessions = sessions;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody(required = false) ApiDtos.Credentials credentials) {
        if (credentials == null || credentials.email == null || credentials.password == null
                || credentials.email.trim().isEmpty() || credentials.password.isEmpty()) {
            return ResponseEntity.badRequest().body(error("Saisis ton adresse e-mail et ton mot de passe."));
        }

        User user = userDAO.authentifier(credentials.email.trim(), credentials.password);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(error("E-mail ou mot de passe incorrect."));
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("token", sessions.create(user));
        response.put("user", ApiDtos.user(user));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public Map<String, String> logout(HttpServletRequest request) {
        Object token = request.getAttribute("apiToken");
        if (token instanceof String) {
            sessions.remove((String) token);
        }
        return Collections.singletonMap("message", "Déconnecté.");
    }

    @org.springframework.web.bind.annotation.GetMapping("/me")
    public Map<String, Object> currentUser(HttpServletRequest request) {
        return ApiDtos.user((User) request.getAttribute("authenticatedUser"));
    }

    private Map<String, String> error(String message) {
        return Collections.singletonMap("error", message);
    }
}
