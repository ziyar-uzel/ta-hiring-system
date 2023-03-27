package nl.tudelft.sem.course.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface CourseRepository extends JpaRepository<Course, String> {

    Optional<Course> findByCourseCode(String courseCode);

    @Query("select c from Course c where c.startDate >= :threeWeeksBefore")
    List<Course> findByStartDateAfter(
            @Param("threeWeeksBefore") Date threeWeeksBefore);

}