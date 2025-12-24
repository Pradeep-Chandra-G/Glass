package app.glass.Glass.repository;

import app.glass.Glass.model.QuizAttempt;
import app.glass.Glass.model.QuizAttempt.AttemptStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    List<QuizAttempt> findByUserId(Long userId);

    List<QuizAttempt> findByUserIdAndQuizId(Long userId, Long quizId);

    Optional<QuizAttempt> findByUserIdAndQuizIdAndStatus(
            Long userId,
            Long quizId,
            AttemptStatus status
    );

    @Query("SELECT COUNT(qa) FROM QuizAttempt qa WHERE qa.user.id = :userId AND qa.quiz.id = :quizId AND qa.status = 'COMPLETED'")
    long countCompletedAttempts(@Param("userId") Long userId, @Param("quizId") Long quizId);

    List<QuizAttempt> findByQuizIdAndStatus(Long quizId, AttemptStatus status);
}