package nl.tudelft.sem.submission.exceptions;

import java.io.Serializable;

// Advice: BadRequest Advice
public class SubmissionDeadlinePassedException extends
        BadRequestException implements Serializable {

    private static final long serialVersionUID = -2835263997379541960L;

    public SubmissionDeadlinePassedException(String message) {
        super(message);
    }
}
