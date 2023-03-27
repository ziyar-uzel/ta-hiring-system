package nl.tudelft.cse.sem.user.controllers;

import java.util.List;
import java.util.Map;
import nl.tudelft.cse.sem.user.exceptions.CourseNotFoundException;
import nl.tudelft.cse.sem.user.exceptions.StudentNotFoundException;
import nl.tudelft.cse.sem.user.models.Student;
import nl.tudelft.cse.sem.user.models.StudentGrade;
import nl.tudelft.cse.sem.user.models.StudentRating;
import nl.tudelft.cse.sem.user.models.User;
import nl.tudelft.cse.sem.user.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class UserController {

    private final transient UserService userService;


    /**
     * Constructor.
     *
     * @param userService to decouple controller from db connection and internal logic
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Gets user by netid.
     *
     * @param netid of user
     * @return ResponseEntity of User, either Student or Lecturer
     */
    @GetMapping("/{netid}")
    @PreAuthorize("hasAnyRole({'ROLE_LECTURER', 'ROLE_NETID_' + #netid})")
    public ResponseEntity<Object> getUserByNetid(@PathVariable String netid) {
        try {
            User user = userService.loadUserByUsername(netid);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Endpoint to retrieve only grade (not JSON) of specific student for specific course.
     *
     * @param courseCode to find specific course
     * @param studentNumber to find specific student
     * @return grade only (not JSON)
     * @throws CourseNotFoundException if student did not take this course
     * @throws StudentNotFoundException if student does not exist
     */
    @GetMapping("/course")
    @PreAuthorize("hasAnyRole({'ROLE_LECTURER', 'ROLE_STUDENTNUMBER_' + #studentNumber})")
    public ResponseEntity<Object> getGradeByCourse(
            @RequestParam("code") String courseCode,
            @RequestParam("studentNumber") Long studentNumber)
            throws CourseNotFoundException, StudentNotFoundException {

        Double grade = userService
                .findGradeByStudentNumberAndCourseCode(studentNumber, courseCode);
        return new ResponseEntity<>(Map.of("grade", grade), HttpStatus.OK);
    }

    /**
     * Endpoint to retrieve only gpa (not JSON) of specific student.
     *
     * @param studentNumber to find specific student
     * @return gpa only (not JSON)
     * @throws StudentNotFoundException if student does not exist
     */
    @GetMapping("/gpa")
    @PreAuthorize("hasAnyRole({'ROLE_LECTURER', 'ROLE_STUDENTNUMBER_' + #studentNumber})")
    public ResponseEntity<Object> getGpaByStudent(@RequestParam("studentNumber") Long studentNumber)
            throws StudentNotFoundException {
        Double gpa = userService
                .getGpaByStudentNumber(studentNumber);
        //TODO: possibly refactor method to return gpa along with amount of taken courses
        return new ResponseEntity<>(Map.of("gpa", gpa), HttpStatus.OK);
    }

    @PutMapping("/addTaRole/{courseCode}")
    @PreAuthorize("hasRole('ROLE_LECTURER')")
    public ResponseEntity<Object> addTaRole(@PathVariable("courseCode") String courseId,
                                            @RequestParam("studentNumber") Long studentNumber)
            throws StudentNotFoundException, CourseNotFoundException {
        userService.addTaRole(studentNumber, courseId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/rating")
    @PreAuthorize("hasAnyRole({'ROLE_LECTURER', 'ROLE_STUDENTNUMBER_' + #studentNumber})")
    public ResponseEntity<Object> getRatingOfStudent(
                        @RequestParam("studentNumber") Long studentNumber)
            throws StudentNotFoundException {

        return new ResponseEntity<>(userService.getRatingOfStudent(studentNumber), HttpStatus.OK);
    }

    @PutMapping("/rate")
    @PreAuthorize("hasRole('ROLE_LECTURER')")
    public ResponseEntity<Object> rateStudent(@RequestParam Long studentNumber,
                                              @RequestParam Float rating)
            throws StudentNotFoundException {
        userService.rateStudent(studentNumber, rating);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/ratings")
    @PreAuthorize("hasRole('ROLE_LECTURER')")
    public ResponseEntity<Object> getRatingsOfStudents(@RequestBody List<Long> studentNumbers) {
        List<StudentRating> studentRatings = userService.findRatingsOfStudents(studentNumbers);
        return new ResponseEntity<>(studentRatings, HttpStatus.OK);
    }


    @PostMapping("/courseGrades")
    @PreAuthorize("hasRole('ROLE_LECTURER')")
    public ResponseEntity<Object> getGradesOfStudents(@RequestParam("courseCode") String courseCode,
                                                      @RequestBody List<Long> studentNumbers) {
        return new ResponseEntity<>(userService.findGradesOfStudents(courseCode, studentNumbers),
                HttpStatus.OK);
    }

    @GetMapping("/tas/info/course/{courseCode}")
    @PreAuthorize("hasRole('ROLE_LECTURER')")
    public ResponseEntity<Object> getInfoOfEachTaForCourse(@PathVariable String courseCode) {
        return new ResponseEntity<>(userService.getInfoOfEachTaForCourse(courseCode),
                HttpStatus.OK);
    }

}
