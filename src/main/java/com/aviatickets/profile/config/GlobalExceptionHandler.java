package com.aviatickets.profile.config;

import com.aviatickets.profile.controller.response.ErrorDto;
import com.aviatickets.profile.exception.UnauthorizedException;
import com.aviatickets.profile.exception.UsernameAlreadyExistsException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.NoSuchElementException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    public static final String EXCEPTION_MESSAGE = "Uncaught exception";

    @ExceptionHandler(value = Exception.class)
    protected ResponseEntity<?> handleException(Exception e) {
        log.error(EXCEPTION_MESSAGE, e);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = NoSuchElementException.class)
    protected ResponseEntity<?> handleNoSuchElementException(NoSuchElementException e) {
        log.debug(EXCEPTION_MESSAGE, e);
        return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {
            MalformedJwtException.class, SignatureException.class, ExpiredJwtException.class
    })
    protected ResponseEntity<?> handleJwtException(JwtException e) {
        log.debug(EXCEPTION_MESSAGE, e);
        return buildErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ValidationException.class)
    protected ResponseEntity<?> handleValidationException(ValidationException e) {
        log.debug(EXCEPTION_MESSAGE, e);
        return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedException.class)
    protected ResponseEntity<?> handleUnauthorizedException(UnauthorizedException e) {
        log.error(EXCEPTION_MESSAGE, e);
        return buildErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    protected ResponseEntity<?> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException e) {
        log.error(EXCEPTION_MESSAGE, e);
        return buildErrorResponse(e.getMessage(), HttpStatus.CONFLICT);
    }

    private ResponseEntity<?> buildErrorResponse(String message, HttpStatus status) {
        return new ResponseEntity<>(
                new ErrorDto(message, status.getReasonPhrase(), status.value()),
                status
        );
    }
}
