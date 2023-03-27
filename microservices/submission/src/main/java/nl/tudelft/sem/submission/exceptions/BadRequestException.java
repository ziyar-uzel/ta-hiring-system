package nl.tudelft.sem.submission.exceptions;

import java.io.Serializable;

public class BadRequestException extends JsonException implements Serializable {

    private static final long serialVersionUID = 6266122900518251394L;

    public BadRequestException(String message) {
        super(message);
    }
}
