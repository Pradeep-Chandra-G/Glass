package app.glass.Glass.service;

import app.glass.Glass.dto.CreateQuizRequest;
import app.glass.Glass.model.*;
import app.glass.Glass.repository.*;
import app.glass.Glass.security.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminService {

    private final QuizRepository quizRepo;
    private final QuestionRepository questionRepo;
    private final OptionRepository optionRepo;
    private final QuizAccessRepository accessRepo;
    private final UserRepository userRepo;
    private final JwtUtil jwtUtil;

    public AdminService(
            QuizRepository quizRepo,
            QuestionRepository questionRepo,
            OptionRepository optionRepo,
            QuizAccessRepository accessRepo,
            UserRepository userRepo,
            JwtUtil jwtUtil) {
        this.quizRepo = quizRepo;
        this.questionRepo = questionRepo;
        this.optionRepo = optionRepo;
        this.accessRepo = accessRepo;
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public Quiz createQuiz(CreateQuizRequest request, String token) {
        Long userId = jwtUtil.extractUserId(token.substring(7));
        User creator = userRepo.findById(userId).orElseThrow();

        Quiz quiz = new Quiz();
        quiz.setTitle(request.getTitle());
        quiz.setDurationSeconds(request.getDurationSeconds());
        quiz.setShowSolutions(request.getShowSolutions());
        quiz.setAllowMultipleAttempts(request.getAllowMultipleAttempts());
        quiz.setPublishType(request.getPublishType());
        quiz.setCreatedBy(creator);
        quiz.setStatus(Quiz.QuizStatus.DRAFT);

        return quizRepo.save(quiz);
    }

    @Transactional
    public Quiz publishQuiz(Long quizId) {
        Quiz quiz = quizRepo.findById(quizId).orElseThrow();

        if (quiz.getQuestions().isEmpty()) {
            throw new RuntimeException("Cannot publish quiz without questions");
        }

        quiz.setStatus(Quiz.QuizStatus.PUBLISHED);
        quiz.setPublishedAt(LocalDateTime.now());

        return quizRepo.save(quiz);
    }

    @Transactional
    public Quiz archiveQuiz(Long quizId) {
        Quiz quiz = quizRepo.findById(quizId).orElseThrow();
        quiz.setStatus(Quiz.QuizStatus.ARCHIVED);
        return quizRepo.save(quiz);
    }

    @Transactional
    public QuizAccess grantQuizAccess(Long quizId, Long userId, String token) {
        Long adminId = jwtUtil.extractUserId(token.substring(7));
        User admin = userRepo.findById(adminId).orElseThrow();
        User user = userRepo.findById(userId).orElseThrow();
        Quiz quiz = quizRepo.findById(quizId).orElseThrow();

        // Check if access already exists
        if (accessRepo.existsByUserIdAndQuizId(userId, quizId)) {
            throw new RuntimeException("User already has access");
        }

        QuizAccess access = new QuizAccess();
        access.setUser(user);
        access.setQuiz(quiz);
        access.setGrantedBy(admin);

        return accessRepo.save(access);
    }

    public List<QuizAccess> getQuizAccessList(Long quizId) {
        return accessRepo.findByQuizId(quizId);
    }

    public List<Quiz> getAllQuizzes() {
        return quizRepo.findAll();
    }
}