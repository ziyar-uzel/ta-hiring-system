package nl.tudelft.sem.submission.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SubmissionTest {

    private static final Submission submission = new Submission();
    private static final Submission submission2 = new Submission();

    @BeforeEach
    void setup() {
        submission.setSubmissionId(1010L);
        submission.setStudentNumber(42L);
        submission.setAmountOfHours(2F);
        submission.setStudentEmail("student@tudelft.nl");
        submission.setAmountOfHours(10f);
        submission.setStudentGpa(10.0f);
        submission.setDescription("This doesn't make sense at all");
        submission.setCourseCode("BackflipsAreSick");
        submission.setStatus(Status.ACCEPTED);

        submission2.setSubmissionId(1020L);
        submission2.setStudentNumber(42L);
        submission2.setAmountOfHours(2F);
        submission2.setStudentEmail("student@tudelft.nl");
        submission2.setAmountOfHours(10f);
        submission2.setStudentGpa(10.0f);
        submission2.setDescription("This doesn't make sense at all");
        submission2.setCourseCode("BackflipsAreSick");
        submission2.setStatus(Status.ACCEPTED);
    }

    @Test
    void submissionItselfEqualTest() {
        assertEquals(submission, submission);
    }

    @Test
    void submissionEqualWithDifferentIdTest() {
        assertEquals(submission, submission2);
    }

    @Test
    void submissionNotEqualTest() {
        assertNotEquals(submission, new Submission());
    }

    @Test
    void submissionNotEqualStudentNumber() {
        submission2.setStudentNumber(43L);
        assertNotEquals(submission, submission2);
    }

    @Test
    void submissionNotEqualStudentEmail() {
        submission2.setStudentEmail("student2@tudelft.nl");
        assertNotEquals(submission, submission2);
    }

    @Test
    void submissionNotEqualStatus() {
        submission2.setStatus(Status.PENDING);
        assertNotEquals(submission, submission2);
    }

    @Test
    void submissionNotEqualAmountOfHours() {
        submission2.setAmountOfHours(12f);
        assertNotEquals(submission, submission2);
    }

    @Test
    void submissionNotEqualStudentGpa() {
        submission2.setStudentGpa(9.2f);
        assertNotEquals(submission, submission2);
    }

    @Test
    void submissionNotEqualDescription() {
        submission2.setDescription("KoenDoesBackFlip");
        assertNotEquals(submission, submission2);
    }

    @Test
    void hashCodeTest() {
        int hash = Objects.hash(submission.getSubmissionId(),
                submission.getCourseCode(), submission.getStudentNumber(),
                submission.getStudentEmail(), submission.getStatus(),
                submission.getAmountOfHours(), submission.getStudentGpa(),
                submission.getDescription());
        assertEquals(hash, submission.hashCode());
    }
}
