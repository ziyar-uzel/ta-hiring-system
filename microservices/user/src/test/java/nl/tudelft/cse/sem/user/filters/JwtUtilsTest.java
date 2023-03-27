package nl.tudelft.cse.sem.user.filters;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import nl.tudelft.cse.sem.user.models.Lecturer;
import nl.tudelft.cse.sem.user.models.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JwtUtilsTest {

    private static String bearer = "Bearer ";
    private static String netid = "netid";

    transient Student koen = new Student();
    transient Set<String> courses = new HashSet<>();
    transient Map<String, Double> grades = new HashMap<>();
    transient Set<String> taCourses = new HashSet<>();

    transient Lecturer annibale = new Lecturer();
    transient Set<String> lecCourses = new HashSet<>();

    transient JwtUtils jwtUtils = new JwtUtils();
    transient JwtInfo jwtInfo = new JwtInfo();

    /**
     * Setup function to initialize a lecturer and student object
     * before each test executes.
     */
    @BeforeEach
    void setup() {
        courses.add("SEM");
        courses.add("AD");
        courses.add("ES");

        taCourses.add("ADS");

        grades.put("CG", 6.0);
        grades.put("ML", 7.0);
        grades.put("DS", null);

        koen.setStudentNumber(1230L);
        koen.setName("Koen");
        koen.setEmail("MyEmail@ForSure.com");
        koen.setNetId("koensnijder");
        koen.setCurrentCourses(courses);
        koen.setGrades(grades);
        koen.setTaCourses(taCourses);

        lecCourses.add("SEM");
        lecCourses.add("BUFF");

        annibale.setNetId("iambuff");
        annibale.setEmail("weJustGotALetter@IWonderWhoIt'sFrom.com");
        annibale.setEmployeeNumber(1L);
        annibale.setCourses(lecCourses);
    }

    @Test
    void createAccessStudentTest() {
        Student maybeImposterKoen = new Student();

        String accessToken = jwtUtils.createAccessToken(koen);
        String token = accessToken.substring(bearer.length());

        SecretKey secretKey = Keys.hmacShaKeyFor(jwtInfo.getSecretKey().getBytes());
        Jws<Claims> jws;
        jws = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
        Claims body = jws.getBody();

        String netidKoen = body.get(netid, String.class);
        maybeImposterKoen.setNetId(netidKoen);

        String email = body.get("email", String.class);
        maybeImposterKoen.setEmail(email);

        Long studentNumber = body.get("studentNumber", Long.class);
        maybeImposterKoen.setStudentNumber(studentNumber);

        Map<String, Double> grades = body.get("grades", Map.class);
        maybeImposterKoen.setGrades(grades);

        List<String> taCoursesList = body
                .get("taCourses", List.class); //returns List because otherwise JPA complains

        Set<String> taCourses = new HashSet<>();
        for (String s : taCoursesList) {
            taCourses.add(s);
        }
        maybeImposterKoen.setTaCourses(taCourses);

        List<String> currentCoursesList = body
                .get("currentCourses", List.class); //returns List because otherwise JPA complains

        Set<String> currentCourses = new HashSet<>();
        for (String s : currentCoursesList) {
            currentCourses.add(s);
        }

        maybeImposterKoen.setCurrentCourses(currentCourses);

        assertEquals(koen.getNetId(), maybeImposterKoen.getNetId());
        assertEquals(koen.getEmail(), maybeImposterKoen.getEmail());
        assertEquals(koen.getStudentNumber(), maybeImposterKoen.getStudentNumber());
        assertEquals(koen.getGrades(), maybeImposterKoen.getGrades());
        assertEquals(koen.getTaCourses(), maybeImposterKoen.getTaCourses());
        assertEquals(koen.getCurrentCourses(), maybeImposterKoen.getCurrentCourses());
    }

    @Test
    void createRefreshStudentTest() {
        String refreshToken = jwtUtils.createRefreshToken(koen);
        String token = refreshToken.substring(bearer.length());

        SecretKey secretKey = Keys.hmacShaKeyFor(jwtInfo.getSecretKey().getBytes());
        Jws<Claims> jws;
        jws = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
        Claims body = jws.getBody();
        String netidK = body.get(netid, String.class);

        assertEquals(koen.getNetId(), netidK);
    }

    @Test
    void createAccessLecturerTest() {
        Lecturer couldBeAnnibale = new Lecturer();

        String accessToken = jwtUtils.createAccessToken(annibale);
        String token = accessToken.substring(bearer.length());

        SecretKey secretKey = Keys.hmacShaKeyFor(jwtInfo.getSecretKey().getBytes());
        Jws<Claims> jws;
        jws = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
        Claims body = jws.getBody();

        String netidAnnibale = body.get(netid, String.class);
        couldBeAnnibale.setNetId(netidAnnibale);

        String email = body.get("email", String.class);
        couldBeAnnibale.setEmail(email);

        Long employeeNumber = body.get("employeeNumber", Long.class);
        couldBeAnnibale.setEmployeeNumber(employeeNumber);

        List<String> coursesList = body
                .get("courses", List.class); //returns List so JPA doesn't complain

        Set<String> courses = new HashSet<>();
        for (String s : coursesList) {
            courses.add(s);
        }
        couldBeAnnibale.setCourses(courses);

        assertEquals(annibale.getNetId(), couldBeAnnibale.getNetId());
        assertEquals(annibale.getEmail(), couldBeAnnibale.getEmail());
        assertEquals(annibale.getCourses(), couldBeAnnibale.getCourses());
        assertEquals(annibale.getEmployeeNumber(), couldBeAnnibale.getEmployeeNumber());
    }

    @Test
    void createRefreshLecturerTest() {
        String refreshToken = jwtUtils.createRefreshToken(annibale);
        String token = refreshToken.substring(bearer.length());

        SecretKey secretKey = Keys.hmacShaKeyFor(jwtInfo.getSecretKey().getBytes());
        Jws<Claims> jws;
        jws = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
        Claims body = jws.getBody();
        String netidAnnibale = body.get(netid, String.class);

        assertEquals(annibale.getNetId(), netidAnnibale);
    }
}
