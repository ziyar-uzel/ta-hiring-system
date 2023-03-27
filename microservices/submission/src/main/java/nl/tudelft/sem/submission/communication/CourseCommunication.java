package nl.tudelft.sem.submission.communication;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import nl.tudelft.sem.submission.exceptions.CourseServiceErrorException;
import nl.tudelft.sem.submission.exceptions.ServiceErrorException;
import nl.tudelft.sem.submission.utils.Utils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class CourseCommunication {

    private static final transient ObjectMapper mapper = new ObjectMapper();


    private String get(String endpoint, String jwtHeader) throws
            IOException, InterruptedException, CourseServiceErrorException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8088" + endpoint))
                .header("Content-Type", "application/json")
                .header("Authorization", jwtHeader)
                .GET()
                .build();

        HttpClient client = HttpClient.newBuilder().build();
        HttpResponse<String> response = client
                .send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != HttpStatus.OK.value()) {
            throw new CourseServiceErrorException(response.body());
        }
        return response.body();
    }


    private String put(String endpoint, Map<String, Object> requestParams, String jwtHeader)
            throws IOException, InterruptedException,
            CourseServiceErrorException, URISyntaxException {

        List<NameValuePair> nvp = new ArrayList<>();
        for (Map.Entry<String, Object> entry : requestParams.entrySet()) {
            nvp.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));
        }
        URI uri = new URIBuilder("http://localhost:8088" + endpoint).addParameters(nvp).build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .header("Authorization", jwtHeader)
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpClient client = HttpClient.newBuilder().build();
        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != HttpStatus.OK.value()) {
            throw new CourseServiceErrorException(response.body());
        }
        return response.body();
    }

    private String delete(String endpoint, Map<String, Object> requestParams, String jwtHeader)
            throws IOException, InterruptedException,
            CourseServiceErrorException, URISyntaxException {

        List<NameValuePair> nvp = new ArrayList<>();
        for (Map.Entry<String, Object> entry : requestParams.entrySet()) {
            nvp.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));
        }
        URI uri = new URIBuilder("http://localhost:8088" + endpoint).addParameters(nvp).build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .header("Authorization", jwtHeader)
                .DELETE()
                .build();

        HttpClient client = HttpClient.newBuilder().build();
        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != HttpStatus.OK.value()) {
            throw new CourseServiceErrorException(response.body());
        }
        return response.body();
    }

    /**
     * Gets the date of course.
     *
     * @param courseCode the id of the course
     * @return the date
     */
    public Date getCourseStartDate(String courseCode, String jwtHeader)
            throws ServiceErrorException {
        try {
            String startDate = mapper.readTree(
                    get("/courses/startDate/" + courseCode, jwtHeader)).get("startDate").asText();
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
            return gson.fromJson(startDate.split("T")[0], Date.class);
        } catch (IOException | InterruptedException | CourseServiceErrorException e) {
            throw new ServiceErrorException(errorMessage(e));
        }
    }

    /**
     * GET request to get course description.
     *
     * @param courseCode  Course code of course
     * @param jwtHeader JWT header for authentication
     * @return The course description with courseCode
     * @throws ServiceErrorException Thrown if there were any server errors.
     */
    public String getCourseDescription(String courseCode, String jwtHeader)
            throws ServiceErrorException {
        try {
            return mapper.readTree(
                    get("/courses/description/" + courseCode, jwtHeader))
                    .get("description").asText();
        } catch (IOException | InterruptedException | CourseServiceErrorException e) {
            throw new ServiceErrorException(errorMessage(e));
        }
    }

    /**
     * Gets the lecturers for a course.
     *
     * @param courseCode the id of the course
     * @return a set of lecturer
     */
    public Set<Long> getLecturersForCourse(String courseCode, String jwtHeader)
            throws ServiceErrorException {
        try {
            return mapper.convertValue(mapper.readTree(
                            get("/courses/lecturers/" + courseCode, jwtHeader))
                    .get("lecturers"), new TypeReference<Set<Long>>() {});
        } catch (IOException | InterruptedException | CourseServiceErrorException e) {
            throw new ServiceErrorException(errorMessage(e));
        }
    }

    /**
     * Adds a student as TA to course.
     *
     * @param studentId Student ID of TA
     * @param courseCode  Course added to
     * @param jwtHeader JWT header for authentication
     * @throws ServiceErrorException Thrown on server error
     * @throws URISyntaxException Thrown on URI error
     */
    public void addHiredStudent(Long studentId, String courseCode, String jwtHeader) throws
            ServiceErrorException, URISyntaxException {
        try {
            put("/courses/add/hiredStudent/" + courseCode,
                    Map.of("studentNumber", studentId), jwtHeader);
        } catch (IOException | InterruptedException | CourseServiceErrorException e) {
            throw new ServiceErrorException(errorMessage(e));
        }
    }

    /**
     * Adds a student as a candidate for a course.
     *
     * @param studentId Student ID of TA
     * @param courseId  Course added to
     * @param jwtHeader JWT header for authentication
     * @throws ServiceErrorException Thrown on server error
     * @throws URISyntaxException Thrown on URI error
     */
    public void addCandidateStudent(Long studentId, String courseId, String jwtHeader) throws
            ServiceErrorException, URISyntaxException {
        try {
            put("/courses/add/candidateStudent/" + courseId,
                    Map.of("studentNumber", studentId), jwtHeader);
        } catch (IOException | InterruptedException | CourseServiceErrorException e) {
            throw new ServiceErrorException(errorMessage(e));
        }
    }

    /**
     * Removes candidate student from list of candidate students in course microservice.
     *
     * @param studentNumber of student to be removes
     * @param jwtHeader to successfully authenticate
     * @param courseCode to indicate at which course student applied
     * @throws ServiceErrorException is thrown
     */
    public void removeCandidateStudent(Long studentNumber, String jwtHeader, String courseCode)
            throws ServiceErrorException {
        try {
            delete("/courses/remove/candidateStudent/" + courseCode,
                    Map.of("studentNumber", studentNumber), jwtHeader);
        } catch (IOException | InterruptedException
                | CourseServiceErrorException | URISyntaxException e) {
            throw new ServiceErrorException(errorMessage(e));
        }
    }

    private String errorMessage(Exception e) {
        return e.getMessage() == null ? Utils.serviceErrorMessage : e.getMessage();
    }
}
