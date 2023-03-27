package nl.tudelft.cse.sem.user.exceptions;

import java.io.Serializable;
import org.springframework.security.core.AuthenticationException;

public class AttemptAuthenticationException
        extends AuthenticationException
        implements Serializable {
    private static final long serialVersionUID =
            -2835263995679541960L;

    public AttemptAuthenticationException(String message) {
        super(message);
    }

}
