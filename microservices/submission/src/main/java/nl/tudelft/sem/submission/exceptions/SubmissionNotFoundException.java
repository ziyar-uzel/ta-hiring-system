package nl.tudelft.sem.submission.exceptions;

import java.io.Serializable;

public class SubmissionNotFoundException extends JsonException implements Serializable {
    private static final long serialVersionUID = -2835263997379541870L;

    public SubmissionNotFoundException(String message) {
        super(message);
    }
}
