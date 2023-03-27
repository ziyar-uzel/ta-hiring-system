package nl.tudelft.cse.sem.user.filters;

import nl.tudelft.cse.sem.user.exceptions.AttemptAuthenticationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

public class JwtPublisherTest {

    private transient JwtPublisher publisher;
    private transient HttpServletRequest request = mock(HttpServletRequest.class);
    private transient HttpServletResponse response = mock(HttpServletResponse.class);

    @BeforeEach
    void setup() {
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
//        UserService userService = mock(UserService.class);
        publisher = new JwtPublisher(authenticationManager);//, userService
    }

    @Test
    void attemptAuthenticationTest() {
        assertThrows(AttemptAuthenticationException.class,
                () -> publisher.attemptAuthentication(request, response));
    }
}
