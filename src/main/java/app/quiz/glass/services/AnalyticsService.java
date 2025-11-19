package app.quiz.glass.services;

import app.quiz.glass.dto.analytics.QuestionAnalyticsDTO;
import app.quiz.glass.dto.analytics.QuizAnalyticsDTO;
import app.quiz.glass.dto.leaderboard.LeaderboardDTO;
import app.quiz.glass.dto.leaderboard.LeaderboardEntryDTO;
import app.quiz.glass.entities.*;
import app.quiz.glass.repositories.AnswerRepository;
import app.quiz.glass.repositories.QuestionRepository;
import app.quiz.glass.repositories.QuizAttemptRepository;
import app.quiz.glass.repositories.QuizRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final QuizRepository quizRepository;
    private final QuizAttemptRepository attemptRepository;
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;

    @Transactional(readOnly = true)
    public LeaderboardDTO getLeaderboard(Long quizId, int limit) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        Pageable pageable = PageRequest.of(0, limit);
        List<QuizAttempt> topAttempts = attemptRepository
                .findLeaderboard(quizId, pageable);

        List<LeaderboardEntryDTO> entries = new ArrayList<>();
        int rank = 1;

        for (QuizAttempt attempt : topAttempts) {
            double percentage = attempt.getTotalPoints() > 0 ?
                    (attempt.getScore() * 100.0 / attempt.getTotalPoints()) : 0;

            entries.add(LeaderboardEntryDTO.builder()
                    .attemptId(attempt.getId())
                    .userName(attempt.getUser().getFullName())
                    .score(attempt.getScore())
                    .totalPoints(attempt.getTotalPoints())
                    .percentage(percentage)
                    .submittedAt(attempt.getSubmittedAt())
                    .rank(rank++)
                    .build());
        }

        long totalParticipants = attemptRepository
                .countByQuizIdAndStatus(quizId, AttemptStatus.SUBMITTED) +
                attemptRepository.countByQuizIdAndStatus(quizId, AttemptStatus.AUTO_SUBMITTED);

        return LeaderboardDTO.builder()
                .quizId(quizId)
                .quizTitle(quiz.getTitle())
                .entries(entries)
                .totalParticipants((int) totalParticipants)
                .build();
    }

    @Transactional(readOnly = true)
    public QuizAnalyticsDTO getQuizAnalytics(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        List<QuizAttempt> allAttempts = attemptRepository
                .findByQuizId(quizId, PageRequest.of(0, Integer.MAX_VALUE))
                .getContent();

        List<QuizAttempt> submittedAttempts = allAttempts.stream()
                .filter(a -> a.getStatus() == AttemptStatus.SUBMITTED ||
                        a.getStatus() == AttemptStatus.AUTO_SUBMITTED)
                .collect(Collectors.toList());

        List<QuizAttempt> inProgressAttempts = allAttempts.stream()
                .filter(a -> a.getStatus() == AttemptStatus.IN_PROGRESS)
                .collect(Collectors.toList());

        // Calculate statistics
        OptionalDouble avgScore = submittedAttempts.stream()
                .mapToInt(QuizAttempt::getScore)
                .average();

        OptionalInt maxScore = submittedAttempts.stream()
                .mapToInt(QuizAttempt::getScore)
                .max();

        OptionalInt minScore = submittedAttempts.stream()
                .mapToInt(QuizAttempt::getScore)
                .min();

        long passedCount = submittedAttempts.stream()
                .filter(a -> {
                    double percentage = a.getTotalPoints() > 0 ?
                            (a.getScore() * 100.0 / a.getTotalPoints()) : 0;
                    return percentage >= quiz.getPassingScore();
                })
                .count();

        double passRate = submittedAttempts.isEmpty() ? 0 :
                (passedCount * 100.0 / submittedAttempts.size());

        // Question-level analytics
        List<QuestionAnalyticsDTO> questionAnalytics =
                calculateQuestionAnalytics(quizId, submittedAttempts);

        return QuizAnalyticsDTO.builder()
                .quizId(quizId)
                .quizTitle(quiz.getTitle())
                .totalAttempts(allAttempts.size())
                .submittedAttempts(submittedAttempts.size())
                .inProgressAttempts(inProgressAttempts.size())
                .averageScore(avgScore.orElse(0.0))
                .passRate(passRate)
                .highestScore(maxScore.orElse(0))
                .lowestScore(minScore.orElse(0))
                .questionAnalytics(questionAnalytics)
                .build();
    }

    private List<QuestionAnalyticsDTO> calculateQuestionAnalytics(
            Long quizId, List<QuizAttempt> submittedAttempts) {

        List<Question> questions = questionRepository
                .findByQuizIdOrderByOrderIndexAsc(quizId);

        List<QuestionAnalyticsDTO> analytics = new ArrayList<>();

        for (Question question : questions) {
            List<Answer> questionAnswers = submittedAttempts.stream()
                    .flatMap(attempt -> answerRepository.findByAttemptId(attempt.getId()).stream())
                    .filter(answer -> answer.getQuestion().getId().equals(question.getId()))
                    .collect(Collectors.toList());

            long correctCount = questionAnswers.stream()
                    .filter(Answer::getIsCorrect)
                    .count();

            double successRate = questionAnswers.isEmpty() ? 0 :
                    (correctCount * 100.0 / questionAnswers.size());

            OptionalDouble avgPoints = questionAnswers.stream()
                    .mapToInt(Answer::getPointsEarned)
                    .average();

            analytics.add(QuestionAnalyticsDTO.builder()
                    .questionId(question.getId())
                    .questionText(question.getQuestionText())
                    .totalAttempts(questionAnswers.size())
                    .correctAttempts((int) correctCount)
                    .successRate(successRate)
                    .averagePoints((int) avgPoints.orElse(0))
                    .build());
        }

        return analytics;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getUserStatistics(Long userId) {
        List<QuizAttempt> userAttempts = attemptRepository
                .findByUserId(userId, PageRequest.of(0, Integer.MAX_VALUE))
                .getContent();

        List<QuizAttempt> completed = userAttempts.stream()
                .filter(a -> a.getStatus() == AttemptStatus.SUBMITTED ||
                        a.getStatus() == AttemptStatus.AUTO_SUBMITTED)
                .collect(Collectors.toList());

        OptionalDouble avgScore = completed.stream()
                .filter(a -> a.getTotalPoints() != null && a.getTotalPoints() > 0)
                .mapToDouble(a -> (a.getScore() * 100.0 / a.getTotalPoints()))
                .average();

        long totalQuizzesCompleted = completed.size();
        long totalQuizzesInProgress = userAttempts.stream()
                .filter(a -> a.getStatus() == AttemptStatus.IN_PROGRESS)
                .count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("userId", userId);
        stats.put("totalQuizzesCompleted", totalQuizzesCompleted);
        stats.put("totalQuizzesInProgress", totalQuizzesInProgress);
        stats.put("averageScorePercentage", avgScore.orElse(0.0));
        stats.put("recentAttempts", completed.stream()
                .limit(5)
                .map(this::toAttemptSummary)
                .collect(Collectors.toList()));

        return stats;
    }

    private Map<String, Object> toAttemptSummary(QuizAttempt attempt) {
        double percentage = attempt.getTotalPoints() > 0 ?
                (attempt.getScore() * 100.0 / attempt.getTotalPoints()) : 0;

        Map<String, Object> summary = new HashMap<>();
        summary.put("quizTitle", attempt.getQuiz().getTitle());
        summary.put("score", attempt.getScore());
        summary.put("totalPoints", attempt.getTotalPoints());
        summary.put("percentage", percentage);
        summary.put("submittedAt", attempt.getSubmittedAt());
        summary.put("status", attempt.getStatus());

        return summary;
    }
}
