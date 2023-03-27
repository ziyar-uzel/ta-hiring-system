package nl.tudelft.sem.submission.filters;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JwtInfoTest {

    private static final JwtInfo jwtInfo = new JwtInfo();

    @Test
    void secretKeyTest() {
        assertEquals("Quousque tandem abutere, Catilina, patientia nostra? "
                + "quam diu etiam furor iste tuus nos eludet? quem ad finem sese "
                + "effrenata iactabit audacia? - Cicero, In Catilinam", jwtInfo.getSecretKey());
    }

    @Test
    void bearerTest() {
        assertEquals("Bearer", jwtInfo.getBearer());
    }
}
