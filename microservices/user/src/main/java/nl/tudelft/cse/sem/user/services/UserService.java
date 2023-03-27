package nl.tudelft.cse.sem.user.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.SneakyThrows;
import nl.tudelft.cse.sem.user.exceptions.CourseNotFoundException;
import nl.tudelft.cse.sem.user.exceptions.StudentNotFoundException;
import nl.tudelft.cse.sem.user.models.Student;
import nl.tudelft.cse.sem.user.models.StudentGrade;
import nl.tudelft.cse.sem.user.models.StudentRating;
import nl.tudelft.cse.sem.user.models.User;
import nl.tudelft.cse.sem.user.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    private static final String doesNotExist = " does not exist";
    private static final String STUDENT_WITH_SN = "Student with student number ";

    private final transient UserRepository userRepository;

    /**
     * Constructor.
     *
     * @param userRepository db where all users are stored
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Finds User by username, which is netid.
     *
     * @param netid here is username of user
     * @return User
     * @throws UsernameNotFoundException can be thrown when user is not found
     */
    @SneakyThrows
    @Override
    public User loadUserByUsername(String netid) {
        Optional<User> user = userRepository.findById(netid);
        return user.orElseThrow(() ->
                new StudentNotFoundException("User: " + netid + " does not exist."));
    }

    /**
     * Retrieves grade of specific student for specific course.
     *
     * @param studentNumber to find specific student
     * @param courseCode    to find specific course
     * @return grade
     * @throws CourseNotFoundException  if student did not take this couse
     * @throws StudentNotFoundException if student does not exist
     */
    public Double findGradeByStudentNumberAndCourseCode(Long studentNumber, String courseCode)
            throws CourseNotFoundException, StudentNotFoundException {
        Optional<Student> student = userRepository
                .findStudentByStudentNumber(studentNumber);
        if (student.isPresent()) {
            if (student.get().getGrades().containsKey(courseCode)) {
                return student.get().getGrades().get(courseCode);
            } else {
                throw new CourseNotFoundException("Course "
                        + courseCode + doesNotExist);
            }
        } else {
            throw new StudentNotFoundException(STUDENT_WITH_SN
                    + studentNumber + doesNotExist);
        }
    }


    /**
     * Finds gpa of specific student.
     *
     * @param studentNumber to find specific student
     * @return gpa
     * @throws StudentNotFoundException if student does not exist
     */
    public Double getGpaByStudentNumber(Long studentNumber) throws StudentNotFoundException {
        Optional<Student> student = userRepository.findStudentByStudentNumber(studentNumber);

        if (student.isPresent()) {
            return student.get().getGrades().values().stream()
                    .mapToDouble(Double::doubleValue).sum()
                    / student.get().getGrades().size();
            // I use java streams to calculate grade in 2 lines
        } else {
            throw new StudentNotFoundException(STUDENT_WITH_SN
                    + studentNumber + doesNotExist);
        }
    }

    /**
     * Adds student as TA.
     *
     * @param studentId Student Id of student to become TA
     * @param courseId  Course code to TA in
     * @throws StudentNotFoundException Thrown if student not found
     * @throws CourseNotFoundException  Thrown if course not found
     */
    public void addTaRole(Long studentId, String courseId)
            throws StudentNotFoundException, CourseNotFoundException {
        Optional<Student> studentByStudentNumber =
                userRepository.findStudentByStudentNumber(studentId);
        if (studentByStudentNumber.isEmpty()) {
            throw new StudentNotFoundException(STUDENT_WITH_SN
                    + studentId + doesNotExist);
        }
        if (studentByStudentNumber.get().getGrades().containsKey(courseId)) {
            studentByStudentNumber.get().addTaCourse(courseId);
            userRepository.save(studentByStudentNumber.get());
        } else {
            throw new CourseNotFoundException("Student does not have course: " + courseId);
        }
    }

    /**
     * Rates a student.
     *
     * @param studentId the student id determining the student
     * @param rating the rating
     * @throws StudentNotFoundException if the student does not exist
     */
    public void rateStudent(Long studentId, Float rating) throws StudentNotFoundException {
        Optional<Student> student =
                userRepository.findStudentByStudentNumber(studentId);

        if (student.isEmpty()) {
            throw new StudentNotFoundException(STUDENT_WITH_SN
                    + studentId + doesNotExist);
        }

        student.get().addRating(rating);
        userRepository.save(student.get());
    }

    /**
     * Gets the rating of a student.
     *
     * @param studentId the requested student
     * @return a student rating
     * @throws StudentNotFoundException if the student does not exist
     */
    public StudentRating getRatingOfStudent(Long studentId) throws StudentNotFoundException {
        Optional<Student> student =
                userRepository.findStudentByStudentNumber(studentId);

        if (student.isEmpty()) {
            throw new StudentNotFoundException("Student with student number "
                    + studentId + doesNotExist);
        }

        return new StudentRating(student.get().getStudentNumber(), student.get().getRating());
    }

    /**
     * Gets the ratings of multiple students.
     *
     * @param studentNumbers the student numbers of the requested students
     * @return  a list of student ratings
     */
    public List<StudentRating> findRatingsOfStudents(List<Long> studentNumbers) {
        List<Student> studentsByStudentNumbers =
                userRepository.findStudentsByStudentNumbers(studentNumbers);
        List<StudentRating> studentRatings = new ArrayList<>();

        for (Student student : studentsByStudentNumbers) {
            studentRatings
                    .add(new StudentRating(student.getStudentNumber(), student.getRating()));

        }

        return studentRatings;
    }

    /**
     * Gets the ratings of multiple students for some course.
     *
     * @param courseCode the code of the course
     * @param studentNumbers the student numbers of the requested students
     * @return  a list of student grades
     */
    public List<StudentGrade> findGradesOfStudents(String courseCode, List<Long> studentNumbers) {
        List<Student> studentsByStudentNumbers =
                userRepository.findStudentsByStudentNumbers(studentNumbers);
        List<StudentGrade> studentGrades = new ArrayList<>();

        for (Student student : studentsByStudentNumbers) {
            if (student.getGrades().containsKey(courseCode)) {
                studentGrades
                        .add(new StudentGrade(student.getStudentNumber(),
                                (student.getGrades().get(courseCode))));
            }
        }

        return studentGrades;
    }

    public List<Student> getInfoOfEachTaForCourse(String courseCode) {
        return userRepository.findTasForCourse(courseCode);
    }
}
