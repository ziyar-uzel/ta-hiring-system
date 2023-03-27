package nl.tudelft.cse.sem.user.filters;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import nl.tudelft.cse.sem.user.exceptions.AttemptAuthenticationException;
import nl.tudelft.cse.sem.user.models.User;
import nl.tudelft.cse.sem.user.models.UserRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JwtPublisher extends UsernamePasswordAuthenticationFilter {

    private final transient AuthenticationManager authenticationManager;

    /**
     * Constructor.
     *
     * @param authenticationManager authenticates user
     */
    public JwtPublisher(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    /**
     * When user attempts to authenticate, his username and password have to be validated.
     *
     * @param request every incoming request
     * @param response is produced by system
     * @return authentication or block user
     * @throws AuthenticationException can be thrown
     */
    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) {
        try {
            String n = new String(request
                    .getInputStream()
                    .readAllBytes());
            UserRequest authenticationClientRequest =
                    new ObjectMapper()
                            .readValue(n, UserRequest.class);

            Authentication authenticate = new UsernamePasswordAuthenticationToken(
                    authenticationClientRequest.getNetid(),
                    authenticationClientRequest.getPassword()
            );

            return authenticationManager.authenticate(authenticate);

        } catch (Exception e) {
            throw new AttemptAuthenticationException(e.getMessage());
        }
    }

    /**
     * If user successfully authenticated -> he will get jwt tokens.
     *
     * @param request every incoming request
     * @param response is produced by system
     * @param chain chain of filters
     * @param authResult result of authentication
     * @throws IOException can be thrown
     * @throws ServletException can be thrown
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) {
        User user = (User) authResult.getPrincipal();
        String accessToken = new JwtUtils().createAccessToken(user);
        String refreshToken = new JwtUtils().createRefreshToken(user);

        response.setHeader(HttpHeaders.AUTHORIZATION, accessToken);
        response.setHeader("RefreshToken", refreshToken);
    }

    @Override
    protected void unsuccessfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException failed)
            throws IOException {

        logger.debug("Failed authentication! Incorrect details.");
        //Add more descriptive message
        response.setStatus(FORBIDDEN.value());
        Map<String, String> error = new HashMap<>();
        error.put("error-message", failed.getMessage());
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), error);
    }
}
