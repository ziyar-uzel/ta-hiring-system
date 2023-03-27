package nl.tudelft.sem.submission.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Map;
import nl.tudelft.sem.submission.controller.dto.SubmissionCreateRequest;
import nl.tudelft.sem.submission.exceptions.BadRequestException;
import nl.tudelft.sem.submission.exceptions.CourseServiceErrorException;
import nl.tudelft.sem.submission.exceptions.ForbiddenException;
import nl.tudelft.sem.submission.exceptions.LecturerUnauthorizedException;
import nl.tudelft.sem.submission.exceptions.ServiceErrorException;
import nl.tudelft.sem.submission.exceptions.SubmissionNotFoundException;
import nl.tudelft.sem.submission.services.SubmissionService;
import nl.tudelft.sem.submission.utils.definition.DefaultStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("submission")
public class SubmissionController {

    private static final String authorizationHeader = "Authorization";

    private final transient SubmissionService submissionService;


    @Autowired
    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    /**
     * End point for creating a TA submission.
     *
     * @param course Course id of the course applied to
     * @param body   Information about the submission
     * @return The resulting TA submission
     */
    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_STUDENTNUMBER_' + #body.studentNumber)")
    public ResponseEntity<Object> createSubmission(@RequestParam("course") String course,
                                               @RequestBody SubmissionCreateRequest body,
                                               @RequestHeader(authorizationHeader) String jwtHeader)
            throws BadRequestException, SubmissionNotFoundException {
        try {
            return ResponseEntity.ok(submissionService.createSubmission(body, course,
                    new Date(System.currentTimeMillis()), jwtHeader));
        } catch (ServiceErrorException | URISyntaxException | IOException e) {
            return internalServerError(e);
        }
    }

    /**
     * Accepts a TA submission for a particular course.
     *
     * @param submissionId Submission Id of the accepted submission
     * @return The updated submission
     */
    @PutMapping("/accept/{submissionId}")
    @PreAuthorize("hasRole('ROLE_LECTURER')")
    public ResponseEntity<Object> acceptSubmission(@PathVariable Long submissionId,
                                               @RequestParam("lecturerId") Long lecturerId,
                                               @RequestHeader(authorizationHeader) String jwtHeader)
            throws BadRequestException, SubmissionNotFoundException, ForbiddenException,
        IOException, InterruptedException {
        try {
            return ResponseEntity.ok(submissionService
                    .acceptSubmission(submissionId, lecturerId, jwtHeader));
        } catch (ServiceErrorException | URISyntaxException e) {
            return internalServerError(e);
        }

    }

    /**
     * Retracts an submission if it is not accepted or too late.
     *
     * @param submissionId Id of the submission retracted
     * @param studentNumber Request parameter with student Id
     * @return Boolean indicating whether operation was a success
     * @throws CourseServiceErrorException  Thrown when course service is not available
     * @throws SubmissionNotFoundException Thrown when submission is not found
     */
    @PutMapping("/retract/{submissionId}")
    @PreAuthorize("hasRole('ROLE_STUDENTNUMBER_' + #studentNumber)")
    public ResponseEntity<Object> retractSubmission(@PathVariable Long submissionId,
                                        @RequestParam("studentNumber") Long studentNumber,
                                        @RequestHeader(authorizationHeader) String jwtHeader)
            throws SubmissionNotFoundException, ForbiddenException {
        try {
            return ResponseEntity.ok(Map.of("retracted",
                    submissionService.retractSubmission(submissionId,
                    studentNumber,
                        new Date(System.currentTimeMillis()),
                    jwtHeader)));
        } catch (ServiceErrorException e) {
            return internalServerError(e);
        }
    }

    /**
     * Endpoint for requesting the status of an submission.
     *
     * @param submissionId the submission id of the submission
     * @return the status (PENDING, RETRACTED, ACCEPTED, REJECTED)
     */
    @GetMapping("/status")
    @PreAuthorize("hasAnyRole('ROLE_LECTURER', 'ROLE_STUDENTNUMBER_' + #studentNumber)")
    public ResponseEntity<Object> getStatus(@RequestParam Long submissionId,
                                            @RequestParam Long studentNumber)
            throws SubmissionNotFoundException {
        return ResponseEntity.ok(Map.of("status", submissionService.getStatus(submissionId)));
    }



    /**
     * Endpoint for lecturers to see all the submissions.
     *
     * @param courseId   the course id for the interested submissions
     * @param lecturerId the lecturer id that made the request
     * @return a list of submissions for the course
     */
    @GetMapping("/getAllPendingSubmissions")
    @PreAuthorize("hasRole('ROLE_LECTURER')")
    public ResponseEntity<Object> getAllPendingSubmissions(
            @RequestParam("courseId") String courseId,
            @RequestParam("lecturerId") Long lecturerId,
            @RequestHeader(authorizationHeader) String jwtHeader) throws ForbiddenException {
        try {
            return ResponseEntity.ok(submissionService
                    .getAllPendingSubmissions(courseId, lecturerId, jwtHeader));
        } catch (ServiceErrorException e) {
            return internalServerError(e);
        }
    }

    /**
     * Returns a list of submissions in an order of an recommendation system.
     * Note: The ServiceErrorException is thrown, not caught
     *
     * @param courseId           the course for the submission
     * @param lecturerId         the lecturer that made the request
     * @param strategy           the type of recommendation
     * @return a response entity containing a list of recommendation.
     */
    @GetMapping("/getRecommendationsForCourse")
    @PreAuthorize("hasRole('ROLE_LECTURER')")
    public ResponseEntity<Object> getRecommendationsForCourse(
            @RequestParam String courseId,
            @RequestParam Long lecturerId,
            @RequestBody DefaultStrategy strategy,
            @RequestHeader(authorizationHeader) String jwtHeader)
            throws LecturerUnauthorizedException, ServiceErrorException,
            IOException, InterruptedException {
        return ResponseEntity.ok(submissionService
                .getRecommendationsForCourse(courseId, lecturerId, strategy, jwtHeader));
    }


    private ResponseEntity<Object> internalServerError(Exception exception) {
        return new ResponseEntity<>(Map.of("error-message",
                exception.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
