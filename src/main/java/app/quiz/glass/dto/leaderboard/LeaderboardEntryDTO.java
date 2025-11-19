package app.quiz.glass.dto.leaderboard;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LeaderboardEntryDTO {
    private Long attemptId;
    private String userName;
    private Integer score;
    private Integer totalPoints;
    private Double percentage;
    private LocalDateTime submittedAt;
    private Integer rank;
}
