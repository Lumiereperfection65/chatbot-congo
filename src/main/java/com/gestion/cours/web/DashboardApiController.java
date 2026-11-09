package com.gestion.cours.web;

import com.gestion.cours.dao.CoursDAO;
import com.gestion.cours.dao.EtudiantDAO;
import com.gestion.cours.dao.ProfesseurDAO;
import com.gestion.cours.model.Cours;
import com.gestion.cours.util.DatabaseConfig;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DashboardApiController {
    private final CoursDAO coursDAO = new CoursDAO();
    private final EtudiantDAO etudiantDAO = new EtudiantDAO();
    private final ProfesseurDAO professeurDAO = new ProfesseurDAO();

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {
        List<Cours> courses = coursDAO.findAll();
        long activeCourses = courses.stream().filter(Cours::isActif).count();
        long upcomingCourses = courses.stream().filter(Cours::isAVenir).count();
        long completedCourses = courses.stream().filter(Cours::isTermine).count();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("cours", courses.size());
        result.put("coursActifs", activeCourses);
        result.put("coursAVenir", upcomingCourses);
        result.put("coursTermines", completedCourses);
        result.put("etudiants", etudiantDAO.findAllActive().size());
        result.put("professeurs", professeurDAO.findAllActive().size());
        result.put("inscriptions", countEnrollments());
        return result;
    }

    private int countEnrollments() {
        String sql = "SELECT COUNT(*) FROM inscriptions";
        try (Connection connection = DatabaseConfig.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Impossible de lire les statistiques d'inscription.", e);
        }
    }
}
