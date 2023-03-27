package nl.tudelft.cse.sem.user.models;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserRequestTest {

    static UserRequest userRequest;
    static UserRequest empty;

    /**
     * Setup function to initialize a userRequest object before all tests execute.
     *
     */
    @BeforeAll
    public static void setup() {

        userRequest = new UserRequest("someId", "password");
        empty = new UserRequest();
    }

    @Test
    void nonNullConstructorTest() {
        assertNotNull(empty);
    }

    @Test
    void getNetIdTest() {
        assertEquals("someId", userRequest.getNetid());
    }

    @Test
    void getPasswordTest() {
        assertEquals("password", userRequest.getPassword());
    }

    @Test
    void setNetIdTest() {
        empty.setNetid("yes");
        assertEquals("yes", empty.getNetid());
    }

    @Test
    void setPasswordTest() {
        empty.setPassword("NotAPassword");
        assertEquals("NotAPassword", empty.getPassword());
    }
}
