package app.glass.Glass.service;

import app.glass.Glass.model.Question;
import app.glass.Glass.model.Submission;
import app.glass.Glass.repository.QuestionRepository;
import app.glass.Glass.repository.SubmissionRepository;
import org.springframework.stereotype.Service;
import app.glass.Glass.model.Option;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SubmissionService {

    private final QuestionRepository questionRepo;
    private final SubmissionRepository submissionRepo;

    public SubmissionService(
            QuestionRepository questionRepo,
            SubmissionRepository submissionRepo) {
        this.questionRepo = questionRepo;
        this.submissionRepo = submissionRepo;
    }

    public Submission submitAnswer(
            Long quizId,
            Long questionId,
            List<Long> selectedOptionIds) {

        Question question = questionRepo.findById(questionId).orElseThrow();

        List<Long> correctIds = question.getOptions().stream()
                .filter(Option::isCorrect)
                .map(Option::getId)
                .sorted()
                .toList();

        boolean isCorrect =
                correctIds.equals(selectedOptionIds.stream().sorted().toList());

        Submission submission = new Submission();
        submission.setQuizId(quizId);
        submission.setQuestionId(questionId);
        submission.setSelectedOptionIds(selectedOptionIds);
        submission.setCorrect(isCorrect);
        submission.setSubmittedAt(LocalDateTime.now());

        return submissionRepo.save(submission);
    }
}
