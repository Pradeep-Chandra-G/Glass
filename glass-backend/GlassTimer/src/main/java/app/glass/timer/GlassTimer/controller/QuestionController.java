package app.glass.timer.GlassTimer.controller;

import app.glass.timer.GlassTimer.model.Question;
import app.glass.timer.GlassTimer.service.QuestionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/question")
public class QuestionController {
    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("/quiz/{quizId}")
    public List<Question> getQuestions(@PathVariable Long quizId) {
        return questionService.getQuestionsByQuiz(quizId);
    }

    @GetMapping("/{id}")
    public Question getQuestion(@PathVariable Long id) {
        return questionService.getQuestion(id);
    }
}
