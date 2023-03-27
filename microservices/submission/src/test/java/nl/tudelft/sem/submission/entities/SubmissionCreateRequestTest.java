package nl.tudelft.sem.submission.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import nl.tudelft.sem.submission.controller.dto.SubmissionCreateRequest;
import org.junit.jupiter.api.Test;

public class SubmissionCreateRequestTest {

    private final transient SubmissionCreateRequest request = new SubmissionCreateRequest();

    @Test
    public void testAllArgsConstructor() {
        SubmissionCreateRequest req = new SubmissionCreateRequest(42L, "email");
        assertEquals(42L, req.getStudentNumber());
        assertEquals("email", req.getStudentEmail());
    }

    @Test
    public void testGetStudentNumber() {
        request.setStudentNumber(101010L);
        assertEquals(101010L, request.getStudentNumber());
    }

    @Test
    public void testGetStudentEmail() {
        request.setStudentEmail("student@tudelft.nl");
        assertEquals("student@tudelft.nl", request.getStudentEmail());
    }

}
