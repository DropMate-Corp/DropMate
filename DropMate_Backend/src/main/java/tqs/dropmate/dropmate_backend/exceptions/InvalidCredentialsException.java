package tqs.dropmate.dropmate_backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a user tries to login with the wrong credentials.
 */
@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class InvalidCredentialsException extends Exception {
    private static final long serialVersionUID = 1L;

    public InvalidCredentialsException(String message){
        super(message);
    }
}
