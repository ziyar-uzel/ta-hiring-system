package nl.tudelft.sem.course.filters;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtFilter extends OncePerRequestFilter {

    private transient JwtInfo jwtInfo = new JwtInfo();

    /**
     * Checks if jwt is correct. If yes, than allows access, otherwise blocks.
     *
     * @param request every incoming request
     * @param response is produced by system
     * @param filterChain chain of filters
     * @throws ServletException could be thrown
     * @throws IOException could be thrown
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (request.getServletPath().equals("/api/login")
                || request.getServletPath().equals("/api/token/refresh")) {
            filterChain.doFilter(request, response);
            return;
        } else {
            String authHeader = request.getHeader(AUTHORIZATION);

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                try {
                    String token = authHeader.substring("Bearer ".length());

                    SecretKey secretKey = Keys.hmacShaKeyFor(jwtInfo.getSecretKey().getBytes());
                    Jws<Claims> jws;
                    jws = Jwts.parserBuilder()
                            .setSigningKey(secretKey)
                            .build()
                            .parseClaimsJws(token);
                    Claims body = jws.getBody();

                    String netid = body.get("netid", String.class);
                    List<String> roles = (ArrayList<String>) body.get("roles");
                    Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    roles.forEach(role -> {
                        authorities.add(new SimpleGrantedAuthority(role));
                    });
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(netid, null, authorities);

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    filterChain.doFilter(request, response);
                } catch (JwtException ex) {
                    response.setStatus(FORBIDDEN.value());
                    Map<String, String> error = new HashMap<>();
                    error.put("error-message", ex.getMessage());
                    response.setContentType(APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(response.getOutputStream(), error);
                }
            } else {
                filterChain.doFilter(request, response);
            }
        }
    }
}

