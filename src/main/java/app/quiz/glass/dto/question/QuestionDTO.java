package app.quiz.glass.dto.question;

import app.quiz.glass.entities.QuestionType;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class QuestionDTO {
    private Long id;
    private Long quizId;
    private QuestionType type;
    private String questionText;
    private Integer points;
    private Integer orderIndex;
    private List<QuestionOptionDTO> options;
    private String explanation; // Only shown after submission
}
