package app.glass.Glass.controller;

import app.glass.Glass.model.QuizAttempt;
import app.glass.Glass.security.JwtUtil;
import app.glass.Glass.service.QuizAttemptService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attempt")
public class QuizAttemptController {

    private final QuizAttemptService attemptService;
    private final JwtUtil jwtUtil;

    public QuizAttemptController(QuizAttemptService attemptService, JwtUtil jwtUtil) {
        this.attemptService = attemptService;
        this.jwtUtil = jwtUtil;
    }

    private Long getUserIdFromToken(String token) {
        return jwtUtil.extractUserId(token.substring(7));
    }

    @PostMapping("/start/{quizId}")
    public ResponseEntity<QuizAttempt> startAttempt(
            @PathVariable Long quizId,
            @RequestHeader("Authorization") String token) {

        Long userId = getUserIdFromToken(token);
        QuizAttempt attempt = attemptService.startQuizAttempt(userId, quizId);
        return ResponseEntity.ok(attempt);
    }

    @PostMapping("/complete/{attemptId}")
    public ResponseEntity<QuizAttempt> completeAttempt(
            @PathVariable Long attemptId) {

        QuizAttempt attempt = attemptService.completeAttempt(attemptId);
        return ResponseEntity.ok(attempt);
    }

    @GetMapping("/{attemptId}")
    public ResponseEntity<QuizAttempt> getAttempt(@PathVariable Long attemptId) {
        return ResponseEntity.ok(attemptService.getAttempt(attemptId));
    }

    @GetMapping("/user/all")
    public ResponseEntity<List<QuizAttempt>> getUserAttempts(
            @RequestHeader("Authorization") String token) {

        Long userId = getUserIdFromToken(token);
        return ResponseEntity.ok(attemptService.getUserAttempts(userId));
    }

    @GetMapping("/user/quiz/{quizId}")
    public ResponseEntity<List<QuizAttempt>> getUserQuizAttempts(
            @PathVariable Long quizId,
            @RequestHeader("Authorization") String token) {

        Long userId = getUserIdFromToken(token);
        return ResponseEntity.ok(
                attemptService.getUserQuizAttempts(userId, quizId)
        );
    }

    @GetMapping("/current/{quizId}")
    public ResponseEntity<QuizAttempt> getCurrentAttempt(
            @PathVariable Long quizId,
            @RequestHeader("Authorization") String token) {

        Long userId = getUserIdFromToken(token);
        QuizAttempt attempt = attemptService.getCurrentAttempt(userId, quizId);

        if (attempt == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(attempt);
    }
}