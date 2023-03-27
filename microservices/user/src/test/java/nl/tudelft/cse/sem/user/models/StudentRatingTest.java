package nl.tudelft.cse.sem.user.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


public class StudentRatingTest {

    private static StudentRating studentRating1;
    private static StudentRating studentRating2;

    @BeforeEach
    void init() {
        studentRating1 = new StudentRating(1L, 8.3f);
        studentRating2 = new StudentRating(1L, 8.3f);
    }

    @Test
    void sameTest() {
        assertEquals(studentRating1, studentRating1);
    }

    @Test
    void equalTest() {
        assertEquals(studentRating1, studentRating2);
    }

    @Test
    void studentNumberDifferTest() {
        studentRating1.setStudentNumber(2L);
        assertNotEquals(studentRating1, studentRating2);
    }

    @Test
    void ratingsDifferTest() {
        studentRating1.setRating(5.0f);
        assertNotEquals(studentRating1, studentRating2);
    }

    @Test
    void randomObjectTest() {
        assertNotEquals(studentRating1, "asdf");
    }
}
