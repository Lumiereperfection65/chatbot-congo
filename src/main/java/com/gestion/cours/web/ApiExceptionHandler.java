package com.gestion.cours.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatus(ResponseStatusException exception) {
        String message = exception.getReason() == null ? "La requête a échoué." : exception.getReason();
        return ResponseEntity.status(exception.getStatus())
                .body(Collections.singletonMap("error", message));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleInvalidInput(IllegalArgumentException exception) {
        String message = exception.getMessage() == null ? "Valeur invalide." : exception.getMessage();
        return ResponseEntity.badRequest().body(Collections.singletonMap("error", message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleUnexpectedError(Exception exception) {
        org.slf4j.LoggerFactory.getLogger(ApiExceptionHandler.class)
                .error("Erreur inattendue sur l'API web", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonMap("error", "Erreur interne. Consulte les journaux du serveur."));
    }
}
