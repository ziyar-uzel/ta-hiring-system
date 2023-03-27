package nl.tudelft.sem.submission.exceptions;

import java.io.Serializable;

public class UserServiceErrorException extends ServiceErrorException implements Serializable {

    private static final long serialVersionUID = -1382526217433918452L;

    public UserServiceErrorException(String message) {
        super(message);
    }

}
