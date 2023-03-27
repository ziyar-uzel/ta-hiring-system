package nl.tudelft.sem.course.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import nl.tudelft.sem.course.CourseMicroService;
import nl.tudelft.sem.course.entity.Course;
import nl.tudelft.sem.course.service.CourseService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = CourseMicroService.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@DirtiesContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class CourseControllerTest {

    private static final HttpClient client = HttpClient.newBuilder().build();
    private static ObjectMapper objectMapper = new ObjectMapper();
    private transient HttpServletRequest request = mock(HttpServletRequest.class);
    private transient HttpServletResponse response = mock(HttpServletResponse.class);

    private static final String courseCode = "CSE2115";
    private static final String courseCode3 = "CSE2000";
    private final transient String jwtToken = "Bearer "
            + "eyJhbGciOiJIUzUxMiJ9.eyJuZXRpZCI6ImFtZXJldXRhIiwiZW1haWw"
            + "iOiJlbWFpbCIsInN0dWRlbnROdW1iZXIiOjEsImdyYWRlcyI6eyJPT1Ai"
            + "OjguMCwiUiZMIjo3LjV9LCJ0YUNvdXJzZXMiOlsiQ08iXSwiY3VycmVudE"
            + "NvdXJzZXMiOlsiQ0ciLCJTRU0iXSwicm9sZXMiOlsiUk9MRV9TVFVERU5UX"
            + "1NFTSIsIlJPTEVfU1RVREVOVF9DTyIsIlJPTEVfU1RVREVOVF9DRyJdLCJle"
            + "HAiOjE2NDU2NTc4MTd9.-9cVxn9f2V3YxndsBo3Fxm9XhPiZm7lP0RtMrpn"
            + "OnEbGAPF6ylXCHxxW_LGSqeTyuavGj8ZgHoS_6LZEzVaxNA";
    private static final String headerName = "Authorization";

    @MockBean
    private transient CourseService courseService;
    @Autowired
    private transient MockMvc mockMvc;
    @Autowired
    private transient CourseController courseController;
    private transient Course course = new Course();
    private transient Course course2 = new Course();
    private transient Course course3 = new Course();

    private transient List<Course> courseList = new ArrayList<>(Arrays.asList(course2));

    @BeforeAll
    void setUp() {
        // In case we use dates
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS+00:00",
                Locale.ENGLISH));
        objectMapper.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date startDate = new Date();
        int noOfDays = 180;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.DAY_OF_YEAR, noOfDays);
        Date endDate = calendar.getTime();

        Set<Long> hiredStudents = new HashSet<>(Arrays.asList(10L, 20L));
        Set<Long> candidateStudents = new HashSet<>(Arrays.asList(10L, 20L, 30L));
        Set<Long> lecturers = new HashSet<>(Arrays.asList(100L, 200L, 300L));
        final Set<Long> candidateStudentsEmpty = new HashSet<>(Arrays.asList());
        final Set<Long> hiredStudentsEmpty = new HashSet<>(Arrays.asList());


        course.setCourseCode(courseCode);
        course.setStartDate(startDate);
        course.setEndDate(endDate);
        course.setDescription("Very Important Course");
        course.setLecturers(lecturers);
        course.setCandidateStudents(candidateStudents);
        course.setHiredStudents(hiredStudents);
        course.setNumOfStudents(40);

        course3.setCourseCode(courseCode3);
        course3.setStartDate(startDate);
        course3.setEndDate(endDate);
        course3.setDescription("Very Important Course");
        course3.setLecturers(lecturers);
        course3.setCandidateStudents(candidateStudentsEmpty);
        course3.setHiredStudents(hiredStudentsEmpty);
        course3.setNumOfStudents(500);
        course2.setStartDate(endDate);

    }

    /**
     * Generic JSON parser; converts object to JSON.
     *
     * @param o Object of any class to convert to JSON
     * @return JSON representation of object
     */
    public static String toJsonString(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Test
    @SneakyThrows
    public void testCourseCreated() {
        when(courseService.createCourse(eq(course))).thenReturn(course);
        when(request.getHeader(headerName)).thenReturn(jwtToken);
        mockMvc.perform(post("/courses/create")
                .header("Authorization", jwtToken)
                .content(toJsonString(course))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(toJsonString(course)));

        ResponseEntity<Object> response = courseController
                .createCourse(course);
        assertEquals(ResponseEntity.ok(course), response);
    }

    @Test
    @SneakyThrows
    public void testGetCourse() {
        when(courseService.findByCourseCode(eq(courseCode))).thenReturn(course);
        when(request.getHeader(headerName)).thenReturn(jwtToken);
        mockMvc.perform(get("/courses/" + courseCode)
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(toJsonString(course)));

        ResponseEntity<Object> response = courseController
                .getCourse(courseCode);
        assertEquals(ResponseEntity.ok(course), response);
    }

    @Test
    @SneakyThrows
    public void testGetNumberOfStudents() {
        when(courseService.findByCourseCode(eq(courseCode))).thenReturn(course);
        when(request.getHeader(headerName)).thenReturn(jwtToken);
        mockMvc.perform(get("/courses/studentNumber/" + courseCode)
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        assertEquals(new ResponseEntity<>(Map.of("numberOfStudents", course.getNumOfStudents()),
                HttpStatus.OK), courseController
                .getNumberOfStudents(courseCode));
    }


    @Test
    @SneakyThrows
    public void testGetLecturers() {
        when(courseService.findByCourseCode(eq(courseCode))).thenReturn(course);
        when(request.getHeader(headerName)).thenReturn(jwtToken);
        mockMvc.perform(get("/courses/lecturers/" + courseCode)
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        assertEquals(new ResponseEntity<>(Map.of("lecturers", course.getLecturers()),
                HttpStatus.OK), courseController.getLecturers(courseCode));
    }

    @Test
    @SneakyThrows
    public void testGetDescription() {
        when(courseService.findByCourseCode(eq(courseCode))).thenReturn(course);
        when(request.getHeader(headerName)).thenReturn(jwtToken);
        mockMvc.perform(get("/courses/description/" + courseCode)
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        assertEquals(new ResponseEntity<>(Map.of("description", course.getDescription()),
                HttpStatus.OK), courseController
                .getDescription(courseCode));
    }


    @Test
    @SneakyThrows
    public void testGetStartDate() {
        when(courseService.findByCourseCode(eq(courseCode))).thenReturn(course);
        when(request.getHeader(headerName)).thenReturn(jwtToken);
        mockMvc.perform(get("/courses/startDate/" + courseCode)
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        assertEquals(new ResponseEntity<>(Map.of("startDate", course.getStartDate()),
                HttpStatus.OK), courseController
                .getStartDate(courseCode));
    }


    @Test
    @SneakyThrows
    public void testGetAvailableCourses() {
        when(courseService.availableCourses())
                .thenReturn(courseList);
        when(request.getHeader(headerName)).thenReturn(jwtToken);
        mockMvc.perform(get("/courses/availableCourses")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        assertEquals(new ResponseEntity<>(courseList, HttpStatus.OK), courseController
                .getAvailableCourses());
    }

    @Test
    @SneakyThrows
    public void testGetRequiredAmountOfTa() {
        when(courseService.findByCourseCode(eq(courseCode))).thenReturn(course);
        when(request.getHeader(headerName)).thenReturn(jwtToken);
        mockMvc.perform(get("/courses/requiredNumberOfTa/" + courseCode)
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        assertEquals(new ResponseEntity<>(Map.of("requiredTas",  course.getNumOfStudents() / 20),
                HttpStatus.OK), courseController
                .getRequiredNumberOfTas(courseCode));
    }


    @SneakyThrows
    @Test
    public void testAddLecturer() {
        doNothing().when(courseService)
                .addLecturer(isA(Long.class), isA(String.class));
        when(request.getHeader(headerName)).thenReturn(jwtToken);
        mockMvc.perform(put("/courses/add/lecturer/" + courseCode)
                .header("Authorization", jwtToken)
                .param("lecturerId", "111")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());


    }

    @SneakyThrows
    @Test
    public void testRemoveLecturer() {
        doNothing().when(courseService)
                .removeLecturer(isA(Long.class), isA(String.class));
        when(request.getHeader(headerName)).thenReturn(jwtToken);
        mockMvc.perform(delete("/courses/remove/lecturer/" + courseCode)
                .header("Authorization", jwtToken)
                .param("lecturerId", "111")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @SneakyThrows
    @Test
    public void testAddCandidateStudent() {
        doNothing().when(courseService)
                .removeCandidateStudents(isA(Long.class), isA(String.class));
        when(request.getHeader(headerName)).thenReturn(jwtToken);
        mockMvc.perform(put("/courses/add/candidateStudent/" + courseCode)
                .header("Authorization", jwtToken)
                .param("studentNumber", "111")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @SneakyThrows
    @Test
    public void testRemoveCandidate() {
        doNothing().when(courseService)
                .removeCandidateStudents(isA(Long.class), isA(String.class));
        when(request.getHeader(headerName)).thenReturn(jwtToken);
        mockMvc.perform(delete("/courses/remove/candidateStudent/" + courseCode)
                .header("Authorization", jwtToken)
                .param("studentNumber", "111")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());


    }


    @SneakyThrows
    @Test
    public void testAddHiredStudents() {
        doNothing().when(courseService)
                .addHiredStudents(isA(Long.class), isA(String.class));
        when(request.getHeader(headerName)).thenReturn(jwtToken);
        mockMvc.perform(put("/courses/add/hiredStudent/" + courseCode)
                .header("Authorization", jwtToken)
                .param("studentNumber", "111")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @SneakyThrows
    @Test
    public void testRemoveHiredStudents() {
        doNothing().when(courseService)
                .removeHiredStudents(isA(Long.class), isA(String.class));
        when(request.getHeader(headerName)).thenReturn(jwtToken);
        mockMvc.perform(delete("/courses/remove/hiredStudent/" + courseCode)
                .header("Authorization", jwtToken)
                .param("studentNumber", "111")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

    }


    @Test
    @SneakyThrows
    public void testGetAdmissionRate() {
        when(courseService.findByCourseCode(eq(courseCode))).thenReturn(course);
        when(request.getHeader(headerName)).thenReturn(jwtToken);
        mockMvc.perform(get("/courses/admissionRate/" + courseCode)
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        int admissionRate = (course.getHiredStudents().size() * 100) / course
                .getCandidateStudents().size();
        String percentage = admissionRate + "%";
        assertEquals(new ResponseEntity<>(Map.of("admissionRate",  percentage),
                HttpStatus.OK), courseController
                .getAdmissionRate(courseCode));
    }

    @Test
    @SneakyThrows
    public void testGetAdmissionRateNoCandidateStudents() {
        when(courseService.findByCourseCode(eq(courseCode3))).thenReturn(course3);
        when(request.getHeader(headerName)).thenReturn(jwtToken);
        mockMvc.perform(get("/courses/admissionRate/" + courseCode3)
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        assertEquals(new ResponseEntity<>(Map.of("admissionRate",  "-"),
                HttpStatus.OK), courseController
                .getAdmissionRate(courseCode3));
    }

    @Test
    @SneakyThrows
    public void testGetAdmissionRateNoHiredStudents() {
        course.setHiredStudents(new HashSet<>(Arrays.asList()));
        when(courseService.findByCourseCode(eq(courseCode))).thenReturn(course);
        when(request.getHeader(headerName)).thenReturn(jwtToken);
        mockMvc.perform(get("/courses/admissionRate/" + courseCode)
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        assertEquals(new ResponseEntity<>(Map.of("admissionRate",  "-"),
                HttpStatus.OK), courseController
                .getAdmissionRate(courseCode));
    }

}
