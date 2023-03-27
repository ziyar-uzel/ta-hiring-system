package nl.tudelft.sem.submission.utils;

import java.io.IOException;
import java.util.List;
import nl.tudelft.sem.submission.entities.Submission;
import nl.tudelft.sem.submission.exceptions.ServiceErrorException;
import nl.tudelft.sem.submission.utils.definition.NoStrategy;
import nl.tudelft.sem.submission.utils.strategy.models.RecommendedSubmission;

public class Recommendation {

    private transient NoStrategy noStrategy;

    public Recommendation(NoStrategy noStrategy) {
        this.noStrategy = noStrategy;
    }

    public List<RecommendedSubmission> getBestSubmissions(String courseCode)
            throws ServiceErrorException, IOException, InterruptedException {
        return noStrategy.getBestApplications(courseCode);
    }

    public NoStrategy getRecommendationStrategy() {
        return noStrategy;
    }

    public void setRecommendationStrategy(NoStrategy noStrategy) {
        this.noStrategy = noStrategy;
    }
}
