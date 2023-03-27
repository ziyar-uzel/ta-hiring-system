package nl.tudelft.sem.course.controller;

import javax.servlet.http.HttpServletResponse;
import nl.tudelft.sem.course.exceptions.CourseNotFoundException;
import nl.tudelft.sem.course.exceptions.TaRatioExceededException;
import nl.tudelft.sem.course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("courses/add")
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class AdditionController {

    private final transient CourseService courseService;

    @Autowired
    public AdditionController(CourseService courseService) {
        this.courseService = courseService;
    }


    /**
     * End point for adding student to the hired student list of a course.
     *
     * @param response      http response
     * @param studentNumber id of the student
     * @param courseCode    id of the course
     */

    @PutMapping("/hiredStudent/{courseCode}")
    public void addHiredStudent(HttpServletResponse response,
                                @RequestParam Long studentNumber,
                                @PathVariable String courseCode)
            throws CourseNotFoundException, TaRatioExceededException {
        courseService.addHiredStudents(studentNumber, courseCode);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * End point for adding student to the candidate student list of a course.
     *
     * @param response      http response
     * @param studentNumber id of the student
     * @param courseCode    id of the course
     */
    @PutMapping("/candidateStudent/{courseCode}")
    public void addCandidateStudent(HttpServletResponse response,
                                @RequestParam Long studentNumber,
                                @PathVariable String courseCode) throws CourseNotFoundException {
        courseService.addCandidateStudents(studentNumber, courseCode);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * End point for adding lecturer to the lecturer list of a course.
     *
     * @param response   http response
     * @param lecturerId id of the lecturer
     * @param courseCode id of the course
     */
    @PutMapping("/lecturer/{courseCode}")
    public void addLecturer(HttpServletResponse response,
                            @RequestParam Long lecturerId,
                            @PathVariable String courseCode) throws CourseNotFoundException {
        courseService.addLecturer(lecturerId, courseCode);
        response.setStatus(HttpServletResponse.SC_OK);
    }




}
