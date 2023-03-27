package nl.tudelft.sem.submission.utils.strategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import nl.tudelft.sem.submission.communication.UserCommunication;
import nl.tudelft.sem.submission.entities.StudentGrade;
import nl.tudelft.sem.submission.entities.Submission;
import nl.tudelft.sem.submission.exceptions.ServiceErrorException;
import nl.tudelft.sem.submission.repositories.SubmissionRepository;
import nl.tudelft.sem.submission.utils.definition.DefaultStrategy;
import nl.tudelft.sem.submission.utils.strategy.models.RecommendedSubmission;

public class GradeStrategy extends DefaultStrategy {

    public GradeStrategy(){

    }

    public GradeStrategy(SubmissionRepository applicationRepository,
                         UserCommunication userCommunication) {
        super(applicationRepository, userCommunication);
    }

    public GradeStrategy(SubmissionRepository applicationRepository,
                         UserCommunication userCommunication, String jwtHeader) {
        super(applicationRepository, userCommunication, jwtHeader);
    }

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

        List<StudentGrade> studentGradeResponses =
                userCommunication.getGradesByCourse(studentNumbers, courseCode, this.jwtHeader);

        for (Submission submission : allPendingSubmissions) {
            for (StudentGrade studentGrade : studentGradeResponses) {
                if (studentGrade.getStudentNumber().equals(submission.getStudentNumber())) {
                    recommendedSubmissions
                            .add(new RecommendedSubmission(studentGrade.getGrade(), submission));
                    break;
                }
            }
        }


        Collections.sort(recommendedSubmissions);

        return recommendedSubmissions;
    }

}
