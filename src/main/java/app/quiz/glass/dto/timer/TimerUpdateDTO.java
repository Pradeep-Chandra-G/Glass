package app.quiz.glass.dto.timer;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TimerUpdateDTO {
    private Long attemptId;
    private Long remainingSeconds;
    private LocalDateTime serverTime;
    private Boolean expired = false;
}
