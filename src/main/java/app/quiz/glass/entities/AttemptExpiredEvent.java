package app.quiz.glass.entities;

public class AttemptExpiredEvent {
    private final Long attemptId;

    public AttemptExpiredEvent(Long attemptId) {
        this.attemptId = attemptId;
    }

    public Long getAttemptId() {
        return attemptId;
    }
}

