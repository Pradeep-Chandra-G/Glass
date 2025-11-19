package app.quiz.glass.dto.analytics;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuestionAnalyticsDTO {
    private Long questionId;
    private String questionText;
    private Integer totalAttempts;
    private Integer correctAttempts;
    private Double successRate;
    private Integer averagePoints;
}
