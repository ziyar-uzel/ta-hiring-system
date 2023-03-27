package nl.tudelft.sem.submission.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import nl.tudelft.sem.submission.entities.Submission;
import nl.tudelft.sem.submission.exceptions.ServiceErrorException;
import nl.tudelft.sem.submission.utils.definition.NoStrategy;
import nl.tudelft.sem.submission.utils.strategy.models.RecommendedSubmission;
import org.junit.jupiter.api.Test;

public class RecommendationTest {

    private final transient NoStrategy recommendationStrategy =
            mock(NoStrategy.class);
    private final transient Recommendation recommendation =
            new Recommendation(recommendationStrategy);

    private final transient Submission submission = new Submission();
    private final transient  Submission submission2 = new Submission();
    private static final String courseString = "HowToDoASickBackFlip";

    @Test
    void getBestSubmissionTest() throws ServiceErrorException, IOException, InterruptedException {
        submission.setCourseCode(courseString);
        submission2.setCourseCode(courseString);
        List<RecommendedSubmission> list = new ArrayList<>();
        list.add(new RecommendedSubmission(1f, submission));
        list.add(new RecommendedSubmission(2f, submission2));


        when(recommendationStrategy.getBestApplications(courseString))
                .thenReturn(list);
        recommendation.setRecommendationStrategy(recommendationStrategy);

        assertEquals(list, recommendation.getBestSubmissions(courseString));
    }
}
