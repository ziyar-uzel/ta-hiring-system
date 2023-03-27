package nl.tudelft.sem.submission.exceptions;

import java.io.Serializable;

public class LecturerUnauthorizedException extends ForbiddenException implements Serializable {

    private static final long serialVersionUID = 1842500290557325293L;

    public LecturerUnauthorizedException(String message) {
        super(message);
    }
}
