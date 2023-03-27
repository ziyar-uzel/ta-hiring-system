package nl.tudelft.sem.submission.emailconfig;

import static org.junit.jupiter.api.Assertions.assertEquals;

import nl.tudelft.sem.submission.entities.Status;
import org.junit.jupiter.api.Test;

public class EmailSendingTest {

    private static final String courseCode = "IJustWantVacation";

    @Test
    void getBodyStatusAcceptedTest() {
        String result = EmailSending.getBody(Status.ACCEPTED, courseCode);
        String expected = "Congratulations! Your application to course " + courseCode
                + " has been accepted!";
        assertEquals(expected, result);
    }

    @Test
    void getBodyStatusRejectedTest() {
        String result = EmailSending.getBody(Status.REJECTED, courseCode);
        String expected = "Sorry! Your application to course " + courseCode
                + " has been rejected!";
        assertEquals(expected, result);
    }

    @Test
    void getBodyStatusOtherTest() {
        String result = EmailSending.getBody(Status.PENDING, courseCode);
        String expected = "Sorry. We can't seem to retrieve "
                + "your application result. Please contact support.";
        assertEquals(expected, result);
    }
}
