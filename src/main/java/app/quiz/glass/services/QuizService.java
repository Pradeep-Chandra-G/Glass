package app.quiz.glass.services;

import app.quiz.glass.dto.pagination.PageResponse;
import app.quiz.glass.dto.quiz.CreateQuizRequest;
import app.quiz.glass.dto.quiz.QuizDTO;
import app.quiz.glass.entities.Question;
import app.quiz.glass.entities.Quiz;
import app.quiz.glass.entities.User;
import app.quiz.glass.repositories.QuestionRepository;
import app.quiz.glass.repositories.QuizAttemptRepository;
import app.quiz.glass.repositories.QuizRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final QuizAttemptRepository attemptRepository;

    @Transactional
    public Quiz createQuiz(CreateQuizRequest request, User createdBy) {
        Quiz quiz = Quiz.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .durationMinutes(request.getDurationMinutes())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .shuffleQuestions(request.getShuffleQuestions())
                .shuffleOptions(request.getShuffleOptions())
                .passingScore(request.getPassingScore())
                .published(false)
                .createdBy(createdBy)
                .createdAt(LocalDateTime.now())
                .build();

        return quizRepository.save(quiz);
    }

    @Transactional
    public Quiz publishQuiz(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        long questionCount = questionRepository.countByQuizId(quizId);
        if (questionCount == 0) {
            throw new RuntimeException("Cannot publish quiz without questions");
        }

        quiz.setPublished(true);
        return quizRepository.save(quiz);
    }

    @Transactional(readOnly = true)
    public PageResponse<QuizDTO> getActiveQuizzes(int page, int size, Long userId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("startTime").descending());
        Page<Quiz> quizPage = quizRepository.findActiveQuizzes(LocalDateTime.now(), pageable);

        List<QuizDTO> content = quizPage.getContent().stream()
                .map(quiz -> toDTO(quiz, userId))
                .collect(Collectors.toList());

        return PageResponse.<QuizDTO>builder()
                .content(content)
                .pageNumber(page)
                .pageSize(size)
                .totalElements(quizPage.getTotalElements())
                .totalPages(quizPage.getTotalPages())
                .last(quizPage.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public QuizDTO getQuizById(Long quizId, Long userId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
        return toDTO(quiz, userId);
    }

    private QuizDTO toDTO(Quiz quiz, Long userId) {
        long questionCount = questionRepository.countByQuizId(quiz.getId());
        int totalPoints = questionRepository.findByQuizIdOrderByOrderIndexAsc(quiz.getId())
                .stream()
                .mapToInt(Question::getPoints)
                .sum();

        boolean hasActiveAttempt = attemptRepository
                .findActiveAttempt(userId, quiz.getId())
                .isPresent();

        return QuizDTO.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .durationMinutes(quiz.getDurationMinutes())
                .startTime(quiz.getStartTime())
                .endTime(quiz.getEndTime())
                .published(quiz.getPublished())
                .passingScore(quiz.getPassingScore())
                .totalQuestions((int) questionCount)
                .totalPoints(totalPoints)
                .isActive(quiz.isActive())
                .hasActiveAttempt(hasActiveAttempt)
                .build();
    }
}
