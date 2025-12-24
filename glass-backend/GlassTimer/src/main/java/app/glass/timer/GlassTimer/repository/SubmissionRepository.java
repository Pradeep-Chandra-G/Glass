package app.glass.timer.GlassTimer.repository;

import app.glass.timer.GlassTimer.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {}
