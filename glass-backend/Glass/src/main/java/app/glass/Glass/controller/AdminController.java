package app.glass.Glass.controller;

import app.glass.Glass.dto.CreateQuizRequest;
import app.glass.Glass.model.Quiz;
import app.glass.Glass.model.QuizAccess;
import app.glass.Glass.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/quiz/create")
    public ResponseEntity<Quiz> createQuiz(
            @RequestBody CreateQuizRequest request,
            @RequestHeader("Authorization") String token) {

        Quiz quiz = adminService.createQuiz(request, token);
        return ResponseEntity.ok(quiz);
    }

    @PutMapping("/quiz/{quizId}/publish")
    public ResponseEntity<Quiz> publishQuiz(@PathVariable Long quizId) {
        Quiz quiz = adminService.publishQuiz(quizId);
        return ResponseEntity.ok(quiz);
    }

    @PutMapping("/quiz/{quizId}/archive")
    public ResponseEntity<Quiz> archiveQuiz(@PathVariable Long quizId) {
        Quiz quiz = adminService.archiveQuiz(quizId);
        return ResponseEntity.ok(quiz);
    }

    @PostMapping("/quiz/{quizId}/grant-access")
    public ResponseEntity<QuizAccess> grantAccess(
            @PathVariable Long quizId,
            @RequestParam Long userId,
            @RequestHeader("Authorization") String token) {

        QuizAccess access = adminService.grantQuizAccess(quizId, userId, token);
        return ResponseEntity.ok(access);
    }

    @GetMapping("/quiz/{quizId}/access")
    public ResponseEntity<List<QuizAccess>> getQuizAccess(@PathVariable Long quizId) {
        return ResponseEntity.ok(adminService.getQuizAccessList(quizId));
    }

    @GetMapping("/quiz/all")
    public ResponseEntity<List<Quiz>> getAllQuizzes() {
        return ResponseEntity.ok(adminService.getAllQuizzes());
    }
}