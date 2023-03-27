package nl.tudelft.sem.submission.services;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import nl.tudelft.sem.submission.emailconfig.EmailSending;
import nl.tudelft.sem.submission.entities.Notification;
import nl.tudelft.sem.submission.entities.Status;
import nl.tudelft.sem.submission.entities.Submission;
import nl.tudelft.sem.submission.repositories.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    private final transient NotificationRepository notificationRepository;
    private final transient ContractCreationService contractCreation;


    /**
     * Constructor for the Notification Service class.
     *
     * @param notificationRepository repository for notifications
     * @param contractCreation this service provides functionality to create contract
     */
    @Autowired
    public NotificationService(NotificationRepository notificationRepository,
                               ContractCreationService contractCreation) {
        this.notificationRepository = notificationRepository;
        this.contractCreation = contractCreation;
    }


    /**
     * Sends a notification when an submission result is known.
     *
     * @param submission the id of the submission whose result is being sent
     * @return the created and sent notification
     */
    @SuppressWarnings("PMD.NullAssignment")
    public Notification createResultNotification(Submission submission)
            throws IOException {

        Notification notification = new Notification();
        notification.setNotifiedStudentNumber(submission.getStudentNumber());
        notification.setNotifiedEmail(submission.getStudentEmail());
        notification.setNotifiedCourseId(submission.getCourseCode());
        notification.setStatus(submission.getStatus());

        // Contract sending (new)
        if (submission.getStatus() == Status.ACCEPTED) {
            // EmailSending happens in the contractCreation method
            contractCreation.createContract(submission,
                    EmailSending.getBody(submission.getStatus(), submission.getCourseCode()));
        } else {
            // Status===REJECTED so no attachment for contract necessary
            String subject = "Result of your submission to course " + submission.getCourseCode();

            new EmailSending().sendEmail(new String[] {
                    submission.getStudentEmail(), subject, EmailSending
                    .getBody(submission.getStatus(), submission.getCourseCode())}, null);
        }
        return notificationRepository.save(notification);
    }

    /**
     * Sends a notification when an submission is created in the form of a confirmation email.
     *
     * @return the created and sent notification (confirmation email)
     */
    public Notification createConfirmationNotification(Submission submission)
            throws IOException {

        Notification notification = new Notification();
        notification.setNotifiedStudentNumber(submission.getStudentNumber());
        notification.setNotifiedEmail(submission.getStudentEmail());
        notification.setNotifiedCourseId(submission.getCourseCode());
        notification.setStatus(Status.PENDING);

        String subject = "Confirmation of your submission";
        String body = "Thank you for your submission to "
                + submission.getCourseCode() + ". We will update you "
                + "as soon as your submission status is known";

        new EmailSending().sendEmail(new String[] { submission.getStudentEmail(), subject, body },
                null);

        return notificationRepository.save(notification);
    }
}
