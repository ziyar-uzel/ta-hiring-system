package nl.tudelft.sem.submission.utils.definition;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import nl.tudelft.sem.submission.communication.UserCommunication;
import nl.tudelft.sem.submission.entities.Status;
import nl.tudelft.sem.submission.entities.Submission;
import nl.tudelft.sem.submission.exceptions.ServiceErrorException;
import nl.tudelft.sem.submission.repositories.SubmissionRepository;
import nl.tudelft.sem.submission.utils.strategy.GradeStrategy;
import nl.tudelft.sem.submission.utils.strategy.RatingStrategy;
import nl.tudelft.sem.submission.utils.strategy.models.RecommendedSubmission;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = GradeStrategy.class, name = "grade"),
    @JsonSubTypes.Type(value = RatingStrategy.class, name = "rating")
})
public abstract class DefaultStrategy implements NoStrategy {
    @JsonIgnore
    protected transient SubmissionRepository submissionRepository;
    @JsonIgnore
    protected transient UserCommunication userCommunication;
    @JsonIgnore
    protected String jwtHeader;

    public DefaultStrategy() {

    }

    public DefaultStrategy(SubmissionRepository submissionRepository,
                           UserCommunication userCommunication) {
        this.submissionRepository = submissionRepository;
        this.userCommunication = userCommunication;
    }

    /**
     * Constructor.
     */
    public DefaultStrategy(SubmissionRepository submissionRepository,
                           UserCommunication userCommunication, String jwtHeader) {
        this.submissionRepository = submissionRepository;
        this.userCommunication = userCommunication;
        this.jwtHeader = jwtHeader;
    }

    /*@Override
    public List<RecommendedSubmission> getBestApplications(String courseCode)
            throws ServiceErrorException {
        List<Submission> submissions = getAllPendingSubmissions(courseCode);
        return submissions.stream().map(x -> new RecommendedSubmission(0f, x))
                .collect(Collectors.toList());
    }*/

    protected List<Submission> getAllPendingSubmissions(String courseCode) {
        Optional<List<Submission>> applicationsOpt = submissionRepository
                .getByCourseCode(courseCode);

        if (applicationsOpt.isEmpty()) {
            return new ArrayList<>();
        }

        return applicationsOpt.get().stream()
                .filter(x -> x.getStatus() == Status.PENDING).collect(Collectors.toList());
    }

    public String getJwtHeader() {
        return jwtHeader;
    }


    public SubmissionRepository getSubmissionRepository() {
        return submissionRepository;
    }

    public UserCommunication getUserCommunication() {
        return userCommunication;
    }

    public void setJwtHeader(String jwtHeader) {
        this.jwtHeader = jwtHeader;
    }

    public void setSubmissionRepository(SubmissionRepository submissionRepository) {
        this.submissionRepository = submissionRepository;
    }

    public void setUserCommunication(UserCommunication userCommunication) {
        this.userCommunication = userCommunication;
    }
}
