package nl.tudelft.sem.course.controller;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletResponse;
import nl.tudelft.sem.course.entity.Course;
import nl.tudelft.sem.course.exceptions.CourseNotFoundException;
import nl.tudelft.sem.course.exceptions.TaRatioExceededException;
import nl.tudelft.sem.course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("courses")
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class CourseController {

    private final transient CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    /**
     * End point for getting course.
     *
     * @param courseCode id of the course
     * @return Course with respect to given id
     */

    @GetMapping("/{courseCode}")
    public ResponseEntity<Object> getCourse(@PathVariable String courseCode)
            throws CourseNotFoundException {
        Course course = courseService.findByCourseCode(courseCode);
        return new ResponseEntity<>(course, HttpStatus.OK);
    }

    /**
     * End point for getting the number of students within a course.
     *
     * @param courseCode id of the course
     * @return number of students in a course with courseId
     */
    @GetMapping("/studentNumber/{courseCode}")
    public ResponseEntity<Object> getNumberOfStudents(@PathVariable String courseCode)
            throws CourseNotFoundException {
        Course course = courseService.findByCourseCode(courseCode);
        int numberOfStudents = course.getNumOfStudents();
        return new ResponseEntity<>(Map.of("numberOfStudents", numberOfStudents), HttpStatus.OK);
    }

    /**
     * End point for getting lecturers of a course.
     *
     * @param courseCode id of the course
     * @return lecturer list of the course with courseId
     */
    @GetMapping("/lecturers/{courseCode}")
    public ResponseEntity<Object> getLecturers(@PathVariable String courseCode)
            throws CourseNotFoundException {
        Course course = courseService.findByCourseCode(courseCode);
        Set<Long> lecturers = course.getLecturers();
        return new ResponseEntity<>(Map.of("lecturers", lecturers), HttpStatus.OK);
    }


    /**
     * End point for getting the description of a course.
     *
     * @param courseCode id of the course
     * @return description of the course with courseId
     */
    @GetMapping("/description/{courseCode}")
    public ResponseEntity<Object> getDescription(@PathVariable String courseCode)
            throws CourseNotFoundException {
        Course course = courseService.findByCourseCode(courseCode);
        String description = course.getDescription();
        return new ResponseEntity<>(Map.of("description", description), HttpStatus.OK);
    }

    /**
     * End point for getting the start date of a course.
     *
     * @param courseCode id of the course
     * @return start date of the course with courseId
     */
    @GetMapping("/startDate/{courseCode}")
    public ResponseEntity<Object> getStartDate(@PathVariable String courseCode)
            throws CourseNotFoundException {
        Course course = courseService.findByCourseCode(courseCode);
        Date date = course.getStartDate();
        return new ResponseEntity<>(Map.of("startDate", date), HttpStatus.OK);
    }


    /**
     * End point for getting the courses whose started date is
     * more than 3 weeks away from the current date.
     *
     * @return the course list.
     */
    @GetMapping("/availableCourses")
    public ResponseEntity<Object> getAvailableCourses() {
        return new ResponseEntity<>(courseService.availableCourses(), HttpStatus.OK);
    }


    /**
     * End point for creating a course with given course JSON object.
     *
     * @param request Course object
     * @return created course.
     */
    @PostMapping("/create")
    public ResponseEntity<Object> createCourse(@RequestBody Course request) {
        return ResponseEntity.ok(courseService.createCourse(request));
    }

    /**
     * End point for getting the required number of TAs of a course.
     *
     * @param courseCode id of the course
     * @return required number of TAs of the course with courseId
     */
    @GetMapping("/requiredNumberOfTa/{courseCode}")
    public ResponseEntity<Object> getRequiredNumberOfTas(@PathVariable String courseCode)
            throws CourseNotFoundException {
        Course course = courseService.findByCourseCode(courseCode);
        int size = course.getNumOfStudents();
        int requiredTas = (int) Math.ceil((double) size / 20.0);
        return new ResponseEntity<>(Map.of("requiredTas", requiredTas), HttpStatus.OK);
    }

    /**
     * End point for getting the admission rate of the course.
     *
     * @param courseCode id of the course
     * @return admission rate(in percentage) of the course with courseId
     */
    @GetMapping("/admissionRate/{courseCode}")
    public ResponseEntity<Object> getAdmissionRate(@PathVariable String courseCode)
            throws CourseNotFoundException {
        Course course = courseService.findByCourseCode(courseCode);
        int candidateStudentSize = course.getCandidateStudents().size();
        int hiredStudentSize = course.getHiredStudents().size();
        String percentage;
        if (candidateStudentSize == 0 || hiredStudentSize == 0) {
            percentage = "-";
        } else {
            int admissionRate = (hiredStudentSize * 100) / candidateStudentSize;
            percentage = admissionRate + "%";
        }
        return new ResponseEntity<>(Map.of("admissionRate", percentage), HttpStatus.OK);
    }


}
