package app.glass.Glass.dto;// SubmitAnswerRequest DTO

import java.util.List;

public class SubmitAnswerRequest {
    private Long attemptId;
    private Long questionId;
    private List<Long> selectedOptionIds;

    public Long getAttemptId() { return attemptId; }
    public void setAttemptId(Long attemptId) { this.attemptId = attemptId; }
    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }
    public List<Long> getSelectedOptionIds() { return selectedOptionIds; }
    public void setSelectedOptionIds(List<Long> selectedOptionIds) {
        this.selectedOptionIds = selectedOptionIds;
    }
}
