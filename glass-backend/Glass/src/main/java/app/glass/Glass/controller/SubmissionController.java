package app.glass.Glass.controller;

import app.glass.Glass.service.SubmissionService;
import app.glass.Glass.model.Submission;
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
