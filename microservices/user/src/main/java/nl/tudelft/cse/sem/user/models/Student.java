package nl.tudelft.cse.sem.user.models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.tudelft.cse.sem.user.serializers.StudentSerializer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "student")
@JsonSerialize(using = StudentSerializer.class)
//@EqualsAndHashCode(callSuper = false)
public class Student extends User {

    static final long serialVersionUID = 1L;

    private Long studentNumber;

    private Float rating = 0f; // 0 - not rated yet

    private Float ratingSum = 0f;

    private Integer numberOfRatings = 0;

    @ElementCollection(targetClass = Double.class, fetch = FetchType.EAGER)
    private Map<String, Double> grades = new HashMap<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> currentCourses = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> taCourses = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> roles = new HashSet<>();
        currentCourses.forEach(course ->
                roles.add(new SimpleGrantedAuthority("ROLE_STUDENT_"
                        + course.toUpperCase(Locale.ENGLISH))));
        taCourses.forEach(course ->
                roles.add(new SimpleGrantedAuthority("ROLE_TA_"
                        + course.toUpperCase(Locale.ENGLISH))));
        grades.forEach((course, grade) -> {
            roles.add(new SimpleGrantedAuthority("ROLE_ATTEMPTED_"
                    + course.toUpperCase(Locale.ENGLISH)));
        });
        roles.add(new SimpleGrantedAuthority("ROLE_NETID_" + netId));
        roles.add(new SimpleGrantedAuthority("ROLE_STUDENTNUMBER_" + studentNumber));
        return roles;
    }

    @Override
    public String getUsername() {
        return netId;
    }


    public void addTaCourse(String course) {
        taCourses.add(course);
    }

    /**
     * Adds a rating to a student.
     *
     * @param rating the rating received
     */
    public void addRating(Float rating) {
        numberOfRatings++;
        ratingSum += rating;
        this.rating = ratingSum / numberOfRatings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Student)) {
            return false;
        }
        Student student = (Student) o;
        return Objects.equals(studentNumber, student.studentNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentNumber);
    }
}
