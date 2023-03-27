package nl.tudelft.sem.submission.utils.definition;

import java.io.IOException;
import java.util.List;
import nl.tudelft.sem.submission.entities.Submission;
import nl.tudelft.sem.submission.exceptions.ServiceErrorException;
import nl.tudelft.sem.submission.utils.strategy.models.RecommendedSubmission;

public interface NoStrategy {
    List<RecommendedSubmission> getBestApplications(String courseId)
            throws ServiceErrorException, IOException, InterruptedException;
}
