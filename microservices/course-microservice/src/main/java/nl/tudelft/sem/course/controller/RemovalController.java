package nl.tudelft.sem.course.controller;

import javax.servlet.http.HttpServletResponse;
import nl.tudelft.sem.course.exceptions.CourseNotFoundException;
import nl.tudelft.sem.course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("courses/remove")
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class RemovalController {

    private final transient CourseService courseService;

    @Autowired
    public RemovalController(CourseService courseService) {
        this.courseService = courseService;
    }

    /**
     * End point for removing lecturer from lecturer list of a course.
     *
     * @param response   http response
     * @param lecturerId id of the lecturer
     * @param courseCode id of the course
     */
    @DeleteMapping("/lecturer/{courseCode}")
    public void removeLecturer(HttpServletResponse response,
                               @RequestParam Long lecturerId,
                               @PathVariable String courseCode) throws CourseNotFoundException {
        courseService.removeLecturer(lecturerId, courseCode);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * End point for removing student from candidate student list of a course.
     *
     * @param studentNumber id of the student
     * @param courseCode    id of the course
     */
    @DeleteMapping("/candidateStudent/{courseCode}")
    public void removeCandidateStudent(@RequestParam Long studentNumber,
                                       @PathVariable String courseCode)
            throws CourseNotFoundException {
        courseService.removeCandidateStudents(studentNumber, courseCode);
    }

    /**
     * End point for removing student from hired student list of a course.
     *
     * @param response      http response
     * @param studentNumber id of the student
     * @param courseCode    id of the course
     */
    @DeleteMapping("/hiredStudent/{courseCode}")
    public void removeHiredStudent(HttpServletResponse response,
                                   @RequestParam Long studentNumber,
                                   @PathVariable String courseCode) throws CourseNotFoundException {
        courseService.removeHiredStudents(studentNumber, courseCode);
        response.setStatus(HttpServletResponse.SC_OK);
    }



}
