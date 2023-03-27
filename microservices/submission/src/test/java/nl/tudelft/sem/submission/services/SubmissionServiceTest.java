package nl.tudelft.sem.submission.services;

import nl.tudelft.sem.submission.SubmissionMicroService;
import nl.tudelft.sem.submission.communication.CourseCommunication;
import nl.tudelft.sem.submission.communication.UserCommunication;
import nl.tudelft.sem.submission.controller.dto.SubmissionCreateRequest;
import nl.tudelft.sem.submission.entities.Notification;
import nl.tudelft.sem.submission.entities.Status;
import nl.tudelft.sem.submission.entities.StudentGrade;
import nl.tudelft.sem.submission.entities.StudentRating;
import nl.tudelft.sem.submission.entities.Submission;
import nl.tudelft.sem.submission.exceptions.*;
import nl.tudelft.sem.submission.repositories.SubmissionRepository;
import nl.tudelft.sem.submission.utils.strategy.GradeStrategy;
import nl.tudelft.sem.submission.utils.strategy.RatingStrategy;
import nl.tudelft.sem.submission.utils.strategy.models.RecommendedSubmission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = SubmissionMicroService.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@DirtiesContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class SubmissionServiceTest {

    @Autowired
    private transient SubmissionService submissionService;
    @MockBean
    private transient SubmissionRepository submissionRepository;
    @MockBean
    private transient CourseCommunication courseCommunication;
    @MockBean
    private transient UserCommunication userCommunication;
    @Autowired
    private transient MockMvc mockMvc;


    private final transient SubmissionCreateRequest createRequest
            = new SubmissionCreateRequest();
    private static final String courseCode = "CSE2115";
    private static final Long lectureId = 42L;
    private transient Submission submission;
    private transient Submission submission1;
    private transient Submission submission2;

    private final transient Long retractStudentNumber = 101010L;
    private final transient Date retractDate = new Date(2021, 11, 1);

    private static final String jwtToken = "Bearer "
            + "eyJhbGciOiJIUzUxMiJ9.eyJuZXRpZCI6ImFtZXJldXRhIiwiZW1haWw"
            + "iOiJlbWFpbCIsInN0dWRlbnROdW1iZXIiOjEsImdyYWRlcyI6eyJPT1Ai"
            + "OjguMCwiUiZMIjo3LjV9LCJ0YUNvdXJzZXMiOlsiQ08iXSwiY3VycmVudE"
            + "NvdXJzZXMiOlsiQ0ciLCJTRU0iXSwicm9sZXMiOlsiUk9MRV9TVFVERU5UX"
            + "1NFTSIsIlJPTEVfU1RVREVOVF9DTyIsIlJPTEVfU1RVREVOVF9DRyJdLCJle"
            + "HAiOjE2NDU2NTc4MTd9.-9cVxn9f2V3YxndsBo3Fxm9XhPiZm7lP0RtMrpn"
            + "OnEbGAPF6ylXCHxxW_LGSqeTyuavGj8ZgHoS_6LZEzVaxNA";

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

    private static class NotificationBuilder {

        private transient Notification notification;

        /**
         *
         * <p> Method to build a notification. </p>
         */
        public NotificationBuilder() {
            this.notification = new Notification();
            this.notification.setStatus(Status.PENDING);
            this.notification.setNotifiedStudentNumber(1234L);
            this.notification.setNotifiedCourseId("1234");
            this.notification.setNotifiedEmail("example1234554321@gmail.com");

        }

        public Notification build(Long id) {
            this.notification.setId(id);
            return this.notification;
        }

        public NotificationBuilder withStatus(Status status) {
            this.notification.setStatus(status);
            return this;
        }

        public NotificationBuilder withNotifiedStudentNumber(Long notifiedStudentNumber) {
            this.notification.setNotifiedStudentNumber(notifiedStudentNumber);
            return this;
        }

        public NotificationBuilder withNotifiedEmail(String notifiedEmail) {
            this.notification.setNotifiedEmail(notifiedEmail);
            return this;
        }

        public NotificationBuilder withNotifiedCourseId(String notifiedCourseId) {
            this.notification.setNotifiedCourseId(notifiedCourseId);
            return this;
        }
    }

    @BeforeEach
    void setup() {

        this.createRequest.setStudentEmail("student@tudelft.nl");
        this.createRequest.setStudentNumber(101010L);
        this.submission = new SubmissionBuilder()
                .withCourse(courseCode)
                .withStudentNumber(createRequest.getStudentNumber())
                .withStudentEmail(createRequest.getStudentEmail())
                .withStatus(Status.PENDING)
                .build(1L);

        submission1 = new SubmissionBuilder()
                .withStatus(Status.ACCEPTED)
                .withStudentNumber(1000L)
                .build(2L);
        submission2 = new SubmissionBuilder()
                .withStudentNumber(2000L)
                .build(3L);
    }


    @Test
    @WithMockUser(roles = {"LECTURER", "STUDENTNUMBER_" + 101010L})
    public void getStatusSuccessTest() throws SubmissionNotFoundException {
        when(submissionRepository.getBySubmissionId(any(Long.class)))
                .thenReturn(Optional.of(submission));

        Status result = submissionService.getStatus(10L);

        assertEquals(result, Status.PENDING);
        verify(submissionRepository).getBySubmissionId(10L);
    }

    @Test
    @WithMockUser(roles = {"LECTURER", "STUDENTNUMBER_" + 101010L})
    public void getStatusFailTest() {
        when(submissionRepository.getBySubmissionId(any(Long.class)))
                .thenReturn(Optional.empty());

        assertThrows(SubmissionNotFoundException.class, () -> submissionService.getStatus(10L));
        verify(submissionRepository).getBySubmissionId(10L);
    }


    @Test
    @WithMockUser(roles = {"STUDENTNUMBER_" + 101010L})
    public void testCreatedSubmission() throws Exception {
        when(submissionRepository.getByStudentNumberAndCourseCode(
                createRequest.getStudentNumber(), courseCode)).thenReturn(Optional.empty());
        when(userCommunication.getGradeByCourse(createRequest
                .getStudentNumber(), courseCode, jwtToken))
                .thenReturn(9.5f);
        when(courseCommunication.getCourseStartDate(courseCode, jwtToken))
                .thenReturn(new Date(2022, 3, 25));
        when(userCommunication.getGpaByStudent(createRequest.getStudentNumber(), jwtToken))
                .thenReturn(submission.getStudentGpa());
        when(courseCommunication.getCourseDescription(courseCode, jwtToken))
                .thenReturn(submission.getDescription());
        when(submissionRepository.save(any(Submission.class))).thenReturn(submission);
        when(submissionRepository.getBySubmissionId(submission.getSubmissionId()))
            .thenReturn(Optional.of(submission));
        Submission submissionCreated = submissionService
                .createSubmission(createRequest, courseCode,
                        new Date(2021, 12, 25), jwtToken);
        assertEquals(submission, submissionCreated);
        verify(submissionRepository, times(1)).save(submission);
    }

    @Test
    @WithMockUser(roles = {"STUDENTNUMBER_" + 101010L})
    public void testCreatedSubmissionJustPasssed() throws Exception {
        when(submissionRepository.getByStudentNumberAndCourseCode(
                createRequest.getStudentNumber(), courseCode)).thenReturn(Optional.empty());
        when(userCommunication.getGradeByCourse(createRequest
                .getStudentNumber(), courseCode, jwtToken))
                .thenReturn(5.75f);
        when(courseCommunication.getCourseStartDate(courseCode, jwtToken))
                .thenReturn(new Date(2022, 3, 25));
        when(userCommunication.getGpaByStudent(createRequest.getStudentNumber(), jwtToken))
                .thenReturn(submission.getStudentGpa());
        when(courseCommunication.getCourseDescription(courseCode, jwtToken))
                .thenReturn(submission.getDescription());
        when(submissionRepository.save(any(Submission.class))).thenReturn(submission);
        when(submissionRepository.getBySubmissionId(submission.getSubmissionId()))
                .thenReturn(Optional.of(submission));
        Submission submissionCreated = submissionService
                .createSubmission(createRequest, courseCode,
                        new Date(2021, 12, 25), jwtToken);
        assertEquals(submission, submissionCreated);
        verify(submissionRepository, times(1)).save(submission);
    }


    @Test
    @WithMockUser(roles = {"STUDENTNUMBER_" + 101010L})
    public void testDuplicateSubmission() throws Exception {
        when(submissionRepository.getByStudentNumberAndCourseCode(
                createRequest.getStudentNumber(), courseCode))
                .thenReturn(Optional.of(submission));

        assertThrows(DuplicateSubmissionException.class, () -> submissionService
                .createSubmission(createRequest,
                        courseCode,  new Date(2021, 12, 25), jwtToken));
        verify(courseCommunication, never()).getCourseStartDate(courseCode, jwtToken);
        verify(courseCommunication, never()).getCourseDescription(courseCode, jwtToken);
        verify(userCommunication, never())
                .getGradeByCourse(createRequest.getStudentNumber(),
                courseCode, jwtToken);
        verify(userCommunication, never()).getGpaByStudent(createRequest
                .getStudentNumber(), jwtToken);
        verify(submissionRepository, never()).save(any(Submission.class));

    }

    @Test
    @WithMockUser(roles = {"STUDENTNUMBER_" + 101010L})
    public void testInsufficientGrade() throws Exception {
        when(submissionRepository.getByStudentNumberAndCourseCode(
                createRequest.getStudentNumber(), courseCode))
                .thenReturn(Optional.empty());
        when(userCommunication.getGradeByCourse(createRequest
                .getStudentNumber(), courseCode, jwtToken))
                .thenReturn(5f);
        when(courseCommunication.getCourseStartDate(courseCode, jwtToken))
                .thenReturn(new Date(2022, 12, 29));

        assertThrows(InsufficientGradeException.class, () -> submissionService
                .createSubmission(createRequest,
                        courseCode,  new Date(2021, 12, 25), jwtToken));
        verify(userCommunication, times(1))
                .getGradeByCourse(createRequest.getStudentNumber(),
                courseCode, jwtToken);
        verify(courseCommunication, times(1)).getCourseStartDate(courseCode, jwtToken);
        verify(courseCommunication, never()).getCourseDescription(courseCode, jwtToken);
        verify(userCommunication, never())
                .getGpaByStudent(createRequest.getStudentNumber(), jwtToken);
        verify(submissionRepository, never()).save(any(Submission.class));

    }

    @Test
    @WithMockUser(roles = {"STUDENTNUMBER_" + 101010L})
    public void testSubmissionDeadlinePassed() throws Exception {
        when(submissionRepository.getByStudentNumberAndCourseCode(
                createRequest.getStudentNumber(), courseCode))
                .thenReturn(Optional.empty());
        when(courseCommunication.getCourseStartDate(courseCode, jwtToken))
                .thenReturn(new Date(2021, 12, 29));

        assertThrows(SubmissionDeadlinePassedException.class, () -> submissionService
                .createSubmission(createRequest, courseCode,
                        new Date(2021, 12, 25), jwtToken));
        verify(courseCommunication, times(1))
                .getCourseStartDate(courseCode, jwtToken);
        verify(courseCommunication, never()).getCourseDescription(courseCode, jwtToken);
        verify(userCommunication, never()).getGpaByStudent(createRequest
                .getStudentNumber(), jwtToken);
        verify(submissionRepository, never()).save(any(Submission.class));

    }

    @Test
    @WithMockUser(roles = {"LECTURER"})
    public void testFindSubmissionFound() throws Exception {
        Submission submissionAccepted = new SubmissionBuilder()
                .withStatus(Status.ACCEPTED)
                .build(submission.getSubmissionId());

        when(submissionRepository.getBySubmissionId(submissionAccepted
                .getSubmissionId()))
                .thenReturn(Optional.of(submissionAccepted));

        assertEquals(submissionAccepted, submissionService
                .findSubmission(submissionAccepted.getSubmissionId()));
    }

    @Test
    @WithMockUser(roles = {"LECTURER"})
    public void testFindSubmissionNotFound() throws Exception {
        Submission submissionPending = new SubmissionBuilder()
                .withStatus(Status.PENDING)
                .build(submission.getSubmissionId());

        when(submissionRepository.getBySubmissionId(submissionPending
                .getSubmissionId()))
                .thenReturn(Optional.empty());

        assertThrows(SubmissionNotFoundException.class,
                () -> submissionService.findSubmission(submissionPending.getSubmissionId()));
    }

    @Test
    @WithMockUser(roles = {"STUDENTNUMBER_" + 101010L})
    public void testAcceptedSubmission() throws Exception {
        Submission app = new SubmissionBuilder()
                .withCourse("CSE500")
                .withStudentNumber(505L)
                .withStudentEmail("infoschaik@gmail.com")
                .withAmountOfHours(20F)
                .build(99L);
        Submission appAccepted = new SubmissionBuilder()
                .withStatus(Status.ACCEPTED)
                .withCourse("CSE500")
                .withStudentNumber(505L)
                .withStudentEmail("infoschaik@gmail.com")
                .withAmountOfHours(20F)
                .build(app.getSubmissionId());

        when(submissionRepository.getBySubmissionId(app.getSubmissionId()))
                .thenReturn(Optional.of(app));
        when(submissionRepository.save(any(Submission.class)))
                .thenReturn(appAccepted);
        when(courseCommunication.getLecturersForCourse(app.getCourseCode(), jwtToken))
                .thenReturn(Set.of(lectureId));

        Submission appResult = submissionService
                .acceptSubmission(app.getSubmissionId(), lectureId, jwtToken);

        assertEquals(appAccepted, appResult);
        verify(courseCommunication, times(1))
                .getLecturersForCourse(appAccepted.getCourseCode(), jwtToken);
        verify(courseCommunication, times(1))
                .addHiredStudent(appAccepted.getStudentNumber(),
                appAccepted.getCourseCode(), jwtToken);
        verify(userCommunication, times(1))
                .addTaRole(appAccepted.getStudentNumber(),
                appAccepted.getCourseCode(), jwtToken);
        verify(submissionRepository, times(1)).save(appResult);

    }

    @Test
    @WithMockUser(roles = {"LECTURER"})
    public void testAcceptNotFoundSubmission() throws Exception {

        when(submissionRepository.getBySubmissionId(submission.getSubmissionId()))
                .thenReturn(Optional.empty());

        assertThrows(SubmissionNotFoundException.class, () -> submissionService
                .acceptSubmission(submission.getSubmissionId(), lectureId, jwtToken));
        verify(courseCommunication, never())
                .getLecturersForCourse(submission.getCourseCode(), jwtToken);
        verify(courseCommunication, never())
                .addHiredStudent(submission.getStudentNumber(),
                submission.getCourseCode(), jwtToken);
        verify(userCommunication, never()).addTaRole(submission.getStudentNumber(),
                submission.getCourseCode(), jwtToken);
        verify(submissionRepository, never()).save(any(Submission.class));
    }

    @Test
    @WithMockUser("LECTURER")
    public void testAcceptClosedRetractedSubmission() throws Exception {
        Submission submissionRetracted = new SubmissionBuilder()
                .withStatus(Status.RETRACTED)
                .build(submission.getSubmissionId());

        when(submissionRepository.getBySubmissionId(submissionRetracted
                .getSubmissionId()))
                .thenReturn(Optional.of(submissionRetracted));

        assertThrows(SubmissionClosedException.class, () -> submissionService
                .acceptSubmission(submissionRetracted
                        .getSubmissionId(), lectureId, jwtToken));
        verify(courseCommunication, never())
                .addHiredStudent(submissionRetracted.getStudentNumber(),
                submissionRetracted.getCourseCode(), jwtToken);
        verify(userCommunication, never()).addTaRole(submissionRetracted
                        .getStudentNumber(),
                submissionRetracted.getCourseCode(), jwtToken);
        verify(submissionRepository, never()).save(any(Submission.class));
    }



    @Test
    @WithMockUser(roles = {"LECTURER"})
    public void testAcceptClosedRejectedSubmission() throws Exception {
        Submission submissionRejected = new SubmissionBuilder()
                .withStatus(Status.REJECTED)
                .build(submission.getSubmissionId());

        when(submissionRepository.getBySubmissionId(submissionRejected
                .getSubmissionId()))
                .thenReturn(Optional.of(submissionRejected));

        assertThrows(SubmissionClosedException.class, () -> submissionService
                .acceptSubmission(submissionRejected.getSubmissionId(), lectureId, jwtToken));

        verify(courseCommunication, never())
                .addHiredStudent(submissionRejected.getStudentNumber(),
                submissionRejected.getCourseCode(), jwtToken);
        verify(userCommunication, never()).addTaRole(submissionRejected.getStudentNumber(),
                submissionRejected.getCourseCode(), jwtToken);
        verify(submissionRepository, never()).save(any(Submission.class));
    }

    @Test
    @WithMockUser(roles = {"LECTURER"})
    public void testAcceptClosedAcceptedSubmission() throws Exception {
        Submission submissionAccepted = new SubmissionBuilder()
                .withStatus(Status.ACCEPTED)
                .build(submission.getSubmissionId());

        when(submissionRepository.getBySubmissionId(submissionAccepted
                .getSubmissionId()))
                .thenReturn(Optional.of(submissionAccepted));

        assertThrows(SubmissionClosedException.class, () -> submissionService
                .acceptSubmission(submissionAccepted.getSubmissionId(), lectureId, jwtToken));
        verify(courseCommunication, never())
                .addHiredStudent(submissionAccepted.getStudentNumber(),
                        submissionAccepted.getCourseCode(), jwtToken);
        verify(userCommunication, never()).addTaRole(submissionAccepted.getStudentNumber(),
                submissionAccepted.getCourseCode(), jwtToken);
        verify(submissionRepository, never()).save(any(Submission.class));
    }

    @Test
    @WithMockUser(roles = {"LECTURER"})
    public void testLecturerUnauthorizedException() throws Exception {
        when(submissionRepository.getBySubmissionId(submission.getSubmissionId()))
                .thenReturn(Optional.of(submission));
        when(courseCommunication.getLecturersForCourse(submission.getCourseCode(), jwtToken))
                .thenReturn(Set.of(1L, 2L, 3L));

        assertThrows(LecturerUnauthorizedException.class, () -> submissionService
                .acceptSubmission(submission.getSubmissionId(), lectureId, jwtToken));
        verify(courseCommunication, times(1))
                .getLecturersForCourse(submission.getCourseCode(), jwtToken);
        verify(courseCommunication, never()).addHiredStudent(submission.getStudentNumber(),
                submission.getCourseCode(), jwtToken);
        verify(userCommunication, never()).addTaRole(submission.getStudentNumber(),
                submission.getCourseCode(), jwtToken);
        verify(submissionRepository, never()).save(any(Submission.class));

    }

    @Test
    @WithMockUser(roles = {"LECTURER"})
    public void testLecturersEmptyUnauthorizedException() throws Exception {
        when(submissionRepository.getBySubmissionId(submission.getSubmissionId()))
                .thenReturn(Optional.of(submission));
        when(courseCommunication.getLecturersForCourse(submission.getCourseCode(), jwtToken))
                .thenReturn(null);

        assertThrows(LecturerUnauthorizedException.class, () -> submissionService
                .acceptSubmission(submission.getSubmissionId(), lectureId, jwtToken));
        verify(courseCommunication, times(1))
                .getLecturersForCourse(submission.getCourseCode(), jwtToken);
        verify(courseCommunication, never()).addHiredStudent(submission.getStudentNumber(),
                submission.getCourseCode(), jwtToken);
        verify(userCommunication, never()).addTaRole(submission.getStudentNumber(),
                submission.getCourseCode(), jwtToken);
        verify(submissionRepository, never()).save(any(Submission.class));
    }

    @Test
    @WithMockUser(roles = {"LECTURER"})
    public void getAllPendingSubmissionsSuccessTest() throws Exception {
        List<Submission> submissions = List.of(submission, submission1, submission2);
        List<Submission> pendingSubmissions = List.of(submission, submission2);

        when(courseCommunication.getLecturersForCourse(courseCode, jwtToken))
                .thenReturn(Set.of(lectureId));
        when(submissionRepository.getByCourseCode(any(String.class)))
                .thenReturn(Optional.of(submissions));

        List<Submission> response = submissionService
                .getAllPendingSubmissions(courseCode, lectureId, jwtToken);

        assertTrue(response.size() == pendingSubmissions.size());

        verify(courseCommunication, times(1))
                .getLecturersForCourse(courseCode, jwtToken);
        verify(submissionRepository, times(1))
                .getByCourseCode(courseCode);
    }

    @Test
    @WithMockUser(roles = {"LECTURER"})
    public void getAllPendingSubmissionsEmptyTest() throws Exception {
        List<Submission> submissions = List.of();

        when(courseCommunication.getLecturersForCourse(courseCode, jwtToken))
                .thenReturn(Set.of(lectureId));
        when(submissionRepository.getByCourseCode(any(String.class)))
                .thenReturn(Optional.of(submissions));

        List<Submission> response = submissionService
                .getAllPendingSubmissions(courseCode, lectureId, jwtToken);

        assertEquals(0, response.size());
        assertTrue(response.isEmpty());

        verify(courseCommunication, times(1))
                .getLecturersForCourse(courseCode, jwtToken);
        verify(submissionRepository, times(1))
                .getByCourseCode(courseCode);
    }


    @Test
    @WithMockUser(roles = {"LECTURER"})
    public void getAllPendingSubmissionsCourseErrorTest() throws Exception {
        List<Submission> submissions = List.of();

        when(courseCommunication.getLecturersForCourse(courseCode, jwtToken))
                .thenThrow(new CourseServiceErrorException("error"));
        when(submissionRepository.getByCourseCode(any(String.class)))
                .thenReturn(Optional.of(submissions));


        assertThrows(CourseServiceErrorException.class,
                () -> submissionService
                        .getAllPendingSubmissions(courseCode, lectureId, jwtToken));

        verify(courseCommunication, times(1))
                .getLecturersForCourse(courseCode, jwtToken);
        verify(submissionRepository, never()).getByCourseCode(courseCode);
    }


    @Test
    @WithMockUser(roles = {"LECTURER"})
    public void getAllPendingSubmissionsLecturerErrorTest() throws Exception {
        List<Submission> submissions = List.of();

        when(courseCommunication.getLecturersForCourse(courseCode, jwtToken))
                .thenReturn(Set.of(1L));
        when(submissionRepository.getByCourseCode(any(String.class)))
                .thenReturn(Optional.of(submissions));


        assertThrows(LecturerUnauthorizedException.class,
                () -> submissionService
                        .getAllPendingSubmissions(courseCode, lectureId, jwtToken));

        verify(courseCommunication, times(1))
                .getLecturersForCourse(courseCode, jwtToken);
        verify(submissionRepository, never()).getByCourseCode(courseCode);
    }

    @Test
    @WithMockUser(roles = {"LECTURER"})
    public void getRecommendationsForCourseRatingTest() throws Exception {
        List<Submission> submissions = List.of(submission, submission2);
        List<StudentRating> studentRatingResponses =
                List.of(new StudentRating(submission.getStudentNumber(), 4.1f),
                        new StudentRating(submission2.getStudentNumber(), 5.0f));
        List<Long> studentNumbers = List.of(submission.getStudentNumber(),
                submission2.getStudentNumber());

        when(courseCommunication.getLecturersForCourse(courseCode, jwtToken))
                .thenReturn(Set.of(lectureId));
        when(userCommunication.getRatings(studentNumbers, jwtToken))
                .thenReturn(studentRatingResponses);
        when(submissionRepository.getByCourseCode(courseCode))
                .thenReturn(Optional.of(submissions));

        List<RecommendedSubmission> response = submissionService
                .getRecommendationsForCourse(courseCode, lectureId, new RatingStrategy(), jwtToken);
        List<Submission> pendingSubmissions = List.of(submission, submission2);
        assertEquals(response.size(), pendingSubmissions.size());
        assertEquals(pendingSubmissions.get(1), response.get(0).getSubmission());
        assertEquals(pendingSubmissions.get(0), response.get(1).getSubmission());

    }

    @Test
    @WithMockUser(roles = {"LECTURER"})
    public void getRecommendationsForCourseDefaultTest() throws Exception {
        List<Submission> submissions = List.of(submission, submission2);
        List<StudentRating> studentRatingResponses =
                List.of(new StudentRating(submission.getStudentNumber(), 4.1f),
                        new StudentRating(submission2.getStudentNumber(), 5.0f));
        List<Long> studentNumbers = List.of(submission.getStudentNumber(),
                submission2.getStudentNumber());

        when(courseCommunication.getLecturersForCourse(courseCode, jwtToken))
                .thenReturn(Set.of(lectureId));
        when(userCommunication.getRatings(studentNumbers, jwtToken))
                .thenReturn(studentRatingResponses);
        when(submissionRepository.getByCourseCode(courseCode))
                .thenReturn(Optional.of(submissions));

        List<RecommendedSubmission> response = submissionService
                .getRecommendationsForCourse(courseCode, lectureId, new RatingStrategy(), jwtToken);
        List<Submission> pendingSubmissions = List.of(submission, submission2);
        assertEquals(response.size(), pendingSubmissions.size());
        assertEquals(pendingSubmissions.get(1), response.get(0).getSubmission());
        assertEquals(pendingSubmissions.get(0), response.get(1).getSubmission());

    }


    @Test
    @WithMockUser(roles = {"LECTURER"})
    public void getRecommendationsForCourseGradeTest() throws Exception {
        List<Submission> submissions = List.of(submission, submission2);
        List<StudentGrade> studentGradeResponses =
                List.of(new StudentGrade(submission.getStudentNumber(), 7.0f),
                        new StudentGrade(submission2.getStudentNumber(), 10.0f));
        List<Long> studentNumbers = List.of(submission.getStudentNumber(),
                submission2.getStudentNumber());


        when(courseCommunication.getLecturersForCourse(courseCode, jwtToken))
                .thenReturn(Set.of(lectureId));
        when(userCommunication.getGradesByCourse(studentNumbers, courseCode, jwtToken))
                .thenReturn(studentGradeResponses);
        when(submissionRepository.getByCourseCode(courseCode))
                .thenReturn(Optional.of(submissions));

        List<RecommendedSubmission> response = submissionService
                .getRecommendationsForCourse(courseCode, lectureId, new GradeStrategy(), jwtToken);
        List<Submission> pendingSubmissions = List.of(submission, submission2);
        assertEquals(response.size(), pendingSubmissions.size());
        assertEquals(pendingSubmissions.get(1), response.get(0).getSubmission());
        assertEquals(pendingSubmissions.get(0), response.get(1).getSubmission());

    }


    @Test
    @WithMockUser(roles = {"LECTURER"})
    public void getRecommendationsForLecturerUnauthorized() throws Exception {
        List<Submission> submissions = List.of(submission, submission1, submission2);

        when(courseCommunication.getLecturersForCourse(courseCode, jwtToken))
                .thenReturn(Set.of(lectureId + 1));
        when(userCommunication.getGradeByCourse(101010L, courseCode, jwtToken)).thenReturn(10.0f);
        when(userCommunication.getGradeByCourse(1000L, courseCode, jwtToken)).thenReturn(2.0f);
        when(userCommunication.getGradeByCourse(2000L, courseCode, jwtToken)).thenReturn(7.0f);
        when(submissionRepository.getByCourseCode(courseCode))
                .thenReturn(Optional.of(submissions));

        assertThrows(LecturerUnauthorizedException.class, () -> {
            submissionService
                    .getRecommendationsForCourse(courseCode, lectureId,
                            new GradeStrategy(), jwtToken);
        });
    }


    @Test
    @WithMockUser(roles = {"STUDENTNUMBER_" + 101010L})
    public void testRetractedSubmission() throws Exception {
        when(submissionRepository.getBySubmissionId(submission.getSubmissionId()))
                .thenReturn(Optional.of(submission));
        when(submissionRepository.save(any(Submission.class))).thenReturn(submission);
        when(courseCommunication.getCourseStartDate(submission.getCourseCode(), jwtToken))
                .thenReturn(new Date(2021, 12, 1));

        assertEquals(true, submissionService
                .retractSubmission(submission.getSubmissionId(),
                submission.getStudentNumber(), retractDate, jwtToken));

        verify(submissionRepository, times(1))
                .getBySubmissionId(submission.getSubmissionId());
        verify(submissionRepository, times(1)).delete(submission);
        verify(courseCommunication, times(1))
                .getCourseStartDate(submission.getCourseCode(), jwtToken);

    }

    @Test
    @WithMockUser(roles = {"STUDENTNUMBER_" + 101010L})
    public void testRetractSubmissionNotFound() throws Exception {
        when(submissionRepository.getBySubmissionId(submission.getSubmissionId()))
                .thenReturn(Optional.empty());

        assertThrows(SubmissionNotFoundException.class, () -> submissionService
                .retractSubmission(submission.getSubmissionId(), retractStudentNumber,
                        retractDate, jwtToken));

        verify(submissionRepository, times(1))
                .getBySubmissionId(submission.getSubmissionId());
        verify(submissionRepository, never()).delete(any(Submission.class));
        verify(courseCommunication, never())
                .getCourseStartDate(submission.getCourseCode(), jwtToken);
    }

    @Test
    @WithMockUser(roles = {"STUDENTNUMBER_" + 101010L})
    public void testRetractStudentUnauthorized() throws Exception {
        // So that student number doesn't match
        Submission app = new SubmissionBuilder()
                .withStudentNumber(retractStudentNumber + 1)
                .build(42L);
        when(submissionRepository.getBySubmissionId(submission.getSubmissionId()))
                .thenReturn(Optional.of(app));

        assertThrows(StudentUnauthorizedException.class, () -> submissionService
                .retractSubmission(submission.getSubmissionId(),
                        retractStudentNumber, retractDate, jwtToken));

        verify(submissionRepository, times(1))
                .getBySubmissionId(submission.getSubmissionId());
        verify(submissionRepository, never()).delete(any(Submission.class));
        verify(courseCommunication, never())
                .getCourseStartDate(submission.getCourseCode(), jwtToken);
    }

    @Test
    @WithMockUser(roles = {"STUDENTNUMBER_" + 101010L})
    public void testRetractCourseServiceError() throws Exception {
        when(submissionRepository.getBySubmissionId(submission.getSubmissionId()))
                .thenReturn(Optional.of(submission));
        when(courseCommunication.getCourseStartDate(submission.getCourseCode(), jwtToken))
                .thenThrow(CourseServiceErrorException.class);

        assertThrows(CourseServiceErrorException.class, () -> submissionService
                .retractSubmission(submission.getSubmissionId(),
                        submission.getStudentNumber(), retractDate, jwtToken));

        verify(submissionRepository, times(1))
                .getBySubmissionId(submission.getSubmissionId());
        verify(submissionRepository, never()).delete(any(Submission.class));
        verify(courseCommunication, times(1))
                .getCourseStartDate(submission.getCourseCode(), jwtToken);
    }

    @Test
    @WithMockUser(roles = {"STUDENTNUMBER_" + 101010L})
    public void testRetractAcceptedSubmission() throws Exception {
        Submission accepted = new SubmissionBuilder()
                .withStatus(Status.ACCEPTED)
                .build(42L);
        when(submissionRepository.getBySubmissionId(submission.getSubmissionId()))
                .thenReturn(Optional.of(accepted));
        when(courseCommunication.getCourseStartDate(accepted.getCourseCode(), jwtToken))
                .thenReturn(new Date(2022, 1, 1));

        assertEquals(false, submissionService.retractSubmission(submission.getSubmissionId(),
                        accepted.getStudentNumber(), retractDate, jwtToken));

        verify(submissionRepository, times(1))
                .getBySubmissionId(submission.getSubmissionId());
        verify(submissionRepository, never()).delete(any(Submission.class));
        verify(courseCommunication, times(1))
                .getCourseStartDate(accepted.getCourseCode(), jwtToken);
    }

    @Test
    @WithMockUser(roles = {"STUDENTNUMBER_" + 101010L})
    public void testRetractSubmissionLate() throws Exception {
        when(submissionRepository.getBySubmissionId(submission.getSubmissionId()))
                .thenReturn(Optional.of(submission));
        when(courseCommunication.getCourseStartDate(submission.getCourseCode(), jwtToken))
                .thenReturn(new Date(2021, 10, 15));

        assertEquals(false, submissionService.retractSubmission(submission.getSubmissionId(),
                submission.getStudentNumber(), retractDate, jwtToken));

        verify(submissionRepository, times(1))
                .getBySubmissionId(submission.getSubmissionId());
        verify(submissionRepository, never()).delete(any(Submission.class));
        verify(courseCommunication, times(1))
                .getCourseStartDate(submission.getCourseCode(), jwtToken);
    }

    @Test
    @WithMockUser(roles = {"LECTURER"})
    public void testSetAmountHoursSuccess() throws Exception {
        // Only way to check: possibly using a spy
        when(submissionRepository.getByStudentNumberAndCourseCode(submission.getStudentNumber(),
                submission.getCourseCode())).thenReturn(Optional.of(submission));
        submission.setAmountOfHours(42f);
        when(submissionRepository.save(any(Submission.class))).thenReturn(submission);
        Submission result = submissionService.setAmountOfHours(submission.getStudentNumber(),
                submission.getCourseCode(), 42f);
        assertEquals(42f, result.getAmountOfHours());
    }

    @Test
    @WithMockUser(roles = {"LECTURER"})
    public void testSetAmountHoursSubmissionNotFound() {
        when(submissionRepository.getByStudentNumberAndCourseCode(submission.getStudentNumber(),
                submission.getCourseCode())).thenReturn(Optional.empty());

        assertThrows(SubmissionNotFoundException.class, () -> {
            submissionService.setAmountOfHours(submission.getStudentNumber(),
                    submission.getCourseCode(), 42f);
        });
    }

}
