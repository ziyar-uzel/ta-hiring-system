package nl.tudelft.cse.sem.user.services;

import nl.tudelft.cse.sem.user.exceptions.CourseNotFoundException;
import nl.tudelft.cse.sem.user.exceptions.StudentNotFoundException;
import nl.tudelft.cse.sem.user.models.Student;
import nl.tudelft.cse.sem.user.models.StudentGrade;
import nl.tudelft.cse.sem.user.models.StudentRating;
import nl.tudelft.cse.sem.user.models.User;
import nl.tudelft.cse.sem.user.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    private final transient UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final transient UserService userService = new UserService(userRepository);

    private transient Student student = new Student();
    private transient Student student2 = new Student();
    private transient Student student3 = new Student();
    private static String bullshitPmdErrors = "OOP";

    @BeforeEach
    void setUp() {
        student.setName("Andrew");
        student.setEmail("andrew@email.com");
        student.setPassword("password");
        student.setNetId("amereuta");
        student.setStudentNumber((long) 1111111);
        student.setTaCourses(Set.of(bullshitPmdErrors));
        student.setCurrentCourses(Set.of("AD"));
        student.setGrades(Map.of(bullshitPmdErrors, 8.0));
        student.setRating(4.55f);
        student.setNumberOfRatings(2);
        student.setRatingSum(9.1f);

        student2.setName("Matei");
        student2.setEmail("aaa@gmail.com");
        student2.setPassword("pass");
        student2.setNetId("aa");
        student2.setStudentNumber((long) 100000);
        student2.setTaCourses(Set.of(bullshitPmdErrors));
        student2.setCurrentCourses(Set.of("AD"));
        student2.setGrades(Map.of(bullshitPmdErrors, 9.0));
        student2.setRating(3.33f);
        student2.setNumberOfRatings(1);
        student2.setRatingSum(3.33f);

        student3.setName("Vasile");
        student3.setEmail("ggg@gmail.com");
        student3.setPassword("pass");
        student3.setNetId("vas");
        student3.setStudentNumber((long) 202313);
        student3.setTaCourses(Set.of("ADS"));
        student3.setCurrentCourses(Set.of("AD"));
        student3.setGrades(Map.of("ADS", 9.0));
    }

    @Test
    void loadByUsernameTest() {
        when(userRepository.findById("amereuta"))
                .thenReturn(Optional.of(student));

        User user = userService.loadUserByUsername("amereuta");
        assertEquals(student, user);
    }

    @Test
    void loadByUsernameExceptionTest() {
        when(userRepository.findById("I just don't exist"))
                .thenThrow(new UsernameNotFoundException("Name not found"));

        assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername("I just don't exist"));
    }

    @Test
    void findGradeByStudentNumberAndCourseTest()
            throws CourseNotFoundException, StudentNotFoundException {
        when(userRepository.findStudentByStudentNumber(1111111L))
                .thenReturn(Optional.of(student));
        assertEquals(8.0, userService
                .findGradeByStudentNumberAndCourseCode(1111111L, bullshitPmdErrors));
    }

    @Test
    void findGradeByStudentNumberAndCourseStudentExceptionTest() {
        when(userRepository.findStudentByStudentNumber(1111112L))
                .thenReturn(Optional.empty());
        assertThrows(StudentNotFoundException.class, () -> userService
                .findGradeByStudentNumberAndCourseCode(1111112L, bullshitPmdErrors));
    }

    @Test
    void findGradeByStudentNumberAndCourseCourseExceptionTest() {
        when(userRepository.findStudentByStudentNumber(1111111L))
                .thenReturn(Optional.of(student));
        assertThrows(CourseNotFoundException.class, () -> userService
                .findGradeByStudentNumberAndCourseCode(1111111L, "R&L"));
    }

    @Test
    void getGpaByStudentNumberTest() throws StudentNotFoundException {
        when(userRepository.findStudentByStudentNumber(1111111L))
                .thenReturn(Optional.of(student));
        assertEquals(8.0, userService.getGpaByStudentNumber(1111111L));
    }

    @Test
    void getGpaByStudentNumberExceptionTest() {
        when(userRepository.findStudentByStudentNumber(1111112L))
                .thenReturn(Optional.empty());
        assertThrows(StudentNotFoundException.class, () -> userService
                .getGpaByStudentNumber(1111112L));
    }

    @Test
    void findGradesOfStudentsResultTest() {
        List<Student> students = List.of(student, student2, student3);
        List<Long> studentNumbers = List.of(student.getStudentNumber(),
                student2.getStudentNumber(), student3.getStudentNumber());
        List<StudentGrade> studentGrades =
            List.of(new StudentGrade(student.getStudentNumber(),
                            student.getGrades().get(bullshitPmdErrors)),
                    new StudentGrade(student2.getStudentNumber(),
                            student2.getGrades().get(bullshitPmdErrors)));

        when(userRepository.findStudentsByStudentNumbers(studentNumbers))
                .thenReturn(students);

        List<StudentGrade> response =
                userService.findGradesOfStudents(bullshitPmdErrors, studentNumbers);

        assertTrue(response.size() == studentGrades.size()
                && response.containsAll(studentGrades));


    }

    @Test
    void findGradesOfStudentsEmptyTest() {
        List<Student> students = List.of(student, student2, student3);
        List<Long> studentNumbers =
                List.of(student.getStudentNumber(),
                        student2.getStudentNumber(),
                        student3.getStudentNumber());
        List<StudentGrade> studentGrades =
                List.of();

        when(userRepository.findStudentsByStudentNumbers(studentNumbers))
                .thenReturn(students);

        List<StudentGrade> response =
                userService.findGradesOfStudents("aaa", studentNumbers);

        assertTrue(response.size() == studentGrades.size()
                && response.containsAll(studentGrades));
    }


    @Test
    void findRatingsOfStudentsResultTest() {
        List<Student> students = List.of(student, student2, student3);
        List<Long> studentNumbers = List.of(student.getStudentNumber(),
                student2.getStudentNumber(),
                student3.getStudentNumber());
        List<StudentRating> studentRatings =
                List.of(new StudentRating(student.getStudentNumber(),
                                student.getRating()),
                        new StudentRating(student2.getStudentNumber(),
                                student2.getRating()),
                        new StudentRating(student3.getStudentNumber(),
                                student3.getRating()));
        when(userRepository.findStudentsByStudentNumbers(studentNumbers))
                .thenReturn(students);

        List<StudentRating> response =
                userService.findRatingsOfStudents(studentNumbers);

        assertTrue(response.size() == studentRatings.size()
                && response.containsAll(studentRatings));
    }

    @Test
    void findRatingsOfStudentsEmptyTest() {
        List<Student> students = List.of();
        List<Long> studentNumbers = List.of();
        List<StudentRating> studentRatings =
                List.of();
        when(userRepository.findStudentsByStudentNumbers(studentNumbers))
                .thenReturn(students);

        List<StudentRating> response = userService.findRatingsOfStudents(studentNumbers);

        assertTrue(response.size() == studentRatings.size()
                && response.containsAll(studentRatings));
    }

    @Test
    void findRatingOfStudentSuccessTest() throws StudentNotFoundException {
        when(userRepository.findStudentByStudentNumber(student.getStudentNumber()))
                .thenReturn(Optional.of(student));
        StudentRating expectedRating =
                new StudentRating(student.getStudentNumber(),
                        student.getRating());
        StudentRating response =
                userService.getRatingOfStudent(student.getStudentNumber());
        assertEquals(response, expectedRating);
    }

    @Test
    void findRatingOfStudentNotFoundTest() {
        when(userRepository.findStudentByStudentNumber(1L))
                .thenReturn(Optional.empty());
        assertThrows(StudentNotFoundException.class,
                () -> userService.getRatingOfStudent(student.getStudentNumber()));
    }

    @Test
    void rateStudentAlreadyRatedTest() throws StudentNotFoundException {
        when(userRepository
                .findStudentByStudentNumber(student.getStudentNumber()))
                .thenReturn(Optional.of(student));
        userService.rateStudent(student.getStudentNumber(), 4f);

        assertEquals(student.getNumberOfRatings(), 3);
        assertEquals(student.getRatingSum(), 9.1f + 4f);
        assertEquals(student.getRating(), (float) ((9.1 + 4) / 3), 0.000001);
    }

    @Test
    void rateStudentNotRatedYetTest() throws StudentNotFoundException {
        when(userRepository
                .findStudentByStudentNumber(student3.getStudentNumber()))
                .thenReturn(Optional.of(student3));
        userService.rateStudent(student3.getStudentNumber(), 3.5f);

        assertEquals(student3.getNumberOfRatings(), 1);
        assertEquals(student3.getRatingSum(), 3.5f);
        assertEquals(student3.getRating(), 3.5f);
    }

    @Test
    void rateStudentNonExistentTest() {
        when(userRepository
                .findStudentByStudentNumber(student3.getStudentNumber()))
                .thenReturn(Optional.empty());
        assertThrows(StudentNotFoundException.class,
                () -> userService.rateStudent(student3.getStudentNumber(), 3.5f));
    }


    @Test
    void getInfoOfEachTaForCourseTest() {
        when(userRepository.findTasForCourse("CSE1100")).thenReturn(List.of(student));
        assertEquals(student, userService.getInfoOfEachTaForCourse("CSE1100").get(0));
    }

}
