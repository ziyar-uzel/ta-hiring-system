package nl.tudelft.sem.submission.exceptions;

import java.io.Serializable;

public class ForbiddenException extends JsonException implements Serializable {
    private static final long serialVersionUID = -726569671748579762L;

    public ForbiddenException(String message) {
        super(message);
    }
}
