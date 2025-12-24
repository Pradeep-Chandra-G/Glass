package app.glass.timer.GlassTimer.service;

import app.glass.timer.GlassTimer.model.Question;
import app.glass.timer.GlassTimer.repository.QuestionRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class QuestionService {
    private final QuestionRepository questionRepo;

    public QuestionService(QuestionRepository questionRepo) {
        this.questionRepo = questionRepo;
    }

    public List<Question> getQuestionsByQuiz(Long quizId) {
        return questionRepo.findAll().stream()
                .filter(q -> q.getQuiz().getId().equals(quizId))
                .toList();
    }

    public Question getQuestion(Long id) {
        return questionRepo.findById(id).orElse(null);
    }
}