package app.glass.Glass.controller;

import app.glass.Glass.dto.SubmitAnswerRequest;
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
    public Submission submit(@RequestBody SubmitAnswerRequest request) {
        return submissionService.submitAnswer(
                request.getAttemptId(),
                request.getQuestionId(),
                request.getSelectedOptionIds()
        );
    }

    @GetMapping("/attempt/{attemptId}")
    public List<Submission> getAttemptSubmissions(@PathVariable Long attemptId) {
        return submissionService.getAttemptSubmissions(attemptId);
    }
}