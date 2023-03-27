package nl.tudelft.cse.sem.user.models;

import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentRating {
    Long studentNumber;
    Float rating;


    public StudentRating(Long studentNumber, Float rating) {
        this.studentNumber = studentNumber;
        this.rating = rating;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StudentRating)) {
            return false;
        }
        StudentRating that = (StudentRating) o;
        return Objects.equals(studentNumber, that.studentNumber)
                && Objects.equals(rating, that.rating);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentNumber, rating);
    }
}
