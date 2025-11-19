package app.quiz.glass.services;

import app.quiz.glass.dto.question.CreateOptionRequest;
import app.quiz.glass.dto.question.CreateQuestionRequest;
import app.quiz.glass.dto.question.QuestionDTO;
import app.quiz.glass.dto.question.QuestionOptionDTO;
import app.quiz.glass.entities.Question;
import app.quiz.glass.entities.QuestionOption;
import app.quiz.glass.entities.Quiz;
import app.quiz.glass.repositories.QuestionOptionRepository;
import app.quiz.glass.repositories.QuestionRepository;
import app.quiz.glass.repositories.QuizRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionOptionRepository optionRepository;
    private final QuizRepository quizRepository;

    @Transactional
    public Question createQuestion(Long quizId, CreateQuestionRequest request) {

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        if (quiz.getPublished()) {
            throw new RuntimeException("Cannot add questions to published quiz");
        }

        long currentCount = questionRepository.countByQuizId(quiz.getId());

        Question question = Question.builder()
                .quiz(quiz)
                .type(request.getType())
                .questionText(request.getQuestionText())
                .explanation(request.getExplanation())
                .points(request.getPoints())
                .orderIndex((int) currentCount)
                .build();

        question = questionRepository.save(question);

        // Create options
        if (request.getOptions() != null && !request.getOptions().isEmpty()) {
            for (int i = 0; i < request.getOptions().size(); i++) {
                CreateOptionRequest optReq = request.getOptions().get(i);
                QuestionOption option = QuestionOption.builder()
                        .question(question)
                        .optionText(optReq.getOptionText())
                        .isCorrect(optReq.getIsCorrect())
                        .orderIndex(i)
                        .build();
                optionRepository.save(option);
            }
        }

        return question;
    }


    @Transactional(readOnly = true)
    public List<QuestionDTO> getQuizQuestions(Long quizId, boolean includeAnswers) {
        List<Question> questions = questionRepository.findByQuizIdWithOptions(quizId);

        return questions.stream()
                .map(q -> toDTO(q, includeAnswers))
                .collect(Collectors.toList());
    }

    private QuestionDTO toDTO(Question question, boolean includeAnswers) {
        List<QuestionOptionDTO> options = question.getOptions().stream()
                .map(opt -> QuestionOptionDTO.builder()
                        .id(opt.getId())
                        .optionText(opt.getOptionText())
                        .orderIndex(opt.getOrderIndex())
                        .isCorrect(includeAnswers ? opt.getIsCorrect() : null)
                        .build())
                .collect(Collectors.toList());

        return QuestionDTO.builder()
                .id(question.getId())
                .quizId(question.getQuiz().getId())
                .type(question.getType())
                .questionText(question.getQuestionText())
                .points(question.getPoints())
                .orderIndex(question.getOrderIndex())
                .options(options)
                .explanation(includeAnswers ? question.getExplanation() : null)
                .build();
    }
}
