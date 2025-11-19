package app.quiz.glass.dto.answer;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmitAnswerRequest {
    @NotNull
    private Long attemptId;

    @NotNull
    private Long questionId;

    private Long selectedOptionId; // For MCQ and TRUE_FALSE

    private Double numericalAnswer; // For NUMERICAL
}
