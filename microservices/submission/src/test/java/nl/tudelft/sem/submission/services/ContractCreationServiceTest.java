package nl.tudelft.sem.submission.services;

import nl.tudelft.sem.submission.entities.Status;
import nl.tudelft.sem.submission.entities.Submission;
import nl.tudelft.sem.submission.repositories.ContractRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ContractCreationServiceTest {

    private final transient ContractRepository contractRepository = Mockito.mock(ContractRepository.class);
    private final transient ContractCreationService contractCreation = new ContractCreationService(contractRepository);

    private transient Submission submission;
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

    @BeforeEach
    void setup() {
        this.submission = new SubmissionBuilder()
                .withCourse(courseCode)
                .withStudentNumber(101010L)
                .withStudentEmail("student@tudelft.nl")
                .withStatus(Status.PENDING)
                .build(1L);
    }

    @Test
    public void testGenericAcceptedEmailWithContract() throws Exception {
        String body = "Hello";
        Boolean result = contractCreation.createContract(submission, body);
        assertEquals(result, true);
    }
}
