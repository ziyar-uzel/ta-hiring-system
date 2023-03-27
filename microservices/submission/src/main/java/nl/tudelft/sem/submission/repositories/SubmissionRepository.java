package nl.tudelft.sem.submission.repositories;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.submission.entities.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    Optional<Submission> getByStudentNumberAndCourseCode(Long studentNumber, String courseCode);

    Optional<Submission> getBySubmissionId(Long submissionId);

    Optional<List<Submission>> getByCourseCode(String courseCode);
}
