package app.glass.timer.GlassTimer.service;

import app.glass.timer.GlassTimer.model.Quiz;
import app.glass.timer.GlassTimer.repository.QuizRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class QuizService {
    private final QuizRepository quizRepo;

    public QuizService(QuizRepository quizRepo) {
        this.quizRepo = quizRepo;
    }

    public List<Quiz> getAllQuizzes() {
        return quizRepo.findAll();
    }

    public Quiz getQuiz(Long id) {
        return quizRepo.findById(id).orElse(null);
    }
}