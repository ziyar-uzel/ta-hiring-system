package nl.tudelft.cse.sem.user.models;

import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentGrade {
    Long studentNumber;
    Double grade;

    public StudentGrade(Long studentNumber, Double grade) {
        this.studentNumber = studentNumber;
        this.grade = grade;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StudentGrade)) {
            return false;
        }
        StudentGrade that = (StudentGrade) o;
        return Objects.equals(studentNumber, that.studentNumber)
                && Objects.equals(grade, that.grade);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentNumber, grade);
    }
}
