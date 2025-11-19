package app.quiz.glass.dto.question;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuestionOptionDTO {
    private Long id;
    private String optionText;
    private Integer orderIndex;
    private Boolean isCorrect; // Only shown after submission
}
