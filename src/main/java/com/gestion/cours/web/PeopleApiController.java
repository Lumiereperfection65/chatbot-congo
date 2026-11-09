package com.gestion.cours.web;

import com.gestion.cours.dao.EtudiantDAO;
import com.gestion.cours.dao.ProfesseurDAO;
import com.gestion.cours.model.Etudiant;
import com.gestion.cours.model.Professeur;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class PeopleApiController {
    private final ProfesseurDAO professeurDAO = new ProfesseurDAO();
    private final EtudiantDAO etudiantDAO = new EtudiantDAO();

    @GetMapping("/professors")
    public List<Map<String, Object>> professors() {
        return professeurDAO.findAllActive().stream()
                .map(ApiDtos::professor).collect(Collectors.toList());
    }

    @PostMapping("/professors")
    public ResponseEntity<?> createProfessor(@RequestBody(required = false) ApiDtos.ProfessorRequest request) {
        if (request == null || !request.toEntity().isValide()) {
            return ResponseEntity.badRequest()
                    .body(error("Nom, prénom, e-mail et spécialité sont obligatoires."));
        }
        Professeur saved = professeurDAO.save(request.toEntity());
        if (saved == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Impossible de créer le professeur. Vérifie que l'e-mail n'est pas déjà utilisé.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiDtos.professor(saved));
    }

    @PutMapping("/professors/{id}")
    public ResponseEntity<?> updateProfessor(@PathVariable Integer id,
                                             @RequestBody(required = false) ApiDtos.ProfessorRequest request) {
        if (request == null || !request.toEntity().isValide()) {
            return ResponseEntity.badRequest()
                    .body(error("Nom, prénom, e-mail et spécialité sont obligatoires."));
        }
        Professeur professor = request.toEntity();
        professor.setId(id);
        Professeur updated = professeurDAO.update(professor);
        if (updated == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Professeur introuvable ou e-mail déjà utilisé.");
        }
        return ResponseEntity.ok(ApiDtos.professor(updated));
    }

    @DeleteMapping("/professors/{id}")
    public ResponseEntity<?> deleteProfessor(@PathVariable Integer id) {
        if (!professeurDAO.delete(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Professeur introuvable.");
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/students")
    public List<Map<String, Object>> students() {
        return etudiantDAO.findAllActive().stream()
                .map(ApiDtos::student).collect(Collectors.toList());
    }

    @PostMapping("/students")
    public ResponseEntity<?> createStudent(@RequestBody(required = false) ApiDtos.StudentRequest request) {
        if (request == null || !request.toEntity().isValide()) {
            return ResponseEntity.badRequest().body(error("Nom, prénom et e-mail sont obligatoires."));
        }
        Etudiant saved = etudiantDAO.save(request.toEntity());
        if (saved == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Impossible de créer l'étudiant. Vérifie que l'e-mail n'est pas déjà utilisé.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiDtos.student(saved));
    }

    @PutMapping("/students/{id}")
    public ResponseEntity<?> updateStudent(@PathVariable Integer id,
                                           @RequestBody(required = false) ApiDtos.StudentRequest request) {
        if (request == null || !request.toEntity().isValide()) {
            return ResponseEntity.badRequest().body(error("Nom, prénom et e-mail sont obligatoires."));
        }
        Etudiant student = request.toEntity();
        student.setId(id);
        Etudiant updated = etudiantDAO.update(student);
        if (updated == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Étudiant introuvable ou e-mail déjà utilisé.");
        }
        return ResponseEntity.ok(ApiDtos.student(updated));
    }

    @DeleteMapping("/students/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable Integer id) {
        if (!etudiantDAO.delete(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Étudiant introuvable.");
        }
        return ResponseEntity.noContent().build();
    }

    private Map<String, String> error(String message) {
        Map<String, String> result = new LinkedHashMap<>();
        result.put("error", message);
        return result;
    }
}
