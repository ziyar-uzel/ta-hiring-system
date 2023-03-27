package nl.tudelft.sem.submission.emailconfig;

import com.sendgrid.Attachments;
import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import java.io.IOException;
import java.io.InputStream;
import lombok.extern.slf4j.Slf4j;
import nl.tudelft.sem.submission.entities.Status;

@Slf4j
public class EmailSending {


    /**
     * Sends an email consisting of a title subject and content body.
     *
     * @param data    an array string with at index 0 the email address, at index 1 the title
     *                and at index 2 the body of the email
     * @param pstream the inputStream used.
     * @return the created and sent notification
     */
    @SuppressWarnings({"PMD.DataflowAnomalyAnalysis", "checkstyle:LeftCurly"})
    public String sendEmail(String[] data, InputStream pstream)
            throws IOException {
        Email from = new Email("a.mereuta@student.tudelft.nl");
        Email to = new Email(data[0]);
        Content body = new Content("text/plain", data[2]);
        Mail mail = new Mail(from, data[1], to, body);
        Request request = new Request();
        if (!(pstream == null)) {
            mail.addAttachments(new Attachments.Builder("contract.pdf", pstream)
                    .withType("application/pdf").build());
        }
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            if (ProductionMode.getProductionMode()) {
                SendGrid sg = new SendGrid(ApiKey.getKey());
                Response response = sg.api(request);
            }
            return "success";
        } catch (IOException ex) {
            return "failed" + ex.toString();
        }
    }

    /**
     * Creates body of email.
     *
     * @param status   Status of application
     * @param courseId Course applied to
     * @return The email body
     */
    public static String getBody(Status status, String courseId) {
        if (status == Status.ACCEPTED) {
            return "Congratulations! Your application to course " + courseId
                    + " has been accepted!";
        } else if (status == Status.REJECTED) {
            return "Sorry! Your application to course " + courseId
                    + " has been rejected!";
        } else {
            return "Sorry. We can't seem to retrieve "
                    + "your application result. Please contact support.";
        }
    }

}
