package fr.iut.pathpilotapi.exceptions;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public static final String DESCRIPTION = "description";
    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ProblemDetail handleBadCredentials(BadCredentialsException exception) {
        LOG.error("Bad credentials: {}", exception.getMessage());
        return createProblemDetail(HttpStatus.UNAUTHORIZED, exception.getMessage(), "The username or password is incorrect");
    }

    @ExceptionHandler({
            AccountStatusException.class,
            AccessDeniedException.class,
            SignatureException.class,
            ExpiredJwtException.class
    })
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ProblemDetail handleForbiddenExceptions(Exception exception) {
        LOG.error("Forbidden: {}", exception.getMessage());
        String description = switch (exception) {
            case AccountStatusException e -> "The account is locked";
            case AccessDeniedException e -> "You are not authorized to access this resource";
            case SignatureException e -> "The JWT signature is invalid";
            case ExpiredJwtException e -> "The JWT token has expired";
            default -> "Forbidden access";
        };
        return createProblemDetail(HttpStatus.FORBIDDEN, exception.getMessage(), description);
    }

    @ExceptionHandler(EmailAlreadyTakenException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ProblemDetail handleEmailAlreadyTaken(EmailAlreadyTakenException exception) {
        LOG.error("Conflict: {}", exception.getMessage());
        return createProblemDetail(HttpStatus.CONFLICT, exception.getMessage(), "The email is already taken");
    }

    @ExceptionHandler({
            UserNotFoundException.class,
            ObjectNotFoundException.class,
            NoResourceFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleUserNotFound(Exception exception) {
        LOG.error("Not Found: {}", exception.getMessage());
        String description = switch (exception) {
            case UserNotFoundException e -> "The user was not found";
            case ObjectNotFoundException e -> "The object was not found";
            case NoResourceFoundException e -> "The resource was not found with this path";
            default -> "Not found";
        };
        return createProblemDetail(HttpStatus.NOT_FOUND, exception.getMessage(), description);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        LOG.error("Validation failed: {}", exception.getMessage());
        String description = exception.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> String.format("Field '%s': %s", fieldError.getField(), fieldError.getDefaultMessage()))
                .collect(Collectors.joining(", "));
        return createProblemDetail(HttpStatus.BAD_REQUEST, "Validation failed", description);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ProblemDetail handleGenericException(Exception exception) {
        LOG.error("Internal Server Error: {}", exception.getMessage());
        LOG.error("Stack trace: ", exception);
        return createProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage(), "Unknown internal server error.");
    }

    /**
     * Create a ProblemDetail object with the status, message and description.
     *
     * @param status      the HTTP status
     * @param message     the message
     * @param description the description
     * @return the ProblemDetail object
     */
    private ProblemDetail createProblemDetail(HttpStatus status, String message, String description) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(status, message);
        errorDetail.setProperty(DESCRIPTION, description);
        return errorDetail;
    }
}
