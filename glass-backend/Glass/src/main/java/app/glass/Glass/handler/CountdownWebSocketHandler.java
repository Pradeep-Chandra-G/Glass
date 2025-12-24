package app.glass.Glass.handler;

import app.glass.Glass.config.QuizTimerProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CountdownWebSocketHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions =
            ConcurrentHashMap.newKeySet();

    private final QuizTimerProperties properties;

    private Instant endTime;

    public CountdownWebSocketHandler(QuizTimerProperties properties) {
        this.properties = properties;
        this.endTime = Instant.now()
                .plusSeconds(properties.getDurationSeconds());
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(
            WebSocketSession session,
            CloseStatus status
    ) {
        sessions.remove(session);
    }

    @Scheduled(fixedRate = 1000)
    public void broadcastRemainingTime() {
        long remainingSeconds =
                Duration.between(Instant.now(), endTime).getSeconds();

        if (remainingSeconds < 0) {
            remainingSeconds = 0;
        }

        String payload = String.valueOf(remainingSeconds);

        for (WebSocketSession session : sessions) {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(payload));
                }
            } catch (Exception ignored) {}
        }
    }
}

