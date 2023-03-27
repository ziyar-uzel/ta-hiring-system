package nl.tudelft.sem.submission.entities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "contract")
@Data
@NoArgsConstructor
public class Contract {

    @Id
    private String bucketKey;
    private Long studentNumber;
    private String courseCode;


    /**
     * Constructor for a contract.
     *
     * @param studentNumber Student number for student it is created
     * @param courseCode    Course to TA for
     */
    public Contract(Long studentNumber, String courseCode) {
        this.studentNumber = studentNumber;
        this.courseCode = courseCode;
        this.bucketKey = "contract-" + this.studentNumber + "-" + this.courseCode + "-"
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"))
                + ".pdf";
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Contract contract = (Contract) o;
        return Objects.equals(bucketKey, contract.bucketKey)
                && Objects.equals(studentNumber, contract.studentNumber)
                && Objects.equals(courseCode, contract.courseCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bucketKey, studentNumber, courseCode);
    }
}
