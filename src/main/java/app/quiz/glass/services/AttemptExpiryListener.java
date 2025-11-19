package app.quiz.glass.services;

import app.quiz.glass.entities.AttemptExpiredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AttemptExpiryListener {

    private final QuizAttemptService quizAttemptService;

    @EventListener
    public void onAttemptExpired(AttemptExpiredEvent event) {
        quizAttemptService.autoSubmitAttempt(event.getAttemptId());
    }
}

