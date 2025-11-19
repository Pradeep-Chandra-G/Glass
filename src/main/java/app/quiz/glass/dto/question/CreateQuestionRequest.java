package app.quiz.glass.dto.question;

import app.quiz.glass.entities.QuestionType;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateQuestionRequest {
    @NotNull
    private QuestionType type;

    @NotNull
    private String questionText;

    private String explanation;

    @Min(1)
    private Integer points = 1;

    private List<CreateOptionRequest> options = new ArrayList<>();
}
