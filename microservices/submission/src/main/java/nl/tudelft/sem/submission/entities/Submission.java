package nl.tudelft.sem.submission.entities;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Submission {

    @Id
    @GeneratedValue
    private Long submissionId;
    private String courseCode;
    private Long studentNumber;
    private String studentEmail;
    private Status status;
    private Float amountOfHours;
    private Float studentGpa;
    private String description;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Submission that = (Submission) o;
        // Submission IDs do not have to equal
        return Objects.equals(courseCode, that.courseCode)
                && Objects.equals(studentNumber, that.studentNumber)
                && Objects.equals(studentEmail, that.studentEmail)
                && status == that.status
                && Objects.equals(amountOfHours, that.amountOfHours)
                && Objects.equals(studentGpa, that.studentGpa)
                && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(submissionId, courseCode, studentNumber,
                studentEmail, status, amountOfHours, studentGpa, description);
    }
}
