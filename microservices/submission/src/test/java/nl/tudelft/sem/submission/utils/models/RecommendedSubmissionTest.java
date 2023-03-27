package nl.tudelft.sem.submission.utils.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import nl.tudelft.sem.submission.entities.Status;
import nl.tudelft.sem.submission.entities.Submission;
import nl.tudelft.sem.submission.utils.strategy.models.RecommendedSubmission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class RecommendedSubmissionTest {
    private static RecommendedSubmission recommendedSubmission1;
    private static RecommendedSubmission recommendedSubmission2;

    @BeforeEach
    void init() {
        Submission submission1 = new Submission();
        submission1.setSubmissionId(1010L);
        submission1.setStudentNumber(42L);
        submission1.setAmountOfHours(2F);
        submission1.setStudentEmail("student@tudelft.nl");
        submission1.setAmountOfHours(10f);
        submission1.setStudentGpa(10.0f);
        submission1.setDescription("This doesn't make sense at all");
        submission1.setCourseCode("BackflipsAreSick");
        submission1.setStatus(Status.ACCEPTED);

        Submission submission2 = new Submission();
        submission2.setSubmissionId(1020L);
        submission2.setStudentNumber(42L);
        submission2.setAmountOfHours(2F);
        submission2.setStudentEmail("student@tudelft.nl");
        submission2.setAmountOfHours(10f);
        submission2.setStudentGpa(10.0f);
        submission2.setDescription("This doesn't make sense at all");
        submission2.setCourseCode("BackflipsAreSick");
        submission2.setStatus(Status.ACCEPTED);

        recommendedSubmission1 = new RecommendedSubmission(10.0f, submission1);
        recommendedSubmission2 = new RecommendedSubmission(10.0f, submission2);

    }

    @Test
    void testEquals() {
        assertEquals(recommendedSubmission1, recommendedSubmission2);
    }

    @Test
    void testEqualsSame() {
        assertEquals(recommendedSubmission1, recommendedSubmission1);
    }

    @Test
    void testSubmissionNotEqual() {
        recommendedSubmission2.getSubmission().setStudentNumber(49L);
        assertNotEquals(recommendedSubmission1, recommendedSubmission2);
    }

    @Test
    void testScoreNotEqual() {
        recommendedSubmission2.setScore(49f);
        assertNotEquals(recommendedSubmission1, recommendedSubmission2);
    }

    @Test
    void testWithOtherObject() {
        assertNotEquals(recommendedSubmission1, "asdf");
    }
}
