package app.quiz.glass.dto.answer;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubmitAnswerResponse {
    private Boolean saved;
    private String message;
    private Integer answeredCount;
    private Integer totalQuestions;
}
