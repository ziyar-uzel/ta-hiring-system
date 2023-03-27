package nl.tudelft.sem.submission.emailconfig;


/**
 * Currently is used for testing purposes.
 */
public class ProductionMode {
    private static final boolean production = false;

    public static boolean getProductionMode() {
        return production;
    }
}
