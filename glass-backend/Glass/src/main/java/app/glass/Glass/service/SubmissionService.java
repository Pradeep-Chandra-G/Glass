package app.glass.Glass.service;

import app.glass.Glass.model.*;
import app.glass.Glass.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SubmissionService {

    private final QuestionRepository questionRepo;
    private final SubmissionRepository submissionRepo;
    private final QuizAttemptRepository attemptRepo;

    public SubmissionService(
            QuestionRepository questionRepo,
            SubmissionRepository submissionRepo,
            QuizAttemptRepository attemptRepo) {
        this.questionRepo = questionRepo;
        this.submissionRepo = submissionRepo;
        this.attemptRepo = attemptRepo;
    }

    @Transactional
    public Submission submitAnswer(
            Long attemptId,
            Long questionId,
            List<Long> selectedOptionIds) {

        QuizAttempt attempt = attemptRepo.findById(attemptId).orElseThrow();

        // Validate attempt is active
        if (attempt.getStatus() != QuizAttempt.AttemptStatus.IN_PROGRESS) {
            throw new RuntimeException("Quiz attempt is not active");
        }

        // Check if expired
        if (LocalDateTime.now().isAfter(attempt.getExpiresAt())) {
            throw new RuntimeException("Quiz time has expired");
        }

        Question question = questionRepo.findById(questionId).orElseThrow();

        // Validate question belongs to quiz
        if (!question.getQuiz().getId().equals(attempt.getQuiz().getId())) {
            throw new RuntimeException("Question does not belong to this quiz");
        }

        // Check correct answer
        List<Long> correctIds = question.getOptions().stream()
                .filter(Option::isCorrect)
                .map(Option::getId)
                .sorted()
                .toList();

        boolean isCorrect =
                correctIds.equals(selectedOptionIds.stream().sorted().toList());

        // Create or update submission
        Submission submission = new Submission();
        submission.setQuizAttempt(attempt);
        submission.setQuestion(question);
        submission.setSelectedOptionIds(selectedOptionIds);
        submission.setCorrect(isCorrect);
        submission.setSubmittedAt(LocalDateTime.now());

        return submissionRepo.save(submission);
    }

    public List<Submission> getAttemptSubmissions(Long attemptId) {
        QuizAttempt attempt = attemptRepo.findById(attemptId).orElseThrow();
        return attempt.getSubmissions();
    }
}