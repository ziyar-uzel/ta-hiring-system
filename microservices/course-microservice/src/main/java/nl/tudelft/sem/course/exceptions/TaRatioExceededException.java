package nl.tudelft.sem.course.exceptions;

import java.io.Serializable;

public class TaRatioExceededException extends Exception implements Serializable {

    private static final long serialVersionUID = -703L;

    public TaRatioExceededException(String message) {
        super(message);
    }
}
