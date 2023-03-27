package nl.tudelft.cse.sem.user.configuartions;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class PasswordConfiguration {

    /**
     * Sets encoder strength to 11.
     *
     * @return PasswordEncoder instance to store in database encoded passwords.
     */
    @Bean
    public BCryptPasswordEncoder bcryptPasswordEncoder() {
        return new BCryptPasswordEncoder(11);
    }

}
