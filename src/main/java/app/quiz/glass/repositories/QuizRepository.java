package app.quiz.glass.repositories;

import app.quiz.glass.entities.Quiz;
import app.quiz.glass.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    Page<Quiz> findByPublishedTrue(Pageable pageable);

    @Query("SELECT q FROM Quiz q WHERE q.published = true " +
            "AND q.startTime <= :now AND q.endTime >= :now")
    Page<Quiz> findActiveQuizzes(@Param("now") LocalDateTime now, Pageable pageable);

    Page<Quiz> findByCreatedBy(User user, Pageable pageable);
}
