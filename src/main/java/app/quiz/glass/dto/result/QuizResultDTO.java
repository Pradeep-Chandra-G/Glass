package app.quiz.glass.dto.result;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class QuizResultDTO {
    private Long attemptId;
    private String quizTitle;
    private Integer score;
    private Integer totalPoints;
    private Double percentage;
    private Boolean passed;
    private LocalDateTime submittedAt;
    private Integer correctAnswers;
    private Integer totalQuestions;
    private List<QuestionResultDTO> questionResults;
}
