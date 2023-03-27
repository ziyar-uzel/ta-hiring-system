package nl.tudelft.cse.sem.user.exceptions;

import java.io.Serializable;

public class CourseNotFoundException extends Exception implements Serializable {
    private static final long serialVersionUID = -2816863659379541870L;

    public CourseNotFoundException(String message) {
        super(message);
    }
}