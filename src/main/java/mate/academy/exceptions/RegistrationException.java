package mate.academy.exceptions;

public class RegistrationException extends Exception {
    public RegistrationException(String message, Throwable cause) {
        super(message, cause);
    }

    public RegistrationException(String message) {
        super(message);
    }
}
