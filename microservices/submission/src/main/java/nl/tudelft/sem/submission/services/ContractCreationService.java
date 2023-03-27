package nl.tudelft.sem.submission.services;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import lombok.extern.slf4j.Slf4j;
import nl.tudelft.sem.submission.emailconfig.EmailSending;
import nl.tudelft.sem.submission.emailconfig.ProductionMode;
import nl.tudelft.sem.submission.entities.Contract;
import nl.tudelft.sem.submission.entities.Submission;
import nl.tudelft.sem.submission.repositories.ContractRepository;
import nl.tudelft.sem.submission.utils.Utils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ContractCreationService {

    private final transient ContractRepository contractRepository;
    private final transient AmazonS3 s3Client;

    /**
     * Constructor for Contract service.
     *
     * @param contractRepository Contract repository
     */
    public ContractCreationService(ContractRepository contractRepository) {
        this.contractRepository = contractRepository;

        BasicAWSCredentials credentials = new BasicAWSCredentials(Utils.accessKey, Utils.secretKey);
        AWSStaticCredentialsProvider provider = new AWSStaticCredentialsProvider(credentials);
        this.s3Client = AmazonS3ClientBuilder.standard()
                .withRegion("eu-central-1")
                .withCredentials(provider)
                .build();
    }

    /**
     * Generates a PDF contract for the createResultNotification method if
     * applicationStatus == ACCEPTED. This contract is user-specific and contains
     * user details like the user's student number and the course for the contract.
     *
     * @param submission - The submission that will get a contract
     * @return the PATH (e.g. '/pdf/10101.pdf/') of the newly created contract
     * @throws IOException the exception that is thrown if something goes wrong
     **/
    @SuppressWarnings("PMD")
    public Boolean createContract(Submission submission, String body) throws IOException {

        PDDocument contract = new PDDocument();
        PDPage page = new PDPage();
        contract.addPage(page);

        PDPageContentStream stream = new PDPageContentStream(contract, page);
        stream.setFont(PDType1Font.HELVETICA_BOLD, 30);
        stream.setLeading(14.5);
        stream.beginText();
        stream.newLineAtOffset(25, 700);
        stream.showText("TA Contract");
        stream.setFont(PDType1Font.HELVETICA, 16);
        stream.newLine();
        lineFormatter(stream);
        stream.showText("Hereby is student " + submission.getStudentNumber()
                + " declared as TA for course " + submission.getCourseCode() + ".");
        lineFormatter(stream);
        stream.showText("Student " + submission.getStudentNumber() + " accepts the workload of "
                + submission.getAmountOfHours() + " hours ");
        stream.newLine();
        stream.showText("as their weekly investment into the course.");

        lineFormatter(stream);
        stream.showText("And accepts this description as their task:");
        stream.newLine();
        stream.setFont(PDType1Font.HELVETICA_OBLIQUE, 16);
        stream.showText(submission.getDescription() + ".");
        lineFormatter(stream);
        stream.setFont(PDType1Font.HELVETICA_BOLD, 16);
        stream.showText("Please sign the contract here:");
        stream.newLineAtOffset(0, -500);
        stream.setFont(PDType1Font.HELVETICA, 2);
        stream.showText("The student also declares hereby that their "
                + "soul is property of TUDelft for the rest of their lifetimes");
        stream.endText();
        stream.close();

        return saveContract(submission, body, contract);
    }

    @SuppressWarnings("PMD")
    private boolean saveContract(Submission submission,
                                 String body, PDDocument contract)
            throws IOException {
        PipedOutputStream pipedOutputStream = new PipedOutputStream();
        PipedInputStream pipedInputStream = new PipedInputStream(pipedOutputStream);

        Contract contract1 = new Contract(submission.getStudentNumber(),
                submission.getCourseCode());
        Runnable runnable = () -> {
            TransferManager transferManager = TransferManagerBuilder.standard()
                    .withS3Client(this.s3Client)
                    .build();

            try {
                byte[] bytes = pipedInputStream.readAllBytes();

                if (ProductionMode.getProductionMode()) {
                    transferManager.upload(Utils.bucketName, contract1.getBucketKey(),
                            new ByteArrayInputStream(bytes), new ObjectMetadata());
                    new EmailSending().sendEmail(new String[]{submission.getStudentEmail(),
                        "Result of your application to "
                                + submission.getCourseCode(), body},
                            new ByteArrayInputStream(bytes));
                }

                contract.close();
            } catch (IOException e) {
                log.error("Smth went wrong");
            }

        };

        Thread thread = new Thread(runnable);
        thread.start();
        contract.save(pipedOutputStream);
        contractRepository.save(contract1);

        return true;
    }

    @SuppressWarnings("PMD")
    public void lineFormatter(PDPageContentStream stream) throws IOException {
        stream.newLine();
        stream.newLine();
    }
}


