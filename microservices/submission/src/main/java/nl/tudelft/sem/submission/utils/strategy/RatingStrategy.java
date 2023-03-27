package nl.tudelft.sem.submission.utils.strategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import nl.tudelft.sem.submission.communication.UserCommunication;
import nl.tudelft.sem.submission.entities.StudentRating;
import nl.tudelft.sem.submission.entities.Submission;
import nl.tudelft.sem.submission.exceptions.ServiceErrorException;
import nl.tudelft.sem.submission.repositories.SubmissionRepository;
import nl.tudelft.sem.submission.utils.definition.DefaultStrategy;
import nl.tudelft.sem.submission.utils.strategy.models.RecommendedSubmission;

public class RatingStrategy extends DefaultStrategy {

    public RatingStrategy(){

    }

    public RatingStrategy(SubmissionRepository applicationRepository,
                          UserCommunication userCommunication) {
        super(applicationRepository, userCommunication);
    }

    public RatingStrategy(SubmissionRepository applicationRepository,
                          UserCommunication userCommunication, String jwtHeader) {
        super(applicationRepository, userCommunication, jwtHeader);
    }


    /**
     * Returns a list of all pending submissions for a course sorted by the rating of the student.
     *
     * @param courseCode the course code in which we are interested
     * @return A list of submissions
     * @throws ServiceErrorException if the user service throws an error
     */
    @Override
    @SuppressWarnings("PMD")
    public List<RecommendedSubmission> getBestApplications(String courseCode)
            throws ServiceErrorException {
        List<RecommendedSubmission> recommendedSubmissions = new ArrayList<>();

        List<Long> studentNumbers = new ArrayList<>();
        List<Submission> allPendingSubmissions = getAllPendingSubmissions(courseCode);
        for (Submission submission : allPendingSubmissions) {
            studentNumbers.add(submission.getStudentNumber());
        }

        List<StudentRating> studentRatingResponses =
                userCommunication.getRatings(studentNumbers, this.jwtHeader);

        for (Submission submission : allPendingSubmissions) {
            for (StudentRating studentRating : studentRatingResponses) {
                if (studentRating.getStudentNumber().equals(submission.getStudentNumber())) {
                    recommendedSubmissions
                            .add(new RecommendedSubmission(studentRating.getRating(), submission));
                    break;
                }
            }
        }


        Collections.sort(recommendedSubmissions);

        return recommendedSubmissions;
    }


}
