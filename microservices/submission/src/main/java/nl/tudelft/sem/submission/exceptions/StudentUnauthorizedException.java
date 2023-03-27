package nl.tudelft.sem.submission.exceptions;

import java.io.Serializable;

public class StudentUnauthorizedException extends ForbiddenException implements Serializable {

    private static final long serialVersionUID = -7557451968281573773L;

    public StudentUnauthorizedException(String message) {
        super(message);
    }
}
