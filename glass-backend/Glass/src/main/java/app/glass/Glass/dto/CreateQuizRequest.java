package app.glass.Glass.dto;

import app.glass.Glass.model.Quiz;

public class CreateQuizRequest {
    private String title;
    private Integer durationSeconds;
    private Boolean showSolutions = false;
    private Boolean allowMultipleAttempts = false;
    private Quiz.PublishType publishType = Quiz.PublishType.PUBLIC;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public Boolean getShowSolutions() {
        return showSolutions;
    }

    public void setShowSolutions(Boolean showSolutions) {
        this.showSolutions = showSolutions;
    }

    public Boolean getAllowMultipleAttempts() {
        return allowMultipleAttempts;
    }

    public void setAllowMultipleAttempts(Boolean allowMultipleAttempts) {
        this.allowMultipleAttempts = allowMultipleAttempts;
    }

    public Quiz.PublishType getPublishType() {
        return publishType;
    }

    public void setPublishType(Quiz.PublishType publishType) {
        this.publishType = publishType;
    }
}