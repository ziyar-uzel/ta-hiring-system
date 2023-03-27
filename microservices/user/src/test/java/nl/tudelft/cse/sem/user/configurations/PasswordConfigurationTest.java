package nl.tudelft.cse.sem.user.configurations;

import nl.tudelft.cse.sem.user.configuartions.PasswordConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PasswordConfigurationTest {

    @Test
    void bcryptPasswordEncoderTest() {
        PasswordConfiguration passwordConfiguration = new PasswordConfiguration();
        assertNotNull(passwordConfiguration.bcryptPasswordEncoder());
    }
}
