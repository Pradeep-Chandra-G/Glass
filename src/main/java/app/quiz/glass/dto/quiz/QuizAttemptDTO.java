package app.quiz.glass.dto.quiz;

import app.quiz.glass.entities.AttemptStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class QuizAttemptDTO {
    private Long id;
    private Long quizId;
    private String quizTitle;
    private AttemptStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime submittedAt;
    private LocalDateTime expiresAt;
    private Integer score;
    private Integer totalPoints;
    private Double percentage;
    private Integer currentQuestionIndex;
    private Integer totalQuestions;
    private Long remainingSeconds;
}
