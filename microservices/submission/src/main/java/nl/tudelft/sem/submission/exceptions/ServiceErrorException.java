package nl.tudelft.sem.submission.exceptions;

import java.io.Serializable;

public class ServiceErrorException extends JsonException implements Serializable {

    private static final long serialVersionUID = -3945833088976438158L;

    public ServiceErrorException(String message) {
        super(message);
    }
}
