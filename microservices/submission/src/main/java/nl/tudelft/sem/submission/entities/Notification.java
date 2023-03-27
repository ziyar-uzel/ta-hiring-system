package nl.tudelft.sem.submission.entities;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long notifiedStudentNumber;

    private String notifiedEmail;

    private Status status;

    private String notifiedCourseId;

    public void setNotifiedStudentNumber(Long notifiedStudentNumber) {
        this.notifiedStudentNumber = notifiedStudentNumber;
    }

    public void setNotifiedEmail(String notifiedEmail) {
        this.notifiedEmail = notifiedEmail;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setNotifiedCourseId(String notifiedCourseId) {
        this.notifiedCourseId = notifiedCourseId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Notification that = (Notification) o;
        return Objects.equals(notifiedStudentNumber, that.notifiedStudentNumber)
            && Objects.equals(notifiedEmail, that.notifiedEmail)
            && status == that.status
            && Objects.equals(notifiedCourseId, that.notifiedCourseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(notifiedStudentNumber, notifiedEmail, status, notifiedCourseId);
    }
}
