package app.quiz.glass.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AttemptAutoSubmitService {

    private final QuizAttemptService attemptService;

    public void autoSubmit(Long attemptId) {
        attemptService.autoSubmitAttempt(attemptId);
    }
}

