package app.glass.Glass.service;

import app.glass.Glass.model.*;
import app.glass.Glass.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class QuizAttemptService {

    private final QuizAttemptRepository attemptRepo;
    private final QuizRepository quizRepo;
    private final UserRepository userRepo;
    private final QuizAccessRepository accessRepo;

    public QuizAttemptService(
            QuizAttemptRepository attemptRepo,
            QuizRepository quizRepo,
            UserRepository userRepo,
            QuizAccessRepository accessRepo) {
        this.attemptRepo = attemptRepo;
        this.quizRepo = quizRepo;
        this.userRepo = userRepo;
        this.accessRepo = accessRepo;
    }

    @Transactional
    public QuizAttempt startQuizAttempt(Long userId, Long quizId) {
        User user = userRepo.findById(userId).orElseThrow();
        Quiz quiz = quizRepo.findById(quizId).orElseThrow();

        // Check quiz is published
        if (quiz.getStatus() != Quiz.QuizStatus.PUBLISHED) {
            throw new RuntimeException("Quiz is not published");
        }

        // Check access for restricted quizzes
        if (quiz.getPublishType() == Quiz.PublishType.RESTRICTED) {
            if (!accessRepo.existsByUserIdAndQuizId(userId, quizId)) {
                throw new RuntimeException("User does not have access to this quiz");
            }
        }

        // Check for existing in-progress attempt
        var existingAttempt = attemptRepo.findByUserIdAndQuizIdAndStatus(
                userId, quizId, QuizAttempt.AttemptStatus.IN_PROGRESS
        );
        if (existingAttempt.isPresent()) {
            return existingAttempt.get();
        }

        // Check multiple attempts
        if (!quiz.getAllowMultipleAttempts()) {
            long completedCount = attemptRepo.countCompletedAttempts(userId, quizId);
            if (completedCount > 0) {
                throw new RuntimeException("Multiple attempts not allowed for this quiz");
            }
        }

        // Create new attempt
        QuizAttempt attempt = new QuizAttempt();
        attempt.setUser(user);
        attempt.setQuiz(quiz);
        attempt.setStartedAt(LocalDateTime.now());
        attempt.setExpiresAt(
                LocalDateTime.now().plusSeconds(quiz.getDurationSeconds())
        );
        attempt.setTotalQuestions(quiz.getQuestions().size());
        attempt.setStatus(QuizAttempt.AttemptStatus.IN_PROGRESS);

        return attemptRepo.save(attempt);
    }

    @Transactional
    public QuizAttempt completeAttempt(Long attemptId) {
        QuizAttempt attempt = attemptRepo.findById(attemptId).orElseThrow();

        if (attempt.getStatus() != QuizAttempt.AttemptStatus.IN_PROGRESS) {
            throw new RuntimeException("Attempt is not in progress");
        }

        attempt.setCompletedAt(LocalDateTime.now());

        // Check if timeout
        if (LocalDateTime.now().isAfter(attempt.getExpiresAt())) {
            attempt.setStatus(QuizAttempt.AttemptStatus.TIMEOUT);
        } else {
            attempt.setStatus(QuizAttempt.AttemptStatus.COMPLETED);
        }

        // Calculate score
        calculateScore(attempt);

        return attemptRepo.save(attempt);
    }

    private void calculateScore(QuizAttempt attempt) {
        long correctCount = attempt.getSubmissions().stream()
                .filter(Submission::isCorrect)
                .count();

        attempt.setCorrectAnswers((int) correctCount);

        // Simple percentage score
        int score = (int) ((correctCount * 100.0) / attempt.getTotalQuestions());
        attempt.setScore(score);
    }

    public QuizAttempt getAttempt(Long attemptId) {
        return attemptRepo.findById(attemptId).orElseThrow();
    }

    public List<QuizAttempt> getUserAttempts(Long userId) {
        return attemptRepo.findByUserId(userId);
    }

    public List<QuizAttempt> getUserQuizAttempts(Long userId, Long quizId) {
        return attemptRepo.findByUserIdAndQuizId(userId, quizId);
    }

    public QuizAttempt getCurrentAttempt(Long userId, Long quizId) {
        return attemptRepo.findByUserIdAndQuizIdAndStatus(
                userId, quizId, QuizAttempt.AttemptStatus.IN_PROGRESS
        ).orElse(null);
    }
}