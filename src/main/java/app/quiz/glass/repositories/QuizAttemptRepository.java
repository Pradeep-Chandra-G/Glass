package app.quiz.glass.repositories;


import app.quiz.glass.entities.AttemptStatus;
import app.quiz.glass.entities.QuizAttempt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    Optional<QuizAttempt> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.user.id = :userId " +
            "AND qa.quiz.id = :quizId AND qa.status = 'IN_PROGRESS'")
    Optional<QuizAttempt> findActiveAttempt(@Param("userId") Long userId,
                                            @Param("quizId") Long quizId);

    Page<QuizAttempt> findByUserId(Long userId, Pageable pageable);

    Page<QuizAttempt> findByQuizId(Long quizId, Pageable pageable);

    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.status = 'IN_PROGRESS' " +
            "AND qa.expiresAt <= :now")
    List<QuizAttempt> findExpiredAttempts(@Param("now") LocalDateTime now);

    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.quiz.id = :quizId " +
            "AND qa.status IN ('SUBMITTED', 'AUTO_SUBMITTED') " +
            "ORDER BY qa.score DESC, qa.submittedAt ASC")
    List<QuizAttempt> findLeaderboard(@Param("quizId") Long quizId, Pageable pageable);

    long countByQuizIdAndStatus(Long quizId, AttemptStatus status);
}

