package nl.tudelft.sem.submission.utils.strategy.models;


import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import nl.tudelft.sem.submission.entities.Submission;

@Getter
@Setter
public class RecommendedSubmission implements Comparable<RecommendedSubmission> {

    protected Float score;
    private Submission submission;

    public RecommendedSubmission(Float score, Submission submission) {
        this.score = score;
        this.submission = submission;
    }

    @Override
    public int compareTo(RecommendedSubmission o) {
        return -Float.compare(this.score, o.score);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RecommendedSubmission)) {
            return false;
        }
        RecommendedSubmission that = (RecommendedSubmission) o;
        return Objects.equals(score, that.score)
                && Objects.equals(submission, that.submission);
    }

}
