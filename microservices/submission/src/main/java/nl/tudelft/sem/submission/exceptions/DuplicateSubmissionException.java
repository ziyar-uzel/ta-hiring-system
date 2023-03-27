package nl.tudelft.sem.submission.exceptions;

import java.io.Serializable;

// Advice: BadRequest Advice
public class DuplicateSubmissionException extends BadRequestException implements Serializable {
    private static final long serialVersionUID = -7612909944959017047L;


    public DuplicateSubmissionException(String message) {
        super(message);
    }
}
