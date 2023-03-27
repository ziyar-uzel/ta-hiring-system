package nl.tudelft.sem.submission.emailconfig;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ProductionModeTest {

    @Test
    void getProductionModeTest() {
        assertFalse(ProductionMode.getProductionMode());
    }
}
