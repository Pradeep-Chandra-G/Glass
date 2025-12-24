package app.glass.Glass.service;

import app.glass.Glass.model.Question;
import app.glass.Glass.repository.QuestionRepository;
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