package app.quiz.glass.dto.answer;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AnswerDTO {
    private Long id;
    private Long questionId;
    private Long selectedOptionId;
    private Double numericalAnswer;
    private Boolean isCorrect;
    private Integer pointsEarned;
    private LocalDateTime answeredAt;
}
