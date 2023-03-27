package nl.tudelft.sem.submission.utils.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.submission.communication.UserCommunication;
import nl.tudelft.sem.submission.entities.Status;
import nl.tudelft.sem.submission.entities.StudentRating;
import nl.tudelft.sem.submission.entities.Submission;
import nl.tudelft.sem.submission.exceptions.ServiceErrorException;
import nl.tudelft.sem.submission.repositories.SubmissionRepository;
import nl.tudelft.sem.submission.services.NotificationServiceTest;
import nl.tudelft.sem.submission.utils.strategy.models.RecommendedSubmission;
import org.junit.jupiter.api.Test;
import nl.tudelft.sem.submission.utils.util.SubmissionBuilder;

public class RatingStrategyTest {

    private final transient SubmissionRepository submissionRepository =
            mock(SubmissionRepository.class);
    private final transient UserCommunication userCommunication =
            mock(UserCommunication.class);
    private final transient RatingStrategy ratingStrategy =
            new RatingStrategy(submissionRepository, userCommunication);

    private static final String courseCode = "CSEBackflip";
    private static final List<Submission> list = new ArrayList<>();
    private static final Submission submission = new SubmissionBuilder()
            .withStudentNumber(1L).withCourse(courseCode).build(1L);
    private static final Submission submission2 = new SubmissionBuilder()
            .withStudentNumber(2L).withCourse(courseCode).build(2L);
    private static final Submission submission3 = new SubmissionBuilder()
            .withStudentNumber(3L).withCourse(courseCode).build(3L);


    private static  class SubmissionBuilder {

        private transient Submission submission;

        /**
         *
         * <p> Method to build an submission. </p>
         */
        public SubmissionBuilder() {
            this.submission = new Submission();
            this.submission.setStatus(Status.PENDING);
            this.submission.setAmountOfHours(0f);
            this.submission.setStudentGpa(10f);
            this.submission.setDescription("Best course ever!");

        }

        public Submission build(Long id) {
            this.submission.setSubmissionId(id);
            return this.submission;
        }

        public SubmissionBuilder withStatus(Status status) {
            this.submission.setStatus(status);
            return this;
        }

        public SubmissionBuilder withCourse(String courseId) {
            this.submission.setCourseCode(courseId);
            return this;
        }

        public SubmissionBuilder withStudentNumber(Long studentNumber) {
            this.submission.setStudentNumber(studentNumber);
            return this;
        }

        public SubmissionBuilder withStudentEmail(String studentEmail) {
            this.submission.setStudentEmail(studentEmail);
            return this;
        }

        public SubmissionBuilder withAmountOfHours(Float hours) {
            this.submission.setAmountOfHours(hours);
            return this;
        }
    }

    @Test
    void getBestApplicationsEmptyTest() throws ServiceErrorException {
        when(submissionRepository.getByCourseCode(courseCode))
                .thenReturn(Optional.of(new ArrayList<>()));

        assertEquals(new ArrayList<>(), ratingStrategy.getBestApplications(courseCode));
    }

    @Test
    void getBestApplicationsTest() throws ServiceErrorException {
        list.add(submission);
        list.add(submission2);
        list.add(submission3);

        List<StudentRating> studentRatingResponses =
                List.of(new StudentRating(submission.getStudentNumber(), 4.1f),
                        new StudentRating(submission2.getStudentNumber(), 3.2f),
                        new StudentRating(submission3.getStudentNumber(), 5.0f));
        List<Long> studentNumbers = List.of(submission.getStudentNumber(),
                submission2.getStudentNumber(), submission3.getStudentNumber());

        when(submissionRepository.getByCourseCode(courseCode)).thenReturn(Optional.of(list));
        when(userCommunication.getRatings(studentNumbers, null))
                .thenReturn(studentRatingResponses);

        List<RecommendedSubmission> highestGradeFirst = new ArrayList<>();
        highestGradeFirst.add(new RecommendedSubmission(5.0f, submission3));
        highestGradeFirst.add(new RecommendedSubmission(4.1f, submission));
        highestGradeFirst.add(new RecommendedSubmission(3.2f, submission2));

        // assertEquals(highestGradeFirst, ratingStrategy.getBestApplications(courseCode));
    }
}
