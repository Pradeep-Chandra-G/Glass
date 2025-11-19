package app.quiz.glass.repositories;

import app.quiz.glass.entities.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    Optional<Answer> findByAttemptIdAndQuestionId(Long attemptId, Long questionId);

    List<Answer> findByAttemptId(Long attemptId);

    @Query("SELECT COUNT(a) FROM Answer a WHERE a.attempt.id = :attemptId " +
            "AND a.isCorrect = true")
    long countCorrectAnswers(@Param("attemptId") Long attemptId);

    @Query("SELECT SUM(a.pointsEarned) FROM Answer a WHERE a.attempt.id = :attemptId")
    Integer sumPointsEarned(@Param("attemptId") Long attemptId);
}
