package nl.tudelft.sem.submission.controller.dto;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SubmissionCreateRequest {

    private Long studentNumber;
    private String studentEmail;

    public SubmissionCreateRequest(){}

    @Override
    public boolean equals(Object o) {
        if (this == o)  {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SubmissionCreateRequest that = (SubmissionCreateRequest) o;
        return Objects.equals(studentNumber, that.studentNumber)
                && Objects.equals(studentEmail, that.studentEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentNumber, studentEmail);
    }
}

