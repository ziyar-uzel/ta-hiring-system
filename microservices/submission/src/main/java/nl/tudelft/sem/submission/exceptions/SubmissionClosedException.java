package nl.tudelft.sem.submission.exceptions;

import java.io.Serializable;

// Advice: BadRequest Advice
public class SubmissionClosedException extends BadRequestException implements Serializable {

    private static final long serialVersionUID = 2394338912119871970L;

    public SubmissionClosedException(String message) {
        super(message);
    }
}
