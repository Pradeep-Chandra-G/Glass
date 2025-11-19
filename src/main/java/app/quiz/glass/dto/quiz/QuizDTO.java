package app.quiz.glass.dto.quiz;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class QuizDTO {
    private Long id;
    private String title;
    private String description;
    private Integer durationMinutes;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean published;
    private Integer passingScore;
    private Integer totalQuestions;
    private Integer totalPoints;
    private Boolean isActive;
    private Boolean hasActiveAttempt;
}
