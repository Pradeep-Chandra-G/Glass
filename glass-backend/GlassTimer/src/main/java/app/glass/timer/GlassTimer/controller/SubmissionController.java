package app.glass.timer.GlassTimer.controller;

import app.glass.timer.GlassTimer.model.Submission;
import app.glass.timer.GlassTimer.service.SubmissionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/submission")
public class SubmissionController {

    private final SubmissionService submissionService;

    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @PostMapping("/submit")
    public Submission submit(
            @RequestParam Long quizId,
            @RequestParam Long questionId,
            @RequestBody List<Long> selectedOptionIds
    ) {
        return submissionService.submitAnswer(
                quizId,
                questionId,
                selectedOptionIds
        );
    }
}
