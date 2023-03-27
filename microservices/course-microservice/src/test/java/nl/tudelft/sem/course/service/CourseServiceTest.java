package nl.tudelft.sem.course.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.SneakyThrows;
import nl.tudelft.sem.course.CourseMicroService;
import nl.tudelft.sem.course.entity.Course;
import nl.tudelft.sem.course.exceptions.CourseNotFoundException;
import nl.tudelft.sem.course.exceptions.TaRatioExceededException;
import nl.tudelft.sem.course.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(classes = CourseMicroService.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@DirtiesContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CourseServiceTest {

    private final transient CourseRepository courseRepository
            = Mockito.mock(CourseRepository.class);

    private transient CourseService courseService = new CourseService(courseRepository);
    private static final String courseCode3 = "CSE2200";
    private static final String courseCode = "CSE2000";
    private static final String invalidCourseCode = "CSSSS";
    private transient Course course = new Course();
    private transient Course course2 = new Course();
    private transient Course course3 = new Course();

    private List<Course> courseList = new ArrayList<>(Arrays.asList(course2));


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

        course.setCourseCode(courseCode);
        course.setStartDate(startDate);
        course.setEndDate(endDate);
        course.setDescription("Very Important Course");
        course.setLecturers(lecturers);
        course.setCandidateStudents(candidateStudents);
        course.setHiredStudents(hiredStudents);
        course.setNumOfStudents(41);

        course3.setCourseCode(courseCode3);
        course3.setStartDate(startDate);
        course3.setEndDate(endDate);
        course3.setDescription("Very Important Course");
        course3.setLecturers(lecturers);
        course3.setCandidateStudents(candidateStudents);
        course3.setHiredStudents(hiredStudents);
        course3.setNumOfStudents(500);
        course2.setStartDate(endDate);
    }

    @SneakyThrows
    @Test
    public void findByCourseIdTest() {
        when(courseRepository.findByCourseCode(courseCode))
                .thenReturn(Optional.ofNullable(course));
        assertEquals(course, courseService
                .findByCourseCode(courseCode));
    }



    @SneakyThrows
    @Test
    public void createCourseTest() {
        when(courseRepository.saveAndFlush(course))
                .thenReturn(course);
        assertEquals(course, courseService
                .createCourse(course));
    }


    @Test
    public void courseNotFoundExceptionTest() {
        when(courseRepository.findByCourseCode(invalidCourseCode)).thenReturn(Optional.empty());

        assertThrows(CourseNotFoundException.class,  () -> courseService
                .findByCourseCode(invalidCourseCode));
    }


    @SneakyThrows
    @Test
    public void testHiredStudent() {
        when(courseRepository.findById(course3.getCourseCode()))
                .thenReturn(Optional.ofNullable(course3));
        courseService.addHiredStudents(111L, course3.getCourseCode());

        Mockito.verify(courseRepository, atLeast(1)).saveAndFlush(course3);
    }

    @SneakyThrows
    @Test
    public void testHiredStudentException() {
        when(courseRepository.findById(course.getCourseCode()))
                .thenReturn(Optional.ofNullable(course));
        courseService.addHiredStudents(111L, course.getCourseCode());

        assertThrows(TaRatioExceededException.class, () -> courseService
                .addHiredStudents(111L, courseCode));
    }

    @SneakyThrows
    @Test
    public void testAddCandidateStudent() {
        when(courseRepository.findById(course3.getCourseCode()))
                .thenReturn(Optional.ofNullable(course3));
        courseService.addCandidateStudents(111L, course3.getCourseCode());

        Mockito.verify(courseRepository, atLeast(1)).saveAndFlush(course3);
    }

    @SneakyThrows
    @Test
    public void testAddLecturer() {
        when(courseRepository.findById(courseCode3))
                .thenReturn(Optional.ofNullable(course3));
        courseService.addLecturer(1111L, courseCode3);

        Mockito.verify(courseRepository, atLeast(1)).saveAndFlush(course3);
    }

    @SneakyThrows
    @Test
    public void testRemoveHiredStudent() {
        when(courseRepository.findById(course3.getCourseCode()))
                .thenReturn(Optional.ofNullable(course3));
        courseService.removeHiredStudents(10L, course3.getCourseCode());

        Mockito.verify(courseRepository, atLeast(1)).saveAndFlush(course3);
    }

    @SneakyThrows
    @Test
    public void testRemoveCandidateStudent() {
        when(courseRepository.findById(course3.getCourseCode()))
                .thenReturn(Optional.ofNullable(course3));
        courseService.removeCandidateStudents(10L, course3.getCourseCode());

        Mockito.verify(courseRepository, atLeast(1)).saveAndFlush(course3);
    }

    @SneakyThrows
    @Test
    public void testRemoveLecturer() {
        when(courseRepository.findById(course3.getCourseCode()))
                .thenReturn(Optional.ofNullable(course3));
        courseService.removeLecturer(100L, course3.getCourseCode());

        Mockito.verify(courseRepository, atLeast(1)).saveAndFlush(course3);
    }

    @SneakyThrows
    @Test
    public void addHiredStudentCourseNotFoundException() {
        when(courseRepository.findByCourseCode(invalidCourseCode)).thenReturn(Optional.empty());
        assertThrows(CourseNotFoundException.class,  () -> courseService
                .addHiredStudents(111L, invalidCourseCode));
    }

    @SneakyThrows
    @Test
    public void addCandidateCourseNotFoundException() {
        when(courseRepository.findByCourseCode(invalidCourseCode)).thenReturn(Optional.empty());
        assertThrows(CourseNotFoundException.class,  () -> courseService
                .addCandidateStudents(111L, invalidCourseCode));
    }

    @SneakyThrows
    @Test
    public void addLecturerCourseNotFoundException() {
        when(courseRepository.findByCourseCode(invalidCourseCode)).thenReturn(Optional.empty());
        assertThrows(CourseNotFoundException.class,  () -> courseService
                .addLecturer(111L, invalidCourseCode));
    }


    @SneakyThrows
    @Test
    public void removeHiredStudentCourseNotFoundException() {
        when(courseRepository.findByCourseCode(invalidCourseCode)).thenReturn(Optional.empty());
        assertThrows(CourseNotFoundException.class,  () -> courseService
                .removeHiredStudents(111L, invalidCourseCode));
    }

    @SneakyThrows
    @Test
    public void removeCandidateCourseNotFoundException() {
        when(courseRepository.findByCourseCode(invalidCourseCode)).thenReturn(Optional.empty());
        assertThrows(CourseNotFoundException.class,  () -> courseService
                .removeCandidateStudents(111L, invalidCourseCode));
    }

    @SneakyThrows
    @Test
    public void removeLecturerCourseNotFoundException() {
        when(courseRepository.findByCourseCode(invalidCourseCode)).thenReturn(Optional.empty());
        assertThrows(CourseNotFoundException.class,  () -> courseService
                .removeLecturer(111L, invalidCourseCode));
    }
}
