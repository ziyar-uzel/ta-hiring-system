package nl.tudelft.sem.course.exceptions;

import java.io.Serializable;

public class CourseNotFoundException extends Exception implements Serializable {

    private static final long serialVersionUID = -703L;

    public CourseNotFoundException(String message) {
        super(message);
    }
}
