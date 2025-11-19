package app.quiz.glass.controllers;

import app.quiz.glass.dto.pagination.PageResponse;
import app.quiz.glass.dto.question.CreateQuestionRequest;
import app.quiz.glass.dto.question.QuestionDTO;
import app.quiz.glass.dto.quiz.CreateQuizRequest;
import app.quiz.glass.dto.quiz.QuizDTO;
import app.quiz.glass.entities.User;
import app.quiz.glass.security.CustomUserDetailsService;
import app.quiz.glass.services.QuestionService;
import app.quiz.glass.services.QuizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;
    private final QuestionService questionService;
    private final CustomUserDetailsService userDetailsService;

    @PostMapping
    public ResponseEntity<QuizDTO> createQuiz(
            @Valid @RequestBody CreateQuizRequest request,
            Principal principal) {

        User user = userDetailsService.getCurrentUser(principal.getName());
        var quiz = quizService.createQuiz(request, user);
        return ResponseEntity.ok(quizService.getQuizById(quiz.getId(), user.getId()));
    }

    @GetMapping
    public ResponseEntity<PageResponse<QuizDTO>> getActiveQuizzes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Principal principal) {

        User user = userDetailsService.getCurrentUser(principal.getName());
        return ResponseEntity.ok(quizService.getActiveQuizzes(page, size, user.getId()));
    }

    @GetMapping("/{quizId}")
    public ResponseEntity<QuizDTO> getQuiz(
            @PathVariable Long quizId,
            Principal principal) {

        User user = userDetailsService.getCurrentUser(principal.getName());
        return ResponseEntity.ok(quizService.getQuizById(quizId, user.getId()));
    }

    @PostMapping("/{quizId}/publish")
    public ResponseEntity<QuizDTO> publishQuiz(
            @PathVariable Long quizId,
            Principal principal) {

        User user = userDetailsService.getCurrentUser(principal.getName());
        var quiz = quizService.publishQuiz(quizId);
        return ResponseEntity.ok(quizService.getQuizById(quiz.getId(), user.getId()));
    }

    @PostMapping("/{quizId}/questions")
    public ResponseEntity<QuestionDTO> addQuestion(
            @PathVariable Long quizId,
            @Valid @RequestBody CreateQuestionRequest request) {

        var question = questionService.createQuestion(quizId, request);
        return ResponseEntity.ok(questionService.getQuizQuestions(quizId, false)
                .stream()
                .filter(q -> q.getId().equals(question.getId()))
                .findFirst()
                .orElseThrow());
    }

    @GetMapping("/{quizId}/questions")
    public ResponseEntity<?> getQuestions(
            @PathVariable Long quizId,
            @RequestParam(defaultValue = "false") boolean includeAnswers) {

        return ResponseEntity.ok(questionService.getQuizQuestions(quizId, includeAnswers));
    }
}
