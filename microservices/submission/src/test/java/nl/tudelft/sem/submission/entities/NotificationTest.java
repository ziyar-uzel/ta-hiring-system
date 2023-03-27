package nl.tudelft.sem.submission.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NotificationTest {

    private static final Notification notification = new Notification();
    private static final Notification notification2 = new Notification();

    @BeforeEach
    void setup() {
        notification2.setNotifiedStudentNumber(505L);
        notification2.setNotifiedEmail("hello@gmail.com");
        notification2.setNotifiedCourseId("CSE5000");
        notification2.setStatus(Status.ACCEPTED);

    }

    @Test
    void setNotifiedStudentNumberTest() {
        notification.setNotifiedStudentNumber(10101001L);
        assertEquals(10101001L, notification.getNotifiedStudentNumber());
    }

    @Test
    void setNotifiedEmailTest() {
        notification.setNotifiedEmail("KoenWillNotDoBackflips@Ever.com");
        assertEquals("KoenWillNotDoBackflips@Ever.com", notification.getNotifiedEmail());
    }

    @Test
    void setStatusTest() {
        notification.setStatus(Status.ACCEPTED);
        assertEquals(Status.ACCEPTED, notification.getStatus());
    }

    @Test
    void setNotifiedCourseIdTest() {
        notification.setNotifiedCourseId("BackflipCourse");
        assertEquals("BackflipCourse", notification.getNotifiedCourseId());
    }

    @Test
    void notEqualsTest() {
        assertNotEquals(notification, notification2);
    }

    @Test
    void sameEqualsTest() {
        assertEquals(notification2, notification2);
    }

    @Test
    void nullEqualsTest() {
        assertNotEquals(notification2, null);
    }

    @Test
    void hashCodeTest() {
        int hash = Objects.hash(notification2.getNotifiedStudentNumber(),
            notification2.getNotifiedEmail(), notification2.getStatus(),
            notification2.getNotifiedCourseId());
        assertEquals(hash, notification2.hashCode());
    }
}
