package nl.tudelft.cse.sem.user.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class LecturerTest {

    static Lecturer andy = new Lecturer();
    static Lecturer lecturer = new Lecturer();
    static Set<String> courses = new HashSet<>();

    private static final String NAME = "andyzaidman";

    /**
     * Setup function to create Lecturer object before all tests are executed.
     *
     */
    @BeforeEach
    public void setup() {
        courses.add("OOP");
        courses.add("HOWTOAVOIDFRAUD");

        andy.setNetId(NAME);
        andy.setName("Andy");
        andy.setEmail("weJustGotALetter@IWonderWhoIt'sFrom.com");
        andy.setEmployeeNumber(1L);
        andy.setPassword("superSecret");
        andy.setCourses(courses);


        lecturer.setNetId(NAME);
        lecturer.setName("Andy");
        lecturer.setEmail("weJustGotALetter@IWonderWhoIt'sFrom.com");
        lecturer.setEmployeeNumber(1L);
        lecturer.setPassword("superSecret");
        lecturer.setCourses(courses);
    }

    @Test
    void getNetIdTest() {
        assertEquals(NAME, andy.getNetId());
    }

    @Test
    void getNameTest() {
        assertEquals("Andy", andy.getName());
    }

    @Test
    void getUserNameTest() {
        assertEquals(NAME, andy.getUsername());
    }

    @Test
    void getEmailTest() {
        assertEquals("weJustGotALetter@IWonderWhoIt'sFrom.com", andy.getEmail());
    }

    @Test
    void getEmployeeNumberTest() {
        assertEquals(1L, andy.getEmployeeNumber());
    }

    @Test
    void getPasswordTest() {
        assertEquals("superSecret", andy.getPassword());
    }

    @Test
    void getCoursesTest() {
        assertEquals(courses, andy.getCourses());
    }

    @Test
    void isAccountNonExpiredTest() {
        assertTrue(andy.isAccountNonExpired());
    }

    @Test
    void isAccountNonLockedTest() {
        assertTrue(andy.isAccountNonLocked());
    }

    @Test
    void isCredentialsNonExpiredTest() {
        assertTrue(andy.isCredentialsNonExpired());
    }

    @Test
    void isEnabledTest() {
        assertTrue(andy.isEnabled());
    }

    @Test
    void equalsSameTest() {
        assertEquals(andy, andy);
    }

    @Test
    void equalsTrueTest() {
        assertEquals(andy, lecturer);
        assertEquals(andy.hashCode(), andy.hashCode());
    }



    @Test
    void equalsEmployeeNumberDifferTest() {
        andy.setEmployeeNumber(45L);
        assertNotEquals(andy, lecturer);
    }


    @Test
    void equalsCoursesDifferTest() {
        andy.setCourses(Set.of("test"));
        assertNotEquals(andy, lecturer);
    }

    @Test
    void equalsOtherObjectTest() {
        assertNotEquals(andy, "lecturer");
    }


}
