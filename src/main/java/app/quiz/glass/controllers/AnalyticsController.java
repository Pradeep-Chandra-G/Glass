package app.quiz.glass.controllers;

import app.quiz.glass.dto.analytics.QuizAnalyticsDTO;
import app.quiz.glass.dto.leaderboard.LeaderboardDTO;
import app.quiz.glass.entities.User;
import app.quiz.glass.security.CustomUserDetailsService;
import app.quiz.glass.services.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final CustomUserDetailsService userDetailsService;

    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<QuizAnalyticsDTO> getQuizAnalytics(@PathVariable Long quizId) {
        return ResponseEntity.ok(analyticsService.getQuizAnalytics(quizId));
    }

    @GetMapping("/quiz/{quizId}/leaderboard")
    public ResponseEntity<LeaderboardDTO> getLeaderboard(
            @PathVariable Long quizId,
            @RequestParam(defaultValue = "10") int limit) {

        return ResponseEntity.ok(analyticsService.getLeaderboard(quizId, limit));
    }

    @GetMapping("/user/stats")
    public ResponseEntity<Map<String, Object>> getUserStatistics(Principal principal) {
        User user = userDetailsService.getCurrentUser(principal.getName());
        return ResponseEntity.ok(analyticsService.getUserStatistics(user.getId()));
    }
}

