package nl.tudelft.sem.course.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;




class CourseTest {

    private transient Course course = new Course();
    private transient Course course2 = new Course();
    private transient Course identicalCourse = new Course();

    @BeforeEach
    void setUp() {

        Date startDate = new Date();
        int noOfDays = 180;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.DAY_OF_YEAR, noOfDays);
        Date endDate = calendar.getTime();

        Set<Long> lecturers = new HashSet<>(Arrays.asList(100L, 200L, 300L));
        Set<Long> candidateStudents = new HashSet<>(Arrays.asList(10L, 20L, 30L));
        Set<Long> hiredStudents = new HashSet<>(Arrays.asList(10L, 20L));

        course.setCourseCode("CSE2000");
        course.setStartDate(startDate);
        course.setEndDate(endDate);
        course.setDescription("Very Important Course");
        course.setLecturers(lecturers);
        course.setCandidateStudents(candidateStudents);
        course.setHiredStudents(hiredStudents);
        course.setNumOfStudents(40);
        identicalCourse.setCourseCode("CSE2000");
        identicalCourse.setStartDate(startDate);
        identicalCourse.setEndDate(endDate);
        identicalCourse.setDescription("Very Important Course");
        identicalCourse.setLecturers(lecturers);
        identicalCourse.setCandidateStudents(candidateStudents);
        identicalCourse.setHiredStudents(hiredStudents);
        identicalCourse.setNumOfStudents(40);
        course2.setCourseCode("CSE1234");
        course2.setStartDate(endDate);
    }

    @Test
    void addHiredStudents() {
        course.addHiredStudents(1000L);
        assertTrue(course.getHiredStudents().contains(1000L));
    }

    @Test
    void addCandidateStudents() {
        course.addCandidateStudents(1000L);
        assertTrue(course.getCandidateStudents().contains(1000L));
    }

    @Test
    void addLecturer() {
        course.addLecturer(10000L);
        assertTrue(course.getLecturers().contains(10000L));
    }

    @Test
    void testEquals() {
        assertTrue(identicalCourse.equals(course) && course.equals(identicalCourse));
        assertTrue(course.hashCode() == identicalCourse.hashCode());
        assertEquals(course, course);

    }

    @Test
    void testNotEquals() {
        assertFalse(course2.equals(course) && course.equals(course2));
        assertFalse(course.hashCode() == course2.hashCode());
    }

    @Test
    void testEqualsSameObject() {
        assertTrue(course.equals(course) && course.equals(course));
        assertTrue(course.hashCode() == course.hashCode());
    }

    @Test
    void testNotEqualsNull() {
        assertNotEquals(course, null);
    }

}