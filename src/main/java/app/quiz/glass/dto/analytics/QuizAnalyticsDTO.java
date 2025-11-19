package app.quiz.glass.dto.analytics;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class QuizAnalyticsDTO {
    private Long quizId;
    private String quizTitle;
    private Integer totalAttempts;
    private Integer submittedAttempts;
    private Integer inProgressAttempts;
    private Double averageScore;
    private Double passRate;
    private Integer highestScore;
    private Integer lowestScore;
    private List<QuestionAnalyticsDTO> questionAnalytics;
}
