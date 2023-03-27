package nl.tudelft.sem.submission.exceptions;

public abstract class JsonException extends Exception {

    private static final long serialVersionUID = 8363881563452629731L;

    public JsonException(String message) {
        super(message);
    }

    public String toJson() {
        return "{ \"message\": \"" + super.getMessage() + "\" }";
    }

}
