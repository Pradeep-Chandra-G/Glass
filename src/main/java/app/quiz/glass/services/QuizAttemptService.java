package app.quiz.glass.services;

import app.quiz.glass.dto.answer.AnswerDTO;
import app.quiz.glass.dto.answer.SubmitAnswerRequest;
import app.quiz.glass.dto.answer.SubmitAnswerResponse;
import app.quiz.glass.dto.question.QuestionDTO;
import app.quiz.glass.dto.question.QuestionOptionDTO;
import app.quiz.glass.dto.quiz.StartQuizResponse;
import app.quiz.glass.dto.result.QuestionResultDTO;
import app.quiz.glass.dto.result.QuizResultDTO;
import app.quiz.glass.entities.*;
import app.quiz.glass.repositories.AnswerRepository;
import app.quiz.glass.repositories.QuestionRepository;
import app.quiz.glass.repositories.QuizAttemptRepository;
import app.quiz.glass.repositories.QuizRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuizAttemptService {

    private final QuizAttemptRepository attemptRepository;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final TimerService timerService;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public StartQuizResponse startQuiz(Long quizId, User user) {
        // Check for existing active attempt
        Optional<QuizAttempt> existingAttempt = attemptRepository
                .findActiveAttempt(user.getId(), quizId);

        if (existingAttempt.isPresent()) {
            throw new RuntimeException("You already have an active attempt for this quiz");
        }

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        if (!quiz.isActive()) {
            throw new RuntimeException("Quiz is not currently active");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMinutes(quiz.getDurationMinutes());

        // Ensure doesn't exceed quiz end time
        if (expiresAt.isAfter(quiz.getEndTime())) {
            expiresAt = quiz.getEndTime();
        }

        QuizAttempt attempt = QuizAttempt.builder()
                .user(user)
                .quiz(quiz)
                .status(AttemptStatus.IN_PROGRESS)
                .startedAt(now)
                .expiresAt(expiresAt)
                .currentQuestionIndex(0)
                .build();

        attempt = attemptRepository.save(attempt);

        // Start timer
        timerService.startTimer(attempt.getId(), expiresAt);

        // Get first question
        List<Question> questions = questionRepository.findByQuizIdWithOptions(quizId);
        QuestionDTO firstQuestion = questions.isEmpty() ? null :
                toQuestionDTO(questions.get(0), false);

        log.info("User {} started quiz {} with attempt {}",
                user.getId(), quizId, attempt.getId());

        return StartQuizResponse.builder()
                .attemptId(attempt.getId())
                .quizId(quiz.getId())
                .title(quiz.getTitle())
                .durationMinutes(quiz.getDurationMinutes())
                .startedAt(now)
                .expiresAt(expiresAt)
                .totalQuestions(questions.size())
                .firstQuestion(firstQuestion)
                .build();
    }

    @Transactional(readOnly = true)
    public QuestionDTO getQuestion(Long attemptId, int questionIndex, Long userId) {
        QuizAttempt attempt = attemptRepository.findByIdAndUserId(attemptId, userId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));

        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) {
            throw new RuntimeException("Attempt is not in progress");
        }

        if (attempt.isExpired()) {
            autoSubmitAttempt(attemptId);
            throw new RuntimeException("Quiz time has expired");
        }

        List<Question> questions = questionRepository
                .findByQuizIdOrderByOrderIndexAsc(attempt.getQuiz().getId());

        if (questionIndex < 0 || questionIndex >= questions.size()) {
            throw new RuntimeException("Invalid question index");
        }

        attempt.setCurrentQuestionIndex(questionIndex);
        attemptRepository.save(attempt);

        return toQuestionDTO(questions.get(questionIndex), false);
    }

    @Transactional
    public SubmitAnswerResponse submitAnswer(SubmitAnswerRequest request, Long userId) {
        QuizAttempt attempt = attemptRepository.findByIdAndUserId(request.getAttemptId(), userId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));

        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) {
            throw new RuntimeException("Attempt is not in progress");
        }

        if (attempt.isExpired()) {
            autoSubmitAttempt(request.getAttemptId());
            throw new RuntimeException("Quiz time has expired");
        }

        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new RuntimeException("Question not found"));

        // Check if answer already exists
        Optional<Answer> existingAnswer = answerRepository
                .findByAttemptIdAndQuestionId(request.getAttemptId(), request.getQuestionId());

        Answer answer = existingAnswer.orElse(Answer.builder()
                .attempt(attempt)
                .question(question)
                .build());

        // Update answer based on question type
        if (question.getType() == QuestionType.NUMERICAL) {
            answer.setNumericalAnswer(request.getNumericalAnswer());
            answer.setSelectedOption(null);
        } else {
            QuestionOption option = question.getOptions().stream()
                    .filter(opt -> opt.getId().equals(request.getSelectedOptionId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Invalid option"));
            answer.setSelectedOption(option);
            answer.setNumericalAnswer(null);
        }

        answer.setLastModifiedAt(LocalDateTime.now());
        if (answer.getAnsweredAt() == null) {
            answer.setAnsweredAt(LocalDateTime.now());
        }

        answerRepository.save(answer);

        long answeredCount = answerRepository.findByAttemptId(attempt.getId()).size();
        long totalQuestions = questionRepository.countByQuizId(attempt.getQuiz().getId());

        log.info("Answer saved for attempt {} question {}",
                request.getAttemptId(), request.getQuestionId());

        return SubmitAnswerResponse.builder()
                .saved(true)
                .message("Answer saved successfully")
                .answeredCount((int) answeredCount)
                .totalQuestions((int) totalQuestions)
                .build();
    }

    @Transactional
    public QuizResultDTO submitQuiz(Long attemptId, Long userId) {
        QuizAttempt attempt = attemptRepository.findByIdAndUserId(attemptId, userId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));

        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) {
            throw new RuntimeException("Attempt already submitted");
        }

        return finalizeAttempt(attempt, AttemptStatus.SUBMITTED);
    }

    @Transactional
    public void autoSubmitAttempt(Long attemptId) {
        QuizAttempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));

        if (attempt.getStatus() == AttemptStatus.IN_PROGRESS) {
            finalizeAttempt(attempt, AttemptStatus.AUTO_SUBMITTED);
        }
    }

    private QuizResultDTO finalizeAttempt(QuizAttempt attempt, AttemptStatus status) {
        timerService.stopTimer(attempt.getId());

        attempt.setStatus(status);
        attempt.setSubmittedAt(LocalDateTime.now());

        // Grade all answers
        List<Answer> answers = answerRepository.findByAttemptId(attempt.getId());
        int totalScore = 0;
        int totalPoints = 0;

        for (Answer answer : answers) {
            Question question = answer.getQuestion();
            totalPoints += question.getPoints();

            boolean isCorrect = gradeAnswer(answer, question);
            answer.setIsCorrect(isCorrect);
            answer.setPointsEarned(isCorrect ? question.getPoints() : 0);
            totalScore += answer.getPointsEarned();

            answerRepository.save(answer);
        }

        attempt.setScore(totalScore);
        attempt.setTotalPoints(totalPoints);
        attemptRepository.save(attempt);

        log.info("Attempt {} finalized with status {} - Score: {}/{}",
                attempt.getId(), status, totalScore, totalPoints);

        return getQuizResult(attempt.getId(), attempt.getUser().getId());
    }

    private boolean gradeAnswer(Answer answer, Question question) {
        switch (question.getType()) {
            case MCQ:
            case TRUE_FALSE:
                return answer.getSelectedOption() != null &&
                        answer.getSelectedOption().getIsCorrect();

            case NUMERICAL:
                // Check against correct options (stored as numerical values in option text)
                return question.getOptions().stream()
                        .filter(QuestionOption::getIsCorrect)
                        .anyMatch(opt -> {
                            try {
                                double correctValue = Double.parseDouble(opt.getOptionText());
                                return Math.abs(correctValue - answer.getNumericalAnswer()) < 0.01;
                            } catch (NumberFormatException e) {
                                return false;
                            }
                        });

            default:
                return false;
        }
    }

    @Transactional(readOnly = true)
    public QuizResultDTO getQuizResult(Long attemptId, Long userId) {
        QuizAttempt attempt = attemptRepository.findByIdAndUserId(attemptId, userId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));

        if (attempt.getStatus() == AttemptStatus.IN_PROGRESS) {
            throw new RuntimeException("Quiz is still in progress");
        }

        List<Answer> answers = answerRepository.findByAttemptId(attemptId);
        List<Question> questions = questionRepository
                .findByQuizIdWithOptions(attempt.getQuiz().getId());

        List<QuestionResultDTO> questionResults = questions.stream()
                .map(question -> {
                    Answer answer = answers.stream()
                            .filter(a -> a.getQuestion().getId().equals(question.getId()))
                            .findFirst()
                            .orElse(null);

                    return QuestionResultDTO.builder()
                            .questionId(question.getId())
                            .questionText(question.getQuestionText())
                            .type(question.getType())
                            .explanation(question.getExplanation())
                            .points(question.getPoints())
                            .pointsEarned(answer != null ? answer.getPointsEarned() : 0)
                            .isCorrect(answer != null ? answer.getIsCorrect() : false)
                            .userAnswer(answer != null ? toAnswerDTO(answer) : null)
                            .options(toOptionDTOs(question.getOptions(), true))
                            .build();
                })
                .collect(Collectors.toList());

        double percentage = attempt.getTotalPoints() > 0 ?
                (attempt.getScore() * 100.0 / attempt.getTotalPoints()) : 0;

        long correctCount = answers.stream().filter(Answer::getIsCorrect).count();

        return QuizResultDTO.builder()
                .attemptId(attempt.getId())
                .quizTitle(attempt.getQuiz().getTitle())
                .score(attempt.getScore())
                .totalPoints(attempt.getTotalPoints())
                .percentage(percentage)
                .passed(percentage >= attempt.getQuiz().getPassingScore())
                .submittedAt(attempt.getSubmittedAt())
                .correctAnswers((int) correctCount)
                .totalQuestions(questions.size())
                .questionResults(questionResults)
                .build();
    }

    public void processExpiredAttempts() {
        List<QuizAttempt> expiredAttempts = attemptRepository
                .findExpiredAttempts(LocalDateTime.now());

        for (QuizAttempt attempt : expiredAttempts) {
            try {
                autoSubmitAttempt(attempt.getId());
            } catch (Exception e) {
                log.error("Error processing expired attempt {}", attempt.getId(), e);
            }
        }
    }

    private QuestionDTO toQuestionDTO(Question question, boolean includeAnswers) {
        return QuestionDTO.builder()
                .id(question.getId())
                .quizId(question.getQuiz().getId())
                .type(question.getType())
                .questionText(question.getQuestionText())
                .points(question.getPoints())
                .orderIndex(question.getOrderIndex())
                .options(toOptionDTOs(question.getOptions(), includeAnswers))
                .explanation(includeAnswers ? question.getExplanation() : null)
                .build();
    }

    private List<QuestionOptionDTO> toOptionDTOs(List<QuestionOption> options, boolean includeAnswers) {
        return options.stream()
                .map(opt -> QuestionOptionDTO.builder()
                        .id(opt.getId())
                        .optionText(opt.getOptionText())
                        .orderIndex(opt.getOrderIndex())
                        .isCorrect(includeAnswers ? opt.getIsCorrect() : null)
                        .build())
                .collect(Collectors.toList());
    }

    private AnswerDTO toAnswerDTO(Answer answer) {
        return AnswerDTO.builder()
                .id(answer.getId())
                .questionId(answer.getQuestion().getId())
                .selectedOptionId(answer.getSelectedOption() != null ?
                        answer.getSelectedOption().getId() : null)
                .numericalAnswer(answer.getNumericalAnswer())
                .isCorrect(answer.getIsCorrect())
                .pointsEarned(answer.getPointsEarned())
                .answeredAt(answer.getAnsweredAt())
                .build();
    }
}
