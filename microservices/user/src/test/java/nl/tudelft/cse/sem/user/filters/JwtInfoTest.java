package nl.tudelft.cse.sem.user.filters;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JwtInfoTest {

    transient JwtInfo info = new JwtInfo();

    @Test
    void secretKeyTest() {
        assertEquals("Quousque tandem abutere, Catilina, patientia nostra? "
                + "quam diu etiam furor iste tuus nos eludet? quem ad finem sese "
                + "effrenata iactabit audacia? - Cicero, In Catilinam", info.getSecretKey());
    }

    @Test
    void bearerTest() {
        assertEquals("Bearer", info.getBearer());
    }
}
