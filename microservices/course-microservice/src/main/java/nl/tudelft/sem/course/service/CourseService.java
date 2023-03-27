package nl.tudelft.sem.course.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import nl.tudelft.sem.course.entity.Course;
import nl.tudelft.sem.course.exceptions.CourseNotFoundException;
import nl.tudelft.sem.course.exceptions.TaRatioExceededException;
import nl.tudelft.sem.course.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CourseService {

    private final transient CourseRepository courseRepository;

    @Autowired
    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }


    /**
     * Finds course by course code.
     *
     * @param courseCode Course code of course to be retrieved
     * @return The retrieved course
     * @throws CourseNotFoundException Thrown if course can not be fond
     */
    public Course findByCourseCode(String courseCode) throws CourseNotFoundException {
        Optional<Course> course = courseRepository.findByCourseCode(courseCode);
        if (course.isEmpty()) {
            throw new CourseNotFoundException(String.format(getFormat(), courseCode));
        }
        return course.get();
    }


    /**
     * Retrieves a list of courses whose start date is more than 3 weeks away from current date.
     *
     * @return A list of courses open for application
     */
    public List<Course> availableCourses() {
        Date date = new Date();
        int noOfDays = 21; //i.e three weeks
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, noOfDays);
        Date resDate = calendar.getTime();
        return courseRepository.findByStartDateAfter(resDate);
    }


    /**
     * Creates a course with given course JSON object.
     *
     * @param request The course to be created
     * @return The resulting course
     */
    public Course createCourse(Course request) {
        log.info("Course is created" + request.getCourseCode());
        return courseRepository.saveAndFlush(request);
    }


    /**
     * Adds hired student to course with course code.
     *
     * @param studentNumber  Id of student to be added
     * @param courseCode Course code of course they are added to
     * @throws CourseNotFoundException Thrown if course is not found
     */
    public void addHiredStudents(Long studentNumber, String courseCode)
            throws CourseNotFoundException, TaRatioExceededException  {
        Optional<Course> cr = courseRepository.findById(courseCode);
        if (cr.isEmpty()) {
            throw new CourseNotFoundException(String.format(getFormat(), courseCode));
        }
        Course course = cr.get();
        int size = course.getNumOfStudents();
        int requiredTas = (int) Math.ceil((double) size / 20.0);
        if (requiredTas <= course.getHiredStudents().size()) {
            throw new TaRatioExceededException(String
                    .format(getFormatForTaException(), courseCode));
        } else {
            course.addHiredStudents(studentNumber);
            course.removeCandidateStudent(studentNumber);
        }
        courseRepository.saveAndFlush(course);
    }

    private String getFormat() {
        return "Course with course code %s not found";
    }

    private String getFormatForTaException() {
        return "Course with course id %s has no space for new TA's "
                + "according to the required TA limit";
    }

    /**
     * Adds given student id to the candidate students field under course.
     *
     * @param studentNumber  Student number of student added as candidate
     * @param courseCode Course code of courses added to
     * @throws CourseNotFoundException Thrown if course is not found
     */
    public void addCandidateStudents(Long studentNumber, String courseCode)
            throws CourseNotFoundException {
        Optional<Course> cr = courseRepository.findById(courseCode);
        if (cr.isEmpty()) {
            throw new CourseNotFoundException(String.format(getFormat(), courseCode));
        }
        Course course = cr.get();
        course.addCandidateStudents(studentNumber);
        courseRepository.saveAndFlush(course);
    }


    /**
     * Adds given lecturer id to the lecturers field under course.
     *
     * @param lecturerId Id of lecturer added
     * @param courseCode Course code of course added to
     * @throws CourseNotFoundException Thrown if course is not found
     */
    public void addLecturer(Long lecturerId, String courseCode) throws CourseNotFoundException {
        Optional<Course> cr = courseRepository.findById(courseCode);
        if (cr.isEmpty()) {
            throw new CourseNotFoundException(String.format(getFormat(), courseCode));
        }
        Course course = cr.get();
        course.addLecturer(lecturerId);
        courseRepository.saveAndFlush(course);
    }


    /**
     * Removes lecturer from lecturer list of a course.
     *
     * @param lecturerId id of the lecturer
     * @param courseCode id of the course
     */
    public void removeLecturer(Long lecturerId, String courseCode) throws CourseNotFoundException {
        Optional<Course> cr = courseRepository.findById(courseCode);
        if (cr.isEmpty()) {
            throw new CourseNotFoundException(String.format(getFormat(), courseCode));
        }
        Course course = cr.get();
        course.getLecturers().remove(lecturerId);
        courseRepository.saveAndFlush(course);
    }

    /**
     * Removes student from candidate student list of a course.
     *
     * @param studentNumber  number of the student
     * @param courseCode id of the course
     */
    public void removeCandidateStudents(Long studentNumber, String courseCode)
            throws CourseNotFoundException {
        Optional<Course> cr = courseRepository.findById(courseCode);
        if (cr.isEmpty()) {
            throw new CourseNotFoundException(String.format(getFormat(), courseCode));
        }
        Course course = cr.get();
        course.getCandidateStudents().remove(studentNumber);
        courseRepository.saveAndFlush(course);
    }


    /**
     * Removes student from hired student list of a course.
     *
     * @param studentNumber  id of the student
     * @param courseCode id of the course
     */
    public void removeHiredStudents(Long studentNumber, String courseCode)
            throws CourseNotFoundException {
        Optional<Course> cr = courseRepository.findById(courseCode);
        if (cr.isEmpty()) {
            throw new CourseNotFoundException(String.format(getFormat(), courseCode));
        }
        Course course = cr.get();
        course.getHiredStudents().remove(studentNumber);
        courseRepository.saveAndFlush(course);
    }


}
