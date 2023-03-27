package nl.tudelft.cse.sem.user.filters;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import nl.tudelft.cse.sem.user.models.Lecturer;
import nl.tudelft.cse.sem.user.models.Student;
import nl.tudelft.cse.sem.user.models.User;
import org.springframework.security.core.GrantedAuthority;

public class JwtUtils {

    private transient JwtInfo jwtInfo = new JwtInfo();

    public JwtUtils() {
    }

    /**
     * Create an access JWT token for the user and return it.
     *
     * @param user - The user to create the token for
     * @return - The generated  JWT token
     */
    public String createAccessToken(User user) {
        if (user instanceof Lecturer) {
            String jws = Jwts.builder()
                    .claim("netid", user.getNetId())
                    .claim("email", user.getEmail())
                    .claim("employeeNumber", ((Lecturer) user)
                            .getEmployeeNumber())
                    .claim("courses", new ArrayList<String>(((Lecturer) user)
                            .getCourses()))
                    .claim("roles", user
                            .getAuthorities()
                            .stream()
                            .map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                    .setExpiration(new Date(System
                            .currentTimeMillis() + 10 * 60 * 1000))
                    .signWith(Keys.hmacShaKeyFor(jwtInfo.getSecretKey().getBytes()))
                    .compact();
            return "Bearer " + jws;
        } else if (user instanceof Student) {
            String jws = Jwts.builder()
                    .claim("netid", user.getNetId())
                    .claim("email", user.getEmail())
                    .claim("studentNumber", ((Student) user)
                            .getStudentNumber())
                    .claim("grades", ((Student) user)
                            .getGrades())
                    .claim("taCourses", new ArrayList<String>(((Student) user)
                            .getTaCourses()))
                    .claim("currentCourses", new ArrayList<String>(((Student) user)
                            .getCurrentCourses()))
                    .claim("roles", user
                            .getAuthorities()
                            .stream()
                            .map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                    .setExpiration(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                    .signWith(Keys.hmacShaKeyFor(jwtInfo.getSecretKey().getBytes()))
                    .compact();

            return "Bearer " + jws;
        } else {
            throw new RuntimeException("Something went wrong.");
            // exception
        }
    }

    /**
     * Create a refresh JWT token for the user and return it.
     *
     * @param user - The user to create a refresh token for
     * @return - The generated JWT refresh token
     */
    public String createRefreshToken(User user) {
        SecretKey secretKey = Keys.hmacShaKeyFor(jwtInfo.getSecretKey().getBytes());

        String jws = Jwts.builder()
                .claim("netid", user.getNetId())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 1000))
                .signWith(secretKey)
                .compact();
        return "Bearer " + jws;
    }
}
