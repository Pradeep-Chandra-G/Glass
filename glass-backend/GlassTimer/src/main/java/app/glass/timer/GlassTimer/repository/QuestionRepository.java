package app.glass.timer.GlassTimer.repository;

import app.glass.timer.GlassTimer.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {}