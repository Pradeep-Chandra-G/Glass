package app.glass.Glass.controller;

import app.glass.Glass.model.Quiz;
import app.glass.Glass.service.QuizService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/api/quiz")
public class QuizController {
    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping("/all")
    public List<Quiz> getAll() {
        return quizService.getAllQuizzes();
    }

    @GetMapping("/{id}")
    public Quiz getQuiz(@PathVariable Long id) {
        return quizService.getQuiz(id);
    }

    @GetMapping("/time")
    public LocalDateTime getServerTime() {
        return LocalDateTime.now();
    }
}