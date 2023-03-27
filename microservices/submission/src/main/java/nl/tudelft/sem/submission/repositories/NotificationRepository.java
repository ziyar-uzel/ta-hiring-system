package nl.tudelft.sem.submission.repositories;

import java.util.ArrayList;
import java.util.Optional;
import nl.tudelft.sem.submission.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Optional<Notification> findById(Long notificationId);

    ArrayList<Notification> findAllByNotifiedStudentNumber(Long notifiedStudentNumber);

    ArrayList<Notification> findAllByNotifiedCourseId(String courseId);

    boolean existsById(Long notificationId);
}
