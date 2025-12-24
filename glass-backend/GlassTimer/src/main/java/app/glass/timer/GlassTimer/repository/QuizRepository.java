package app.glass.timer.GlassTimer.repository;

import app.glass.timer.GlassTimer.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepository extends JpaRepository<Quiz, Long> {}