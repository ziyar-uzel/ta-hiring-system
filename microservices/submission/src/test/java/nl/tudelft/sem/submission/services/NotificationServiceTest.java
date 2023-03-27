package nl.tudelft.sem.submission.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sendgrid.Client;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import java.io.IOException;
import java.util.Optional;

import nl.tudelft.sem.submission.SubmissionMicroService;
import nl.tudelft.sem.submission.controller.dto.SubmissionCreateRequest;
import nl.tudelft.sem.submission.emailconfig.ApiKey;
import nl.tudelft.sem.submission.emailconfig.EmailSending;
import nl.tudelft.sem.submission.entities.Notification;
import nl.tudelft.sem.submission.entities.Status;
import nl.tudelft.sem.submission.entities.Submission;
import nl.tudelft.sem.submission.repositories.ContractRepository;
import nl.tudelft.sem.submission.repositories.NotificationRepository;
import nl.tudelft.sem.submission.repositories.SubmissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(classes = SubmissionMicroService.class,
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@DirtiesContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class NotificationServiceTest {
    @Autowired
    private transient NotificationService notificationService;
    @MockBean
    private transient SubmissionRepository submissionRepository;
    @MockBean
    private transient NotificationRepository notificationRepository;
    @MockBean
    private transient ContractRepository contractRepository;

    private transient Submission submission;
    private transient Notification notification;
    private final transient SubmissionCreateRequest createRequest
        = new SubmissionCreateRequest();
    private transient Submission submission1;
    private transient Submission submission2;
    private static final String courseCode = "CSE2115";

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

        this.notification = new NotificationBuilder()
            .withNotifiedCourseId(courseCode)
            .withNotifiedEmail(createRequest.getStudentEmail())
            .withNotifiedStudentNumber(createRequest.getStudentNumber())
            .withStatus(Status.PENDING)
            .build(null);

        submission1 = new SubmissionBuilder()
            .withStatus(Status.ACCEPTED)
            .withStudentNumber(1000L)
            .build(2L);
        submission2 = new SubmissionBuilder()
            .withStudentNumber(2000L)
            .build(3L);
    }

    @Test
    @WithMockUser(roles = {"LECTURER"})
    public void testGenericSendGridConstructWithClientTest() throws IOException {
        Client client = mock(Client.class);
        SendGrid sg = new SendGrid(ApiKey.getKey(), client);
        Request request = new Request();
        sg.makeCall(request);
        verify(client).api(request);
    }

    @Test
    @WithMockUser(roles = {"LECTURER", "STUDENTNUMBER_" + 1000L})
    public void testGenericSendEmailTest() throws Exception {
        new EmailSending().sendEmail(new String[]{"infoschaik@gmail.com", "news",
            "hello"}, null);
    }

    @Test
    @WithMockUser(roles = {"LECTURER", "STUDENTNUMBER_" + 1000L})
    public void testResultNotificationSuccess() throws Exception {
        when(submissionRepository.getBySubmissionId(submission.getSubmissionId()))
            .thenReturn(Optional.of(submission));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        Notification notificationCreated = notificationService
            .createResultNotification(submission);
        assertEquals(notification, notificationCreated);
        verify(notificationRepository, times(1)).save(notificationCreated);
    }

    @Test
    @WithMockUser(roles = {"LECTURER", "STUDENTNUMBER_" + 1000L})
    public void testConfirmationNotificationSuccess() throws Exception {
        when(submissionRepository.getBySubmissionId(submission.getSubmissionId()))
            .thenReturn(Optional.of(submission));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        Notification notificationCreated = notificationService
            .createConfirmationNotification(submission);
        assertEquals(notification, notificationCreated);
        verify(notificationRepository, times(1)).save(notificationCreated);
    }

}
