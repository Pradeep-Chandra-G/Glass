package app.quiz.glass.dto.leaderboard;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LeaderboardDTO {
    private Long quizId;
    private String quizTitle;
    private List<LeaderboardEntryDTO> entries;
    private Integer totalParticipants;
}
