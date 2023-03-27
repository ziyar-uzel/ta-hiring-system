package nl.tudelft.sem.submission.communication;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import nl.tudelft.sem.submission.entities.StudentGrade;
import nl.tudelft.sem.submission.entities.StudentRating;
import nl.tudelft.sem.submission.exceptions.ServiceErrorException;
import nl.tudelft.sem.submission.exceptions.UserServiceErrorException;
import nl.tudelft.sem.submission.utils.Utils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class UserCommunication {

    private static final String USER_URL = "http://localhost:8085";
    private static final HttpClient client = HttpClient.newBuilder().build();
    private static final transient ObjectMapper mapper = new ObjectMapper();


    private String get(String endpoint, String jwtHeader) throws
            IOException, InterruptedException, UserServiceErrorException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(USER_URL + endpoint))
                .header("Content-Type", "application/json")
                .header("Authorization", jwtHeader)
                .GET()
                .build();

        HttpResponse<String> response = client
                .send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != HttpStatus.OK.value()) {
            throw new UserServiceErrorException(String.valueOf(response.body()));
        }

        return response.body();
    }

    private String post(String endpoint, String jwtHeader, Object body) throws
            IOException, InterruptedException, UserServiceErrorException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(USER_URL + endpoint))
                .header("Content-Type", "application/json")
                .header("Authorization", jwtHeader)
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)))
                .build();

        HttpResponse<String> response = client
                .send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != HttpStatus.OK.value()) {
            throw new UserServiceErrorException(String.valueOf(response.body()));
        }

        return response.body(); //gson.fromJson(response.body(), template);
    }

    private String put(String endpoint, Map<String, Object> requestParams,
                       String jwtHeader) throws IOException,
            InterruptedException, UserServiceErrorException, URISyntaxException {

        List<NameValuePair> nvp = new ArrayList<>();
        for (Map.Entry<String, Object> entry : requestParams.entrySet()) {
            nvp.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));
        }
        URI uri = new URIBuilder(USER_URL + endpoint).addParameters(nvp).build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .header("Authorization", jwtHeader)
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != HttpStatus.OK.value()) {
            throw new UserServiceErrorException(String.valueOf(response.body()));
        }

        return response.body();
    }

    /**
     * Gets grade for student for a particular course.
     *
     * @param studentNumber Student number of student
     * @param courseCode      Course code for the course
     * @param jwtHeader     JWT header for authentication
     * @return Grade for that course
     * @throws ServiceErrorException Thrown on server error
     */
    public Float getGradeByCourse(Long studentNumber, String courseCode, String jwtHeader) throws
            ServiceErrorException {
        try {
            return Float.parseFloat(mapper.readTree(get("/users/course?code=" + courseCode
                    + "&studentNumber=" + studentNumber, jwtHeader)).get("grade").asText());
        } catch (IOException | InterruptedException | UserServiceErrorException e) {
            throw new ServiceErrorException(Utils.serviceErrorMessage);
        }
    }

    /**
     * Gets GPA of student.
     *
     * @param studentNumber Student number of student to get GPA for
     * @param jwtHeader     JWT header for authentication
     * @return GPA of student
     * @throws ServiceErrorException Thrown on server error
     */
    public Float getGpaByStudent(Long studentNumber, String jwtHeader) throws
            ServiceErrorException {
        try {
            return Float.parseFloat(mapper.readTree(
                            get("/users/gpa?studentNumber=" + studentNumber, jwtHeader))
                    .get("gpa").asText());
        } catch (IOException | InterruptedException | UserServiceErrorException e) {
            throw new ServiceErrorException(Utils.serviceErrorMessage);
        }
    }

    /**
     * Adds a TA role for a course to student.
     *
     * @param studentId Student who became TA
     * @param courseCode  Course to TA in
     * @param jwtHeader JWT header for authentication
     * @throws ServiceErrorException Thrown on server error
     * @throws URISyntaxException    Thrown on URI error
     */
    public void addTaRole(Long studentId, String courseCode, String jwtHeader) throws
            ServiceErrorException, URISyntaxException {
        try {
            put("/users/addTaRole/" + courseCode,
                    Map.of("studentNumber", studentId), jwtHeader);
        } catch (IOException | InterruptedException | UserServiceErrorException e) {
            throw new ServiceErrorException(Utils.serviceErrorMessage);
        }
    }

    /**
     * Gets rating for a student as TA.
     *
     * @param studentNumber Student whose rating is to be obtained
     * @param jwtHeader     JWT header for authentication
     * @return Rating of student
     * @throws ServiceErrorException Thrown on server error
     */
    public Float getRating(Long studentNumber, String jwtHeader) throws ServiceErrorException {
        try {
            return Float.parseFloat(mapper.readTree(get("/users/rating?studentNumber="
                            + studentNumber, jwtHeader))
                    .get("rating").asText());
        } catch (IOException | InterruptedException | UserServiceErrorException e) {
            throw new ServiceErrorException(Utils.serviceErrorMessage);
        }
    }

    /**
     * Gets the ratings of multiple students from the user microservice.
     *
     * @param studentNumbers the student numbers of the wanted students
     * @return a list of student ratings
     * @throws ServiceErrorException if the user service is unavailable
     */
    public List<StudentRating> getRatings(List<Long> studentNumbers, String jwtHeader)
            throws ServiceErrorException {
        try {
            return mapper.readValue(post("/users/ratings", jwtHeader, studentNumbers),
                    new TypeReference<List<StudentRating>>() {});
        } catch (IOException | InterruptedException | UserServiceErrorException e) {
            throw new ServiceErrorException(Utils.serviceErrorMessage);
        }
    }

    /**
     * Gets the grades of multiple users for a course from the user microservice.
     *
     * @param studentNumbers the student numbers
     * @param courseCode the code of the targeted course
     * @return a list of student grades
     * @throws ServiceErrorException if the user service is unavailable
     */
    public List<StudentGrade> getGradesByCourse(List<Long> studentNumbers,
                                                String courseCode, String jwtHeader)
            throws ServiceErrorException {
        try {
            return mapper.readValue(post("/users/courseGrades?courseCode=" + courseCode,
                    jwtHeader, studentNumbers), new TypeReference<List<StudentGrade>>() {});
        } catch (IOException | InterruptedException | UserServiceErrorException e) {
            throw new ServiceErrorException(Utils.serviceErrorMessage);
        }
    }
}
