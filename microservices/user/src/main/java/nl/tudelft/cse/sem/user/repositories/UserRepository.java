package nl.tudelft.cse.sem.user.repositories;

import java.util.List;
import java.util.Optional;
import nl.tudelft.cse.sem.user.models.Student;
import nl.tudelft.cse.sem.user.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    /**
     * Finds user by netid.
     *
     * @param netid of user
     * @return Optional of user
     */
    @Override
    Optional<User> findById(String netid);

    @Query("select s from Student s where s.studentNumber=?1")
    Optional<Student> findStudentByStudentNumber(Long studentNumber);

    @Query("SELECT s FROM Student s WHERE s.studentNumber IN (:studentNumbers)")
    // 2. Spring JPA In cause using @Query
    List<Student> findStudentsByStudentNumbers(@Param("studentNumbers") List<Long> studentNumbers);

    @Query("SELECT s FROM Student s WHERE (:courseCode) MEMBER OF s.taCourses")
    List<Student> findTasForCourse(@Param("courseCode") String courseCode);
}
