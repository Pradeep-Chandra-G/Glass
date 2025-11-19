package app.quiz.glass.dto.quiz;

import app.quiz.glass.dto.question.QuestionDTO;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class StartQuizResponse {
    private Long attemptId;
    private Long quizId;
    private String title;
    private Integer durationMinutes;
    private LocalDateTime startedAt;
    private LocalDateTime expiresAt;
    private Integer totalQuestions;
    private QuestionDTO firstQuestion;
}
