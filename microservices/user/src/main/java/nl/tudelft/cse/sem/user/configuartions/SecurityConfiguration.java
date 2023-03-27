package nl.tudelft.cse.sem.user.configuartions;

import nl.tudelft.cse.sem.user.filters.JwtFilter;
import nl.tudelft.cse.sem.user.filters.JwtPublisher;
import nl.tudelft.cse.sem.user.services.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableWebSecurity
@org.springframework.context.annotation.Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final transient UserService userService;
    private final transient BCryptPasswordEncoder bcryptPasswordEncoder;

    /**
     * Constructor.
     *
     * @param userService that will be used by
     *                    authentication manager to know where to look for users
     * @param bcryptPasswordEncoder to encode passwords
     */
    public SecurityConfiguration(UserService userService,
                                 BCryptPasswordEncoder bcryptPasswordEncoder) {
        this.userService = userService;
        this.bcryptPasswordEncoder = bcryptPasswordEncoder;
    }

    /**
     * Configures security -> adds jwt filters and unblock for free access db -> debug purposes.
     *
     * @param http allows for security configuration
     * @throws Exception from http security
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        JwtPublisher publisher = new JwtPublisher(authenticationManagerBean());
        publisher.setFilterProcessesUrl("/users/login");

        http
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(publisher)
                .addFilterAfter(new JwtFilter(), JwtPublisher.class)
                .authorizeRequests()
                .antMatchers("/users/login/**", "/users/token/refresh").permitAll()
                .antMatchers("/h2-console/**").permitAll()
                .anyRequest().authenticated();

        http.csrf().disable();
        http.headers().frameOptions().disable();
    }

    /**
     * Tell authentication manager builder to use
     * userservice to find users and encode passwords with our encoder.
     *
     * @param auth Authentication Manager Builder
     *             used to build Authentication Manager
     * @throws Exception can be thrown
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(bcryptPasswordEncoder);
    }

    /**
     * Creates authentication manager which will be used by security (and by jwt filters).
     *
     * @return authentication manager
     * @throws Exception  can be thrown
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
