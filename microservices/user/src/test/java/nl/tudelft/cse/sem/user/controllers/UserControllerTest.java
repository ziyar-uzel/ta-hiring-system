package nl.tudelft.cse.sem.user.controllers;

import nl.tudelft.cse.sem.user.exceptions.CourseNotFoundException;
import nl.tudelft.cse.sem.user.exceptions.StudentNotFoundException;
import nl.tudelft.cse.sem.user.models.Student;
import nl.tudelft.cse.sem.user.models.StudentGrade;
import nl.tudelft.cse.sem.user.models.StudentRating;
import nl.tudelft.cse.sem.user.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    private final transient UserService userService = Mockito.mock(UserService.class);
    private transient UserController userController = new UserController(userService);

    private transient Student student = new Student();
    private static String OOP = "OOP";

    @BeforeEach
    void setUp() {
        student.setName("Andrew");
        student.setEmail("andrew@email.com");
        student.setPassword("password");
        student.setNetId("amereuta");
        student.setStudentNumber((long) 1111111);
        student.setTaCourses(Set.of(OOP));
        student.setCurrentCourses(Set.of("AD"));
        student.setGrades(Map.of(OOP, 8.0));
    }

    @Test
    void getUserByNetidTest() {
        when(userService.loadUserByUsername("amereuta"))
                .thenReturn(student);
        assertEquals(new ResponseEntity<>(student, HttpStatus.OK), userController
                .getUserByNetid("amereuta"));
    }

    @Test
    void getUserByNetIdTestException() {
        when(userService.loadUserByUsername("no"))
                .thenThrow(new UsernameNotFoundException("Student not found"));
        assertEquals(ResponseEntity.badRequest().build(), userController
                .getUserByNetid("no"));
    }

    @Test
    void getGradeByCourseTest() throws CourseNotFoundException, StudentNotFoundException {
        when(userService.findGradeByStudentNumberAndCourseCode(1111111L, OOP))
                .thenReturn(8.0);
        assertEquals(new ResponseEntity<>(Map.of("grade", 8.0), HttpStatus.OK), userController
                .getGradeByCourse(OOP, 1111111L));
    }

    @Test
    void getGradeByCourseTestCourseNotFoundExceptionTest()
            throws CourseNotFoundException, StudentNotFoundException {
        when(userService.findGradeByStudentNumberAndCourseCode(1111111L, "R&L"))
                .thenThrow(new CourseNotFoundException("course not found"));
        assertThrows(CourseNotFoundException.class,
                () -> userController.getGradeByCourse("R&L", 1111111L));
    }

    @Test
    void getGradeByCourseTestStudentNotFoundExceptionTest()
            throws CourseNotFoundException, StudentNotFoundException {
        when(userService.findGradeByStudentNumberAndCourseCode(1111112L, OOP))
                .thenThrow(new StudentNotFoundException("student not found"));
        assertThrows(StudentNotFoundException.class,
                () -> userController.getGradeByCourse(OOP, 1111112L));
    }

    @Test
    void getGpaByStudentTest() throws StudentNotFoundException {
        when(userService.getGpaByStudentNumber(1111111L))
                .thenReturn(8.0);
        assertEquals(new ResponseEntity<>(Map.of("gpa", 8.0), HttpStatus.OK), userController
                .getGpaByStudent(1111111L));
    }

    @Test
    void getGpaByStudentExceptionTest() throws StudentNotFoundException {
        when(userService.getGpaByStudentNumber(1111112L))
                .thenThrow(new StudentNotFoundException("student not found"));
        assertThrows(StudentNotFoundException.class, () -> userController
                .getGpaByStudent(1111112L));
    }

    @Test
    void getGradesOfStudentsTest() {
        List<StudentGrade> expected = List.of(new StudentGrade(1111112L, 10d),
                                                new StudentGrade(2L, 5.3d));
        List<Long> studentNumbers = List.of(1111112L, 2L, 3L);
        when(userService.findGradesOfStudents(OOP, studentNumbers))
                .thenReturn(expected);
        assertEquals(new ResponseEntity<>(expected, HttpStatus.OK),
                userController.getGradesOfStudents(OOP, studentNumbers));
    }

    @Test
    void getRatingsOfStudentsTest() {
        List<StudentRating> expected = List.of(new StudentRating(1111112L, 10f),
                new StudentRating(2L, 5.3f));
        List<Long> studentNumbers = List.of(1111112L, 2L, 3L);
        when(userService.findRatingsOfStudents(studentNumbers))
                .thenReturn(expected);
        assertEquals(new ResponseEntity<>(expected, HttpStatus.OK),
                userController.getRatingsOfStudents(studentNumbers));
    }

    @Test
    void getRatingOfStudentTest() throws StudentNotFoundException {
        StudentRating expected = new StudentRating(1111112L, 10f);
        Long studentNumber = 1111112L;
        when(userService.getRatingOfStudent(studentNumber))
                .thenReturn(expected);
        assertEquals(new ResponseEntity<>(expected, HttpStatus.OK),
                userController.getRatingOfStudent(studentNumber));
    }

    @Test
    void getRatingOfStudentExceptionTest() throws StudentNotFoundException {
        Long studentNumber = 1111112L;
        when(userService.getRatingOfStudent(studentNumber))
                .thenThrow(StudentNotFoundException.class);
        assertThrows(StudentNotFoundException.class,
                () -> userController.getRatingOfStudent(studentNumber));
    }

    @Test
    void rateStudentTest() throws StudentNotFoundException {
        Long studentNumber = 1111112L;
        Float rating = 4.3f;
        userController.rateStudent(studentNumber, rating);
        verify(userService, times(1))
                .rateStudent(studentNumber, rating);

        assertEquals(new ResponseEntity<>(HttpStatus.OK),
                userController.rateStudent(studentNumber, rating));
    }

    @Test
    void rateStudentExceptionTest() throws StudentNotFoundException {
        Long studentNumber = 1111112L;
        doThrow(StudentNotFoundException.class).when(userService)
                .rateStudent(studentNumber, 4.3f);
        assertThrows(StudentNotFoundException.class,
                () -> userController.rateStudent(studentNumber, 4.3f));
    }

    @Test
    void getInfoOfEachTaForCourseTest() {
        List<Student> students = List.of(student);
        when(userService.getInfoOfEachTaForCourse("test"))
                .thenReturn(students);
        assertEquals(new ResponseEntity<>(students, HttpStatus.OK),
                userController.getInfoOfEachTaForCourse("test"));
    }

    @Test
    void addTaRoleTest() throws CourseNotFoundException, StudentNotFoundException {
        String course = "SomeCourse";
        userController.addTaRole(course, student.getStudentNumber());
        verify(userService, times(1))
                .addTaRole(student.getStudentNumber(), course);

        assertEquals(new ResponseEntity<>(HttpStatus.OK),
                userController.addTaRole(course, student.getStudentNumber()));
    }
}
