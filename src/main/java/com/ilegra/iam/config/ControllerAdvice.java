package com.ilegra.iam.config;

import com.ilegra.iam.exception.InvalidCredentialsException;
import com.ilegra.iam.exception.InvalidRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestControllerAdvice
class ControllerAdvice {

    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(value = UNAUTHORIZED, reason = "Invalid credentials")
    void handleInvalidCredentialsException() {
        // No content
    }

    @ExceptionHandler(InvalidRequestException.class)
    @ResponseStatus(FOUND)
    ResponseEntity<Void> handleInvalidRequestException(InvalidRequestException exc) {
        return ResponseEntity.status(FOUND)
            .header("Location", createLocationUrl(exc))
            .build();
    }

    private static String createLocationUrl(InvalidRequestException exc) {
        StringBuilder url = new StringBuilder();
        if (exc.getRedirectUri() != null) {
            url.append(exc.getRedirectUri());
        }
        url.append("?error=invalid_request");
        url.append("&error_description=").append(exc.getMessage());
        return url.toString();
    }

}
