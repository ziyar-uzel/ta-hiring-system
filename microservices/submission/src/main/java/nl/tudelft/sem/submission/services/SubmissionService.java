package nl.tudelft.sem.submission.services;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import nl.tudelft.sem.submission.communication.CourseCommunication;
import nl.tudelft.sem.submission.communication.UserCommunication;
import nl.tudelft.sem.submission.controller.dto.SubmissionCreateRequest;
import nl.tudelft.sem.submission.entities.Status;
import nl.tudelft.sem.submission.entities.Submission;
import nl.tudelft.sem.submission.exceptions.BadRequestException;
import nl.tudelft.sem.submission.exceptions.CourseServiceErrorException;
import nl.tudelft.sem.submission.exceptions.DuplicateSubmissionException;
import nl.tudelft.sem.submission.exceptions.ForbiddenException;
import nl.tudelft.sem.submission.exceptions.InsufficientGradeException;
import nl.tudelft.sem.submission.exceptions.LecturerUnauthorizedException;
import nl.tudelft.sem.submission.exceptions.ServiceErrorException;
import nl.tudelft.sem.submission.exceptions.StudentUnauthorizedException;
import nl.tudelft.sem.submission.exceptions.SubmissionClosedException;
import nl.tudelft.sem.submission.exceptions.SubmissionDeadlinePassedException;
import nl.tudelft.sem.submission.exceptions.SubmissionNotFoundException;
import nl.tudelft.sem.submission.exceptions.UserServiceErrorException;
import nl.tudelft.sem.submission.repositories.SubmissionRepository;
import nl.tudelft.sem.submission.utils.Recommendation;
import nl.tudelft.sem.submission.utils.Utils;
import nl.tudelft.sem.submission.utils.definition.DefaultStrategy;
import nl.tudelft.sem.submission.utils.definition.NoStrategy;
import nl.tudelft.sem.submission.utils.strategy.GradeStrategy;
import nl.tudelft.sem.submission.utils.strategy.RatingStrategy;
import nl.tudelft.sem.submission.utils.strategy.models.RecommendedSubmission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class SubmissionService {

    private final transient SubmissionRepository submissionRepository;
    private final transient CourseCommunication courseCommunication;
    private final transient UserCommunication userCommunication;
    private final transient NotificationService notificationService;

    /**
     * Constructor for the Submission Service class.
     *
     * @param submissionRepository  repository for submissions
     * @param courseCommunication    communication to course service
     * @param userCommunication      communication to user service
     */
    @Autowired
    public SubmissionService(SubmissionRepository submissionRepository,
                             CourseCommunication courseCommunication,
                             UserCommunication userCommunication,
                             @Lazy NotificationService notificationService) {
        this.submissionRepository = submissionRepository;
        this.courseCommunication = courseCommunication;
        this.userCommunication = userCommunication;
        this.notificationService = notificationService;
    }

    /**
     * Creates an submission for a user and stores it into database.
     *
     * @param courseCode     course code of course applied to
     * @param submissionDate date submission was submitted
     * @return the submission created
     */

    public Submission createSubmission(SubmissionCreateRequest body,
                                       String courseCode, Date submissionDate, String jwtHeader)
            throws BadRequestException, ServiceErrorException, SubmissionNotFoundException,
            IOException, URISyntaxException {

        // Check whether it's a valid submission
        checkSubmissionCredentials(body.getStudentNumber(), submissionDate, courseCode, jwtHeader);

        String courseDescription = courseCommunication.getCourseDescription(courseCode, jwtHeader);
        Float studentGpa = userCommunication.getGpaByStudent(body.getStudentNumber(), jwtHeader);
        courseCommunication.addCandidateStudent(body.getStudentNumber(), courseCode, jwtHeader);

        // Set fields of submission
        Submission submission = Submission.builder().courseCode(courseCode)
                .studentNumber(body.getStudentNumber())
                .studentEmail(body.getStudentEmail())
                .status(Status.PENDING).amountOfHours(0f)
                .description(courseDescription).studentGpa(studentGpa).build();

        // Save to db
        Submission finalSubmission = submissionRepository.save(submission);
        notificationService.createConfirmationNotification(finalSubmission);

        return finalSubmission;
    }


    /**
     * Checks the credentials of the submission.
     *
     * @param studentNumber  Student number checked for
     * @param submissionDate Date of submission
     * @param courseCode     Course code of course applied
     * @param jwtHeader      Header for authentication
     * @throws BadRequestException   Thrown on invalid submission
     * @throws ServiceErrorException Thrown on service error
     */
    public void checkSubmissionCredentials(Long studentNumber, Date submissionDate,
                                           String courseCode, String jwtHeader)
            throws BadRequestException, ServiceErrorException {

        // Check for duplicates
        checkForDuplicate(studentNumber, courseCode);

        // Submission must be made more than 3 weeks prior to start date
        Date startDate = courseCommunication.getCourseStartDate(courseCode, jwtHeader);
        checkSubmissionDeadline(startDate, submissionDate);

        // Student has insufficient grade for the course
        Float courseGrade = userCommunication
                .getGradeByCourse(studentNumber, courseCode, jwtHeader);
        if (courseGrade < Utils.passGrade) {
            throw new InsufficientGradeException(
                    "Applicant does not have a sufficient grade (< " + Utils.passGrade + ")");
        }
    }

    /**
     * Checks whether submission deadline has passed.
     *
     * @param startDate      Start date of the course
     * @param submissionDate Date of submitting application
     * @throws BadRequestException Thrown on invalid submission
     */
    public void checkSubmissionDeadline(Date startDate, Date submissionDate)
            throws BadRequestException {
        long difference = startDate.getTime() - submissionDate.getTime();
        long days = TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS)
                - Utils.daysBeforeDeadline;
        if (days < 0) {
            throw new SubmissionDeadlinePassedException(
                    "Submission deadline for this course has passed!");
        }
    }

    /**
     * Checks whether the submission created is duplicate.
     *
     * @param studentNumber Student number of student applying
     * @param courseCode    Course code of course applied to
     * @throws BadRequestException Thrown if submission is duplicate
     */
    public void checkForDuplicate(Long studentNumber, String courseCode)
            throws BadRequestException {
        // Duplicate application created
        Optional<Submission> duplicate = submissionRepository
                .getByStudentNumberAndCourseCode(studentNumber, courseCode);
        if (duplicate.isPresent()) {
            throw new DuplicateSubmissionException("Submission has already been created");
        }
    }


    /**
     * Updates the amount of hours the selected applicant should work.
     *
     * @param studentNumber The student number of selected submission
     * @param courseCode      The course code of course selected for
     * @param amountOfHours The amount of hours they have to work
     * @return The updated submission
     * @throws SubmissionNotFoundException thrown when submission does not exist
     */
    public Submission setAmountOfHours(Long studentNumber, String courseCode, Float amountOfHours)
            throws SubmissionNotFoundException {

        // Check whether submission exists
        Optional<Submission> checkSubmission = submissionRepository
                .getByStudentNumberAndCourseCode(studentNumber, courseCode);
        if (checkSubmission.isEmpty()) {
            throw new SubmissionNotFoundException("Submission does not exist!");
        }

        // Update the number of hours on submission
        Submission submission = checkSubmission.get();
        submission.setAmountOfHours(amountOfHours);

        return submissionRepository.save(submission);
    }

    /**
     * Finds a submission in the submission repository.
     *
     * @param submissionId Id of submission to find
     * @return Submission if it is found
     * @throws SubmissionNotFoundException Thrown when submission does not exist
     */
    public Submission findSubmission(Long submissionId) throws SubmissionNotFoundException {
        Optional<Submission> app = submissionRepository.getBySubmissionId(submissionId);
        if (app.isEmpty()) {
            throw new SubmissionNotFoundException("Submission does not exist");
        }
        System.out.println("this submission exists" + app.get().getCourseCode());
        return app.get();
    }


    /**
     * Accepts submission by lecturer.
     *
     * @param submissionId Id of submission accepted
     * @param lecturerId   Id of lecturer sending request
     * @param jwtHeader    JWT header for authentication
     * @return The updated submission
     * @throws SubmissionNotFoundException Thrown if submission is not found
     * @throws BadRequestException         Thrown if submission is invalid
     * @throws ServiceErrorException       Thrown if other microservices fail
     * @throws ForbiddenException          Thrown if lecturer is not authenticated
     */
    public Submission acceptSubmission(Long submissionId, Long lecturerId, String jwtHeader)
            throws SubmissionNotFoundException, BadRequestException, ServiceErrorException,
            ForbiddenException, URISyntaxException, IOException, InterruptedException {

        Submission submission = findSubmission(submissionId);
        System.out.println(submission.getCourseCode());
        // Check whether lecturer can accept submission
        Set<Long> lecturers = courseCommunication
                .getLecturersForCourse(submission.getCourseCode(), jwtHeader);
        checkValidAccept(lecturers, lecturerId, submission.getStatus());
        updateAsTa(submission.getStudentNumber(), submission.getCourseCode(), jwtHeader);

        // Update submission status
        submission.setStatus(Status.ACCEPTED);

        Submission acceptedSubmission = submissionRepository.save(submission);
        notificationService
                .createResultNotification(acceptedSubmission);
        return acceptedSubmission;
    }

    /**
     * Checks if a submission can be retracted regarding:
     * - the time until the course start date (more than 3 weeks left);
     * - and the status of the submission (PENDING).
     *
     * @param submission the submission
     * @param retractDate the date when the retract is requested
     * @return true if possible, false otherwise
     */
    private boolean canRetractSubmission(Submission submission, Date retractDate, String jwtHeader)
            throws ServiceErrorException {
        // Can not be retracted if the submission is accepted or there is less than 3 weeks left
        Date startDate = courseCommunication
                .getCourseStartDate(submission.getCourseCode(), jwtHeader);
        long difference = startDate.getTime() - retractDate.getTime();
        long days = TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS)
                - Utils.daysBeforeDeadline;

        return days >= 0 && submission.getStatus() != Status.ACCEPTED;
    }

    /**
     * Deletes a submission from the database.
     *
     * @param submission the submission
     */
    private void deleteSubmission(Submission submission, String jwtHeader)
            throws ServiceErrorException {
        courseCommunication.removeCandidateStudent(submission.getStudentNumber(),
                jwtHeader, submission.getCourseCode());
        submissionRepository.delete(submission);
    }

    /**
     * Gets the submission from a submissionId.
     *
     * @param submissionId the submission id
     * @return the submission
     */
    private Submission getSubmission(Long submissionId)
            throws SubmissionNotFoundException {
        Optional<Submission> app = submissionRepository.getBySubmissionId(submissionId);
        if (app.isEmpty()) {
            throw new SubmissionNotFoundException("Submission does not exist!");
        }

        return app.get();
    }


    /**
     * Checks whether the submission is valid for accept.
     *
     * @param lecturers  Lecturers responsible for course
     * @param lecturerId Id of lecturer accepting the submission
     * @param status     Current status of submission
     * @throws ForbiddenException  Thrown if lecturer is unauthorized
     * @throws BadRequestException Thrown if submission is invalid
     */
    public void checkValidAccept(Set<Long> lecturers, Long lecturerId, Status status)
            throws ForbiddenException, BadRequestException {
        if (status != Status.PENDING) {
            throw new SubmissionClosedException("Submission has been either rejected "
                    + "or retracted or previously accepted.");
        }
        if (lecturers == null || !lecturers.contains(lecturerId)) {
            throw new LecturerUnauthorizedException(
                    "Lecturer unauthorized to accept this submission");
        }
    }

    /**
     * Updates student as TA with user and course microservice.
     *
     * @param studentNumber Student number of student becoming TA
     * @param courseCode    Course code of course to be TA of
     * @param jwtHeader     JWT header for authentication
     * @throws ServiceErrorException Thrown if other services are down
     */
    public void updateAsTa(Long studentNumber, String courseCode, String jwtHeader)
            throws URISyntaxException, ServiceErrorException {
        // Add as hired TA to course microservice and user microservice
        courseCommunication.addHiredStudent(studentNumber, courseCode, jwtHeader);
        userCommunication.addTaRole(studentNumber, courseCode, jwtHeader);
    }


    /**
     * Retracts an submission with the given submission Id.
     * The submission will be deleted from database.
     *
     * @param submissionId Id of submission retracted
     * @param studentNumber Student number of retracted submission
     * @param retractDate   Date of retracting submission
     * @return Boolean indicating whether submission was retracted
     * @throws SubmissionNotFoundException Thrown when submission is not found
     * @throws CourseServiceErrorException  Thrown when course service is not available
     * @throws StudentUnauthorizedException Thrown when student is not authorized
     */
    public boolean retractSubmission(Long submissionId,
                                     Long studentNumber, Date retractDate, String jwtHeader)
            throws SubmissionNotFoundException, ServiceErrorException, ForbiddenException {
        Submission submission = getSubmission(submissionId);

        // Check whether user is authorized to retract the submission
        if (submission.getStudentNumber() != studentNumber) {
            throw new StudentUnauthorizedException("User unauthorized to retract this submission");
        }

        if (!canRetractSubmission(submission, retractDate, jwtHeader)) {
            return false;
        }

        // Delete submission from database
        deleteSubmission(submission, jwtHeader);
        return true;
    }

    /**
     * Gets the status of the submission.
     *
     * @param submissionId the id of the submission that needs to be retrieved
     * @return the status of the submission
     * @throws SubmissionNotFoundException if the submission does not exist
     */
    public Status getStatus(Long submissionId) throws SubmissionNotFoundException {
        Optional<Submission> submission = submissionRepository.getBySubmissionId(submissionId);

        if (submission.isEmpty()) {
            throw new SubmissionNotFoundException("Submission to the course can not be found!");
        }

        return submission.get().getStatus();
    }

    /**
     * Gets all the submissions for a course.
     *
     * @param courseCode   the id of the course
     * @param lecturerId the id of the lecturer that wants the submissions
     * @return a list of pending submissions
     * @throws CourseServiceErrorException   If there is a communication error
     * @throws LecturerUnauthorizedException if there is no lecturer for
     *                                       the required course with the specified id
     */
    public List<Submission> getAllPendingSubmissions(String courseCode, Long lecturerId,
                                                     String jwtHeader)
            throws ServiceErrorException, ForbiddenException {

        Set<Long> lecturers = courseCommunication.getLecturersForCourse(courseCode, jwtHeader);

        if (!lecturers.contains(lecturerId)) {
            throw new LecturerUnauthorizedException(
                    "Lecturer unauthorized to accept this submission");
        }


        Optional<List<Submission>> submissions = submissionRepository.getByCourseCode(courseCode);

        if (submissions.isEmpty()) {
            return new ArrayList<>();
        }

        return submissions.get().stream().filter(x -> x.getStatus() == Status.PENDING)
                .collect(Collectors.toList());
    }


    /**
     * Gets all the recommendations for a course.
     *
     * @param courseCode the id of the course we are looking for
     * @param lecturerId the id of the lecturer that made the request
     * @param strategy the recommendation strategy
     * @return a list of submissions
     */
    public List<RecommendedSubmission> getRecommendationsForCourse(String courseCode,
                                                                   Long lecturerId,
                                                                   DefaultStrategy strategy,
                                                                   String jwtHeader)
            throws LecturerUnauthorizedException, ServiceErrorException,
            IOException, InterruptedException {
        Set<Long> lecturers = courseCommunication.getLecturersForCourse(courseCode, jwtHeader);

        if (!lecturers.contains(lecturerId)) {
            throw new LecturerUnauthorizedException(
                    "Lecturer unauthorized to accept this submissions");
        }


        strategy.setSubmissionRepository(submissionRepository);
        strategy.setUserCommunication(userCommunication);
        strategy.setJwtHeader(jwtHeader);

        Recommendation r = new Recommendation(strategy);

        return r.getBestSubmissions(courseCode);
    }

}
