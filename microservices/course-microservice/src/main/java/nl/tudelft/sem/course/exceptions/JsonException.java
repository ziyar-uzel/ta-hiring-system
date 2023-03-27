package nl.tudelft.sem.course.exceptions;

public abstract class JsonException extends RuntimeException {

    private static final long serialVersionUID = 8363881563452629731L;

    public JsonException(String message) {
        super(message);
    }

    public String toJson() {
        return "{ \"message\": \"" + super.getMessage() + "\" }";
    }

}