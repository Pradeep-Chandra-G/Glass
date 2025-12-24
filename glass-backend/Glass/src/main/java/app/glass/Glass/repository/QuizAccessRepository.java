package app.glass.Glass.repository;

import app.glass.Glass.model.QuizAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface QuizAccessRepository extends JpaRepository<QuizAccess, Long> {

    Optional<QuizAccess> findByUserIdAndQuizId(Long userId, Long quizId);

    List<QuizAccess> findByUserId(Long userId);

    List<QuizAccess> findByQuizId(Long quizId);

    boolean existsByUserIdAndQuizId(Long userId, Long quizId);
}