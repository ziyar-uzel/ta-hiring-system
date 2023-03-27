package nl.tudelft.cse.sem.user.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


public class StudentGradeTest {

    private static StudentGrade studentGrade1;
    private static StudentGrade studentGrade2;

    @BeforeEach
    void init() {
        studentGrade1 = new StudentGrade(1L, 8.3);
        studentGrade2 = new StudentGrade(1L, 8.3);
    }

    @Test
    void sameTest() {
        assertEquals(studentGrade1, studentGrade1);
    }

    @Test
    void equalTest() {
        assertEquals(studentGrade1, studentGrade2);
    }

    @Test
    void studentNumberDifferTest() {
        studentGrade1.setStudentNumber(2L);
        assertNotEquals(studentGrade1, studentGrade2);
    }

    @Test
    void gradesDifferTest() {
        studentGrade1.setGrade(5.0);
        assertNotEquals(studentGrade1, studentGrade2);
    }

    @Test
    void randomObjectTest() {
        assertNotEquals(studentGrade1, "asdf");
    }

}
