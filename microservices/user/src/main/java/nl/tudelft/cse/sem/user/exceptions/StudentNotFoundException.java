package nl.tudelft.cse.sem.user.exceptions;

import java.io.Serializable;

public class StudentNotFoundException extends Exception implements Serializable {
    private static final long serialVersionUID = -2835263659379541870L;

    public StudentNotFoundException(String message) {
        super(message);
    }
}