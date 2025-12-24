package app.glass.Glass.repository;

import app.glass.Glass.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {}
