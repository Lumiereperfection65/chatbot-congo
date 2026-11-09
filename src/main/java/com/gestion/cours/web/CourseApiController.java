package com.gestion.cours.web;

import com.gestion.cours.dao.CategorieDAO;
import com.gestion.cours.dao.CoursDAO;
import com.gestion.cours.dao.ProfesseurDAO;
import com.gestion.cours.model.Categorie;
import com.gestion.cours.model.Cours;
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
public class CourseApiController {
    private final CoursDAO coursDAO = new CoursDAO();
    private final ProfesseurDAO professeurDAO = new ProfesseurDAO();
    private final CategorieDAO categorieDAO = new CategorieDAO();

    @GetMapping("/courses")
    public List<Map<String, Object>> courses() {
        return coursDAO.findAll().stream().map(ApiDtos::course).collect(Collectors.toList());
    }

    @GetMapping("/courses/{id}")
    public Map<String, Object> course(@PathVariable Integer id) {
        Cours cours = coursDAO.findById(id);
        if (cours == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cours introuvable.");
        }
        return ApiDtos.course(cours);
    }

    @GetMapping("/options")
    public Map<String, Object> options() {
        Map<String, Object> result = new LinkedHashMap<>();
        List<Professeur> professeurs = professeurDAO.findAllActive();
        List<Categorie> categories = categorieDAO.findAllActive();
        result.put("professeurs", professeurs.stream().map(ApiDtos::professor).collect(Collectors.toList()));
        result.put("categories", categories.stream().map(ApiDtos::category).collect(Collectors.toList()));
        return result;
    }

    @PostMapping("/courses")
    public ResponseEntity<?> createCourse(@RequestBody(required = false) ApiDtos.CourseRequest request) {
        if (request == null) {
            return ResponseEntity.badRequest().body(error("Le formulaire du cours est vide."));
        }
        Cours cours = request.toEntity();
        if (!cours.isValide()) {
            return ResponseEntity.badRequest()
                    .body(error("Le titre, la présentation, le niveau et le professeur sont obligatoires."));
        }
        Cours saved = coursDAO.save(cours);
        if (saved == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Impossible d'enregistrer le cours.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiDtos.course(saved));
    }

    @PutMapping("/courses/{id}")
    public ResponseEntity<?> updateCourse(@PathVariable Integer id,
                                          @RequestBody(required = false) ApiDtos.CourseRequest request) {
        if (request == null) {
            return ResponseEntity.badRequest().body(error("Le formulaire du cours est vide."));
        }
        Cours cours = request.toEntity();
        cours.setId(id);
        if (!cours.isValide()) {
            return ResponseEntity.badRequest()
                    .body(error("Le titre, la présentation, le niveau et le professeur sont obligatoires."));
        }
        Cours updated = coursDAO.update(cours);
        if (updated == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cours introuvable ou impossible à modifier.");
        }
        return ResponseEntity.ok(ApiDtos.course(coursDAO.findById(id)));
    }

    @DeleteMapping("/courses/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable Integer id) {
        if (!coursDAO.delete(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cours introuvable.");
        }
        return ResponseEntity.noContent().build();
    }

    private Map<String, String> error(String message) {
        Map<String, String> result = new LinkedHashMap<>();
        result.put("error", message);
        return result;
    }
}
