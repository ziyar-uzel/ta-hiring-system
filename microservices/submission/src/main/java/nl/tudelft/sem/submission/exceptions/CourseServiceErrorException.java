package nl.tudelft.sem.submission.exceptions;

import java.io.Serializable;

public class CourseServiceErrorException extends ServiceErrorException implements Serializable {
    private static final long serialVersionUID = -6767807204444081766L;

    public CourseServiceErrorException(String message) {
        super(message);
    }

}
