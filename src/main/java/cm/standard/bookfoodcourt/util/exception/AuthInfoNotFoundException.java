package cm.standard.bookfoodcourt.util.exception;

public class AuthInfoNotFoundException extends Exception{
    public AuthInfoNotFoundException(String message) {
        super(message);
    }

    public AuthInfoNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
