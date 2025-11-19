package app.quiz.glass.controllers;

import app.quiz.glass.dto.answer.SubmitAnswerRequest;
import app.quiz.glass.dto.answer.SubmitAnswerResponse;
import app.quiz.glass.dto.question.QuestionDTO;
import app.quiz.glass.dto.quiz.StartQuizResponse;
import app.quiz.glass.dto.result.QuizResultDTO;
import app.quiz.glass.entities.User;
import app.quiz.glass.security.CustomUserDetailsService;
import app.quiz.glass.services.QuizAttemptService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/attempts")
@RequiredArgsConstructor
public class QuizAttemptController {

    private final QuizAttemptService attemptService;
    private final CustomUserDetailsService userDetailsService;

    @PostMapping("/start/{quizId}")
    public ResponseEntity<StartQuizResponse> startQuiz(
            @PathVariable Long quizId,
            Principal principal) {

        User user = userDetailsService.getCurrentUser(principal.getName());
        return ResponseEntity.ok(attemptService.startQuiz(quizId, user));
    }

    @GetMapping("/{attemptId}/questions/{questionIndex}")
    public ResponseEntity<QuestionDTO> getQuestion(
            @PathVariable Long attemptId,
            @PathVariable int questionIndex,
            Principal principal) {

        User user = userDetailsService.getCurrentUser(principal.getName());
        return ResponseEntity.ok(attemptService.getQuestion(attemptId, questionIndex, user.getId()));
    }

    @PostMapping("/answer")
    public ResponseEntity<SubmitAnswerResponse> submitAnswer(
            @Valid @RequestBody SubmitAnswerRequest request,
            Principal principal) {

        User user = userDetailsService.getCurrentUser(principal.getName());
        return ResponseEntity.ok(attemptService.submitAnswer(request, user.getId()));
    }

    @PostMapping("/{attemptId}/submit")
    public ResponseEntity<QuizResultDTO> submitQuiz(
            @PathVariable Long attemptId,
            Principal principal) {

        User user = userDetailsService.getCurrentUser(principal.getName());
        return ResponseEntity.ok(attemptService.submitQuiz(attemptId, user.getId()));
    }

    @GetMapping("/{attemptId}/result")
    public ResponseEntity<QuizResultDTO> getResult(
            @PathVariable Long attemptId,
            Principal principal) {

        User user = userDetailsService.getCurrentUser(principal.getName());
        return ResponseEntity.ok(attemptService.getQuizResult(attemptId, user.getId()));
    }
}
