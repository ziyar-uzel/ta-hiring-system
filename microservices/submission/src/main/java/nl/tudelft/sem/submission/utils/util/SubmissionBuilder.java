package nl.tudelft.sem.submission.utils.util;

import nl.tudelft.sem.submission.entities.Status;
import nl.tudelft.sem.submission.entities.Submission;

public class SubmissionBuilder {

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
