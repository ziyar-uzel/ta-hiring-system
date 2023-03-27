package nl.tudelft.sem.submission.filters;

public class JwtInfo {

    private String secretKey = "Quousque tandem abutere, Catilina, patientia nostra? "
            + "quam diu etiam furor iste tuus nos eludet? quem ad finem sese "
            + "effrenata iactabit audacia? - Cicero, In Catilinam";
    private String bearer = "Bearer";

    public JwtInfo() {
    }


    public String getSecretKey() {
        return secretKey;
    }

    public String getBearer() {
        return bearer;
    }

    private void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    private void setBearer(String bearer) {
        this.bearer = bearer;
    }
}
