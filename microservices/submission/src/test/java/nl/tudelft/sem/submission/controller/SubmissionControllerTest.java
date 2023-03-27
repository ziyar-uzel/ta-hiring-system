package nl.tudelft.sem.submission.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import nl.tudelft.sem.submission.SubmissionMicroService;
import nl.tudelft.sem.submission.controller.dto.SubmissionCreateRequest;
import nl.tudelft.sem.submission.entities.Status;
import nl.tudelft.sem.submission.entities.Submission;
import nl.tudelft.sem.submission.exceptions.CourseServiceErrorException;
import nl.tudelft.sem.submission.exceptions.DuplicateSubmissionException;
import nl.tudelft.sem.submission.exceptions.InsufficientGradeException;
import nl.tudelft.sem.submission.exceptions.LecturerUnauthorizedException;
import nl.tudelft.sem.submission.exceptions.StudentUnauthorizedException;
import nl.tudelft.sem.submission.exceptions.SubmissionClosedException;
import nl.tudelft.sem.submission.exceptions.SubmissionDeadlinePassedException;
import nl.tudelft.sem.submission.exceptions.SubmissionNotFoundException;
import nl.tudelft.sem.submission.exceptions.UserServiceErrorException;
import nl.tudelft.sem.submission.repositories.SubmissionRepository;
import nl.tudelft.sem.submission.services.SubmissionService;
import nl.tudelft.sem.submission.utils.definition.DefaultStrategy;
import nl.tudelft.sem.submission.utils.strategy.RatingStrategy;
import nl.tudelft.sem.submission.utils.strategy.models.RecommendedSubmission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(classes = SubmissionMicroService.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc(addFilters = false)
@DirtiesContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class SubmissionControllerTest {

    private static final String SUBMISSION_URL = "http://localhost:8086";
    private static final HttpClient client = HttpClient.newBuilder().build();
    private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
    private static ObjectMapper objectMapper = new ObjectMapper();

    private final transient SubmissionCreateRequest createRequest = new SubmissionCreateRequest();
    private final transient Long retractStudentNumber = 101010L;
    private static final String courseCode = "CSE2115";
    private static final String studentJWT = "Bearer "
            + "eyJhbGciOiJIUzUxMiJ9.eyJuZXRpZCI6ImFtZXJldXRhIiwiZW1haWw"
            + "iOiJlbWFpbCIsInN0dWRlbnROdW1iZXIiOjEsImdyYWRlcyI6eyJPT1Ai"
            + "OjguMCwiUiZMIjo3LjV9LCJ0YUNvdXJzZXMiOlsiQ08iXSwiY3VycmVudE"
            + "NvdXJzZXMiOlsiQ0ciLCJTRU0iXSwicm9sZXMiOlsiUk9MRV9TVFVERU5UX"
            + "1NFTSIsIlJPTEVfU1RVREVOVF9DTyIsIlJPTEVfU1RVREVOVF9DRyJdLCJle"
            + "HAiOjE2NDU2NTc4MTd9.-9cVxn9f2V3YxndsBo3Fxm9XhPiZm7lP0RtMrpn"
            + "OnEbGAPF6ylXCHxxW_LGSqeTyuavGj8ZgHoS_6LZEzVaxNA";
    private static final String lecturerJWT = "Bearer "
            + "eyJhbGciOiJIUzUxMiJ9.eyJuZXRpZCI6ImxvZmkiLCJlbWFpbCI6ImxvZmlAZW"
            + "1haWwiLCJlbXBsb3llZU51bWJlciI6MSwiY291cnNlcyI6WyJDU0UxNTA1IiwiQ"
            + "1NFMjUyNSIsIkNTRTE1MDAiXSwicm9sZXMiOlsiUk9MRV9MRUNUVVJFUiJdLCJl"
            + "eHAiOjE2NDYxMDIxMTl9.JmeN0omTvOEpE4EZoPALTJB8p6SY63BSGnlPn5eVEE"
            + "v2VQMsl6WpcagO61dyQevlVCmwKDKqfAnEhqk-GNutRQ";


    private static final String headerName = "Authorization";

    @MockBean
    private transient SubmissionService submissionService;
    @MockBean
    private transient SubmissionRepository submissionRepository;
    @Autowired
    private transient MockMvc mockMvc;
    @Autowired
    private transient SubmissionController submissionController;
    private transient Submission submission1;
    private transient Submission submission2;
    private transient Submission submission3;

    private transient RecommendedSubmission recommendedSubmission1;
    private transient RecommendedSubmission recommendedSubmission2;
    private transient RecommendedSubmission recommendedSubmission3;

    private static  class SubmissionBuilder {

        private transient Submission submission;

        /**
         *
         * <p> Method to build an submission. </p>
         */
        public SubmissionBuilder() {
            this.submission = new Submission();
            this.submission.setStatus(Status.PENDING);
            this.submission.setAmountOfHours(0f);
            this.submission.setStudentGpa(10f);
            this.submission.setDescription("Best course ever!");

        }

        public Submission build(Long id) {
            this.submission.setSubmissionId(id);
            return this.submission;
        }

        public SubmissionBuilder withStatus(Status status) {
            this.submission.setStatus(status);
            return this;
        }

        public SubmissionBuilder withCourse(String courseId) {
            this.submission.setCourseCode(courseId);
            return this;
        }

        public SubmissionBuilder withStudentNumber(Long studentNumber) {
            this.submission.setStudentNumber(studentNumber);
            return this;
        }

        public SubmissionBuilder withStudentEmail(String studentEmail) {
            this.submission.setStudentEmail(studentEmail);
            return this;
        }

        public SubmissionBuilder withAmountOfHours(Float hours) {
            this.submission.setAmountOfHours(hours);
            return this;
        }
    }

    @BeforeEach
    void setUp() {

        // In case we use dates
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ",
                Locale.ENGLISH));
        objectMapper.setTimeZone(TimeZone.getTimeZone("UTC"));
        this.createRequest.setStudentEmail("student@tudelft.nl");
        this.createRequest.setStudentNumber(101010L);

        submission1 = new SubmissionBuilder()
                .build(1L);
        submission2 = new SubmissionBuilder()
                .build(2L);
        submission3 = new SubmissionBuilder()
                .build(3L);

        recommendedSubmission1 = new RecommendedSubmission(1f, submission1);
        recommendedSubmission2 = new RecommendedSubmission(2f, submission2);
        recommendedSubmission3 = new RecommendedSubmission(3f, submission3);

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

    private HttpResponse<String> makeRequest(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }


    @Test
    @WithMockUser(roles = {"LECTURER", "STUDENTNUMBER_" + 101010L})
    public void getStatusSuccessTest() throws Exception {
        try {
            when(submissionService.getStatus(any(Long.class))).thenReturn(Status.ACCEPTED);
        } catch (SubmissionNotFoundException e) {
            e.printStackTrace();
        }

        ResponseEntity<Object> response = submissionController
                .getStatus(1L, createRequest.getStudentNumber());
        assertEquals(ResponseEntity.ok(Map.of("status", Status.ACCEPTED)), response);
    }

    @Test
    @WithMockUser(roles = {"LECTURER", "STUDENTNUMBER_" + 101010L})
    public void getStatusFailTest() throws Exception {
        when(submissionService.getStatus(any(Long.class)))
                    .thenThrow(SubmissionNotFoundException.class);

        assertThrows(SubmissionNotFoundException.class,
                () -> submissionController
                        .getStatus(1L, createRequest.getStudentNumber()));
    }

    @Test
    @WithMockUser(roles = {"LECTURER"})
    public void getSubmissionsSuccessTest()
            throws Exception {
        List<Submission> trueSubmissions = List.of(submission1, submission2, submission3);
        when(submissionService.getAllPendingSubmissions("over9000", 10L, lecturerJWT))
                .thenReturn(trueSubmissions);

        mockMvc.perform(get("/submission/getAllPendingSubmissions")
                .header("Authorization", lecturerJWT)
                .param("courseId", courseCode)
                .param("lecturerId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        ResponseEntity<Object> response = submissionController
                .getAllPendingSubmissions("over9000", 10L, lecturerJWT);
        assertEquals(ResponseEntity.ok(trueSubmissions), response);
        verify(submissionService).getAllPendingSubmissions("over9000", 10L, lecturerJWT);
    }

    @Test
    @WithMockUser(roles = {"STUDENTNUMBER_" + 101010L})
    public void testSubmissionsCreated() throws Exception {
        Submission submission = new SubmissionBuilder()
                .withCourse(courseCode)
                .withStudentNumber(createRequest.getStudentNumber())
                .withStudentEmail(createRequest.getStudentEmail())
                .build(1L);

        when(submissionService.createSubmission(eq(createRequest), eq(courseCode),
                any(Date.class), eq(studentJWT))).thenReturn(submission);

        mockMvc.perform(post("/submission/create").header("Authorization", studentJWT)
                    .param("course", courseCode)
                    .content(toJsonString(createRequest))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(toJsonString(submission)));

        ResponseEntity<Object> response = submissionController
                .createSubmission(courseCode, createRequest, studentJWT);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertEquals(ResponseEntity.ok(submission), response);
    }

    @Test
    @WithMockUser(roles = {"STUDENTNUMBER_" + 101010L})
    public void testDuplicateSubmissionException() throws Exception {
        when(submissionService.createSubmission(eq(createRequest), eq(courseCode),
                any(Date.class), eq(studentJWT))).thenThrow(DuplicateSubmissionException.class);

        mockMvc.perform(post("/submission/create").header("Authorization", studentJWT)
                    .param("course", courseCode)
                    .content(toJsonString(createRequest))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()); // Status code = 400

        assertThrows(DuplicateSubmissionException.class,
                () -> submissionController.createSubmission(courseCode, createRequest, studentJWT));
    }

    @Test
    @WithMockUser(roles = {"STUDENTNUMBER_" + 101010L})
    public void testInsufficientGradeException() throws Exception {
        when(submissionService.createSubmission(eq(createRequest), eq(courseCode),
                any(Date.class), eq(studentJWT))).thenThrow(InsufficientGradeException.class);

        mockMvc.perform(post("/submission/create").header("Authorization", studentJWT)
                        .param("course", courseCode)
                        .content(toJsonString(createRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()); // Status code = 403

        assertThrows(InsufficientGradeException.class,
                () -> submissionController.createSubmission(courseCode, createRequest, studentJWT));
    }


    @Test
    @WithMockUser(roles = {"STUDENTNUMBER_" + 101010L})
    public void testSubmissionDeadlinePassedException() throws Exception {
        when(submissionService.createSubmission(eq(createRequest), eq(courseCode),
                any(Date.class), eq(studentJWT)))
                .thenThrow(SubmissionDeadlinePassedException.class);

        mockMvc.perform(post("/submission/create")
                        .header("Authorization", studentJWT)
                        .param("course", courseCode)
                        .content(toJsonString(createRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()); // Status code = 400
        assertThrows(SubmissionDeadlinePassedException.class,
                () -> submissionController.createSubmission(courseCode, createRequest, studentJWT));
    }

    @Test
    @WithMockUser(roles = {"STUDENTNUMBER_" + 101010L})
    public void testSubmissionCourseServiceException() throws Exception {
        when(submissionService.createSubmission(eq(createRequest), eq(courseCode),
                any(Date.class), eq(studentJWT)))
                .thenThrow(new CourseServiceErrorException("Internal service error!"));

        mockMvc.perform(post("/submission/create")
                        .header("Authorization", studentJWT)
                        .param("course", courseCode)
                        .content(Objects.requireNonNull(toJsonString(createRequest)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(roles = {"STUDENTNUMBER_" + 101010L})
    public void testSubmissionUserServiceException() throws Exception {
        when(submissionService.createSubmission(eq(createRequest), eq(courseCode),
                any(Date.class), eq(studentJWT)))
                .thenThrow(new UserServiceErrorException("Internal service error!"));

        mockMvc.perform(post("/submission/create")
                .header("Authorization", studentJWT)
                        .param("course", courseCode)
                        .content(toJsonString(createRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(roles = {"LECTURER"})
    public void getSubmissionsUnauthorisedTest() throws Exception {
        when(submissionService
                .getAllPendingSubmissions(eq(courseCode), any(Long.class), eq(lecturerJWT)))
                .thenThrow(new LecturerUnauthorizedException("test"));


        mockMvc.perform(get("/submission/getAllPendingSubmissions")
                        .header("Authorization", lecturerJWT)
                        .param("courseId", courseCode)
                        .param("lecturerId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());


        assertThrows(LecturerUnauthorizedException.class,
                () -> submissionController.getAllPendingSubmissions(courseCode, 1L, lecturerJWT));
    }

    @Test
    @WithMockUser(roles = {"LECTURER"})
    public void getSubmissionsCourseServiceErrorTest() throws Exception {

        when(submissionService.getAllPendingSubmissions(any(String.class),
                eq(1L), eq(lecturerJWT)))
                .thenThrow(new CourseServiceErrorException("error"));

        mockMvc.perform(get("/submission/getAllPendingSubmissions")
                        .header("Authorization", lecturerJWT)
                        .param("courseId", courseCode)
                        .param("lecturerId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

    }

    @Test
    @WithMockUser(roles = {"LECTURER"})
    public void testAcceptedSubmission() throws Exception {
        Submission submission = new SubmissionBuilder()
                .withCourse(courseCode)
                .withStudentNumber(createRequest.getStudentNumber())
                .withStudentEmail(createRequest.getStudentEmail())
                .withStatus(Status.ACCEPTED)
                .build(1L);

        when(submissionService.acceptSubmission(69L, 2L, lecturerJWT)).thenReturn(submission);

        mockMvc.perform(put("/submission/accept/{submissionId}", 69L)
                        .header("Authorization", lecturerJWT)
                        .param("lecturerId", String.valueOf(2L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(toJsonString(submission)));
        ResponseEntity<Object> response
                = submissionController.acceptSubmission(69L, 2L, lecturerJWT);
        assertEquals(ResponseEntity.ok(submission), response);
    }

    @Test
    @WithMockUser(roles = {"LECTURER"})
    public void testAcceptNotFoundSubmission() throws Exception {
        when(submissionService.acceptSubmission(1L, 2L, lecturerJWT))
                .thenThrow(SubmissionNotFoundException.class);

        mockMvc.perform(put("/submission/accept/{submissionId}", 1L)
                        .header("Authorization", lecturerJWT)
                    .param("lecturerId", String.valueOf(2L))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        assertThrows(SubmissionNotFoundException.class,
                () -> submissionController.acceptSubmission(1L, 2L, lecturerJWT));
    }

    @Test
    @WithMockUser(roles = {"LECTURER"})
    public void testAcceptClosedSubmission() throws Exception {
        when(submissionService.acceptSubmission(1L, 2L, lecturerJWT))
                .thenThrow(SubmissionClosedException.class);

        mockMvc.perform(put("/submission/accept/{submissionId}", 1L)
                        .header("Authorization", lecturerJWT)
                        .param("lecturerId", String.valueOf(2L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        assertThrows(SubmissionClosedException.class,
                () -> submissionController.acceptSubmission(1L, 2L, lecturerJWT));
    }


    @Test
    @WithMockUser(roles = {"LECTURER"})
    public void testLecturerUnauthorizedException() throws Exception {
        when(submissionService.acceptSubmission(90L, 2L, lecturerJWT))
                .thenThrow(LecturerUnauthorizedException.class);

        mockMvc.perform(put("/submission/accept/{submissionId}", 90L)
                        .header("Authorization", lecturerJWT)
                        .param("lecturerId", String.valueOf(2L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        assertThrows(LecturerUnauthorizedException.class,
                () -> submissionController.acceptSubmission(90L, 2L, lecturerJWT));
    }

    @Test
    @WithMockUser(roles = {"STUDENTNUMBER_" + 101010L})
    public void testRetractedSubmission() throws Exception {
        when(submissionService.retractSubmission(eq(1L),
                eq(retractStudentNumber), any(Date.class), eq(studentJWT)))
                .thenReturn(true);

        mockMvc.perform(put("/submission/retract/{submissionId}", 1L)
                        .header("Authorization", studentJWT)
                    .param("studentNumber", String.valueOf(retractStudentNumber))
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(toJsonString(Map.of("retracted", true))));
        ResponseEntity<Object> response = submissionController
                .retractSubmission(1L, retractStudentNumber, studentJWT);
        assertEquals(ResponseEntity.ok(Map.of("retracted", true)), response);
    }

    @Test
    @WithMockUser(roles = {"STUDENTNUMBER_" + 101010L})
    public void testSubmissionNotRetracted() throws Exception {
        when(submissionService.retractSubmission(eq(1L),
                eq(retractStudentNumber), any(Date.class), eq(studentJWT)))
                .thenReturn(false);

        mockMvc.perform(put("/submission/retract/{submissionId}", 1L)
                        .with(user("student").roles("STUDENT"))
                        .header("Authorization", studentJWT)
                        .param("studentNumber", String.valueOf(retractStudentNumber))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(toJsonString(Map.of("retracted", false))));
        ResponseEntity<Object> response = submissionController
                .retractSubmission(1L, retractStudentNumber, studentJWT);
        assertEquals(ResponseEntity.ok(Map.of("retracted", false)), response);
    }

    @Test
    @WithMockUser(roles = {"STUDENTNUMBER_" + 101010L})
    public void testRetractSubmissionNotFound() throws Exception {
        when(submissionService.retractSubmission(eq(1L),
                eq(retractStudentNumber), any(Date.class), eq(studentJWT)))
                .thenThrow(SubmissionNotFoundException.class);

        mockMvc.perform(put("/submission/retract/{submissionId}", 1L)
                        .header("Authorization", studentJWT)
                        .param("studentNumber", String.valueOf(retractStudentNumber))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        assertThrows(SubmissionNotFoundException.class,
                () -> submissionController.retractSubmission(1L, retractStudentNumber, studentJWT));
    }

    @Test
    @WithMockUser(roles = {"STUDENTNUMBER_" + 101010L})
    public void testRetractStudentUnauthorizedException() throws Exception {
        when(submissionService.retractSubmission(eq(1L),
                eq(retractStudentNumber), any(Date.class), eq(studentJWT)))
                .thenThrow(StudentUnauthorizedException.class);

        mockMvc.perform(put("/submission/retract/{submissionId}", 1L)
                        .header("Authorization", studentJWT)
                    .param("studentNumber", String.valueOf(retractStudentNumber))
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        assertThrows(StudentUnauthorizedException.class,
                () -> submissionController.retractSubmission(1L, retractStudentNumber, studentJWT));
    }

    @Test
    @WithMockUser(roles = {"STUDENTNUMBER_" + 101010L})
    public void testRetractCourseServiceException() throws Exception {
        when(submissionService.retractSubmission(eq(1L),
                eq(retractStudentNumber), any(Date.class), eq(studentJWT)))
                .thenThrow(new CourseServiceErrorException("Internal service error"));

        mockMvc.perform(put("/submission/retract/{submissionId}", 1L)
                        .header("Authorization", studentJWT)
                    .param("studentNumber", String.valueOf(retractStudentNumber))
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

    }


    @Test
    @WithMockUser(roles = {"LECTURER"})
    public void getRecommendationsForCourseTest() throws Exception {
        List<RecommendedSubmission> trueSubmissions = List.of(recommendedSubmission1,
                recommendedSubmission2, recommendedSubmission3);
        when(submissionService.getRecommendationsForCourse(eq(courseCode),
                eq(1L), any(DefaultStrategy.class), eq(studentJWT)))
                .thenReturn(trueSubmissions);

        mockMvc.perform(get("/submission/getRecommendationsForCourse")
                    .header("Authorization", studentJWT)
                    .param("courseId", courseCode)
                    .param("lecturerId", String.valueOf(1L))
                    .param("recommendationType", String.valueOf(0))
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new RatingStrategy())))
                .andExpect(status().isOk())
                .andExpect(content().json("["
                        + toJsonString(recommendedSubmission1)
                        + "," + toJsonString(recommendedSubmission2)
                        + "," + toJsonString(recommendedSubmission3)
                        + "]"
                ));
    }


}
