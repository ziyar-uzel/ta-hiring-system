package nl.tudelft.sem.submission.exceptions;

import java.io.Serializable;

// Advice: Forbidden Advice
public class InsufficientGradeException extends BadRequestException implements Serializable {
    private static final long serialVersionUID = 8264789617037079207L;

    public InsufficientGradeException(String message) {
        super(message);
    }

}
