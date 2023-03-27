package nl.tudelft.sem.course.entity;


import java.util.Date;
import java.util.Objects;
import java.util.Set;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;



@NoArgsConstructor
@Data
@Entity
@Table(name = "courses")
public class Course {

    @Id
    private String courseCode;

    @Column
    private Date startDate;

    @Column
    private Date endDate;

    @Column
    private int numOfStudents;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "course_lecturer", joinColumns = @JoinColumn(name = "course_id"))
    private Set<Long> lecturers;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "course_candidate_student",
            joinColumns = @JoinColumn(name = "course_id"))
    private Set<Long> candidateStudents;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "course_hired_student", joinColumns = @JoinColumn(name = "course_id"))
    private Set<Long> hiredStudents;

    @Column
    private String description;

    /**
     * adds given student id to the hired students field under course.
     */
    public void addHiredStudents(Long studentId) {
        hiredStudents.add(studentId);
    }

    /**
     * adds given student id to the candidate students field under course.
     */
    public void addCandidateStudents(Long studentId) {
        candidateStudents.add(studentId);
    }



    /**
     * adds given lecturer id to the lecturers field under course.
     */
    public void addLecturer(Long studentId) {
        lecturers.add(studentId);
    }

    public void removeCandidateStudent(Long studentId) {
        candidateStudents.remove(studentId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Course)) {
            return false;
        }
        Course course = (Course) o;
        return getCourseCode().equals(course.getCourseCode())
                && Objects.equals(getStartDate(), course.getStartDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCourseCode(), getStartDate());
    }

}