package nl.tudelft.cse.sem.user.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


public class StudentTest {

    static Student koen;
    static Student andrew;
    static Set<String> courses = new HashSet<>();
    static Map<String, Double> grades = new HashMap<>();
    static Set<String> taCourses = new HashSet<>();

    private static final String NAME = "koensnijder";

    /**
     * Setup function to create student object before all tests execute.
     *
     */
    @BeforeEach
    public void setup() {
        koen = new Student();
        andrew = new Student();

        courses.add("SEM");
        courses.add("AD");
        courses.add("ES");

        taCourses.add("ADS");

        grades.put("CG", 6.0);
        grades.put("ML", 7.0);
        grades.put("DS", null);

        koen.setStudentNumber(1230L);
        koen.setName("Koen");
        koen.setEmail("MyEmail@ForSure.com");
        koen.setNetId(NAME);
        koen.setPassword("iLoveCookies");
        koen.setCurrentCourses(courses);
        koen.setGrades(grades);
        koen.setTaCourses(taCourses);

        andrew.setStudentNumber(1230L);
        andrew.setName("Koen");
        andrew.setEmail("MyEmail@ForSure.com");
        andrew.setNetId(NAME);
        andrew.setPassword("iLoveCookies");
        andrew.setCurrentCourses(courses);
        andrew.setGrades(grades);
        andrew.setTaCourses(taCourses);
    }

    @Test
    void getStudentNumberTest() {
        assertEquals(1230L, koen.getStudentNumber());
    }

    @Test
    void getNameTest() {
        assertEquals("Koen", koen.getName());
    }

    @Test
    void getEmailTest() {
        assertEquals("MyEmail@ForSure.com", koen.getEmail());
    }

    @Test
    void getNetIdTest() {
        assertEquals(NAME, koen.getNetId());
    }

    @Test
    void getPasswordTest() {
        assertEquals("iLoveCookies", koen.getPassword());
    }

    @Test
    void getCurrentCoursesTest() {
        assertEquals(courses, koen.getCurrentCourses());
    }

    @Test
    void getGradesTest() {
        assertEquals(grades, koen.getGrades());
    }

    @Test
    void getTaCoursesTest() {
        assertEquals(taCourses, koen.getTaCourses());
    }

    @Test
    void isAccountNonExpiredTest() {
        assertTrue(koen.isAccountNonExpired());
    }

    @Test
    void isAccountNonLockedTest() {
        assertTrue(koen.isAccountNonLocked());
    }

    @Test
    void isCredentialsNonExpiredTest() {
        assertTrue(koen.isCredentialsNonExpired());
    }

    @Test
    void isEnabledTest() {
        assertTrue(koen.isEnabled());
    }

    @Test
    void addTaCourseTest() {
        koen.addTaCourse("How To Do A Backflip");
        assertTrue(koen.getTaCourses().contains("How To Do A Backflip"));
    }

    @Test
    void getUsernameTest() {
        assertEquals(NAME, koen.getNetId());
    }

    @Test
    void equalsSameTest() {
        assertEquals(koen, koen);
    }

    @Test
    void equalsTest() {
        assertEquals(koen, andrew);
    }

    @Test
    void equalsStudentNumbersDifferTest() {
        koen.setStudentNumber(42L);
        assertNotEquals(koen, andrew);
    }

    @Test
    void equalsObjectsDifferTest() {
        assertNotEquals(andrew, "asdf");
    }
}
