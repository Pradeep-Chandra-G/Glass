package app.quiz.glass.dto.result;

import app.quiz.glass.dto.answer.AnswerDTO;
import app.quiz.glass.dto.question.QuestionOptionDTO;
import app.quiz.glass.entities.QuestionType;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class QuestionResultDTO {
    private Long questionId;
    private String questionText;
    private QuestionType type;
    private String explanation;
    private Integer points;
    private Integer pointsEarned;
    private Boolean isCorrect;
    private AnswerDTO userAnswer;
    private List<QuestionOptionDTO> options;
}
