package app.quiz.glass.services;

import app.quiz.glass.dto.timer.TimerUpdateDTO;
import app.quiz.glass.entities.AttemptExpiredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@EnableAsync
@RequiredArgsConstructor
public class TimerService {

    private final ApplicationEventPublisher eventPublisher;
    private final SimpMessagingTemplate messagingTemplate;

    private final Map<Long, LocalDateTime> activeTimers = new ConcurrentHashMap<>();

    public void startTimer(Long attemptId, LocalDateTime expiryTime) {
        activeTimers.put(attemptId, expiryTime);
    }

    public void stopTimer(Long attemptId) {
        activeTimers.remove(attemptId);
    }

    @Scheduled(fixedDelay = 1000)
    public void broadcastTimerUpdates() {
        LocalDateTime now = LocalDateTime.now();

        activeTimers.forEach((attemptId, expiryTime) -> {
            long remaining = Duration.between(now, expiryTime).getSeconds();

            if (remaining <= 0) {
                stopTimer(attemptId);

                // ✔ Publish event (NO dependency on QuizAttemptService)
                eventPublisher.publishEvent(new AttemptExpiredEvent(attemptId));

                // WebSocket notify
                messagingTemplate.convertAndSend("/topic/timer/" + attemptId,
                        TimerUpdateDTO.builder()
                                .attemptId(attemptId)
                                .remainingSeconds(0L)
                                .expired(true)
                                .serverTime(now)
                                .build());

            } else {
                messagingTemplate.convertAndSend("/topic/timer/" + attemptId,
                        TimerUpdateDTO.builder()
                                .attemptId(attemptId)
                                .remainingSeconds(remaining)
                                .serverTime(now)
                                .build());
            }
        });
    }
}
