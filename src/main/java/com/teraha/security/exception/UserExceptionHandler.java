package com.teraha.security.exception;

import java.time.OffsetDateTime;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.teraha.commons.dtos.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class UserExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(
            UserNotFoundException ex) {

        return respond(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRoleNotFound(
            RoleNotFoundException ex) {

        return respond(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(InvalidRoleAssignmentException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRoleAssignment(
            InvalidRoleAssignmentException ex) {

        return respond(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(
            UserAlreadyExistsException ex) {

        return respond(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException ex) {

        log.warn("Database constraint violation during user operation");

        return respond(
            HttpStatus.CONFLICT,
            "The operation conflicts with existing data");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationFailure(
            MethodArgumentNotValidException ex) {

        return respond(
            HttpStatus.BAD_REQUEST,
            "Request validation failed");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableRequest(
            HttpMessageNotReadableException ex) {

        return respond(
            HttpStatus.BAD_REQUEST,
            "Invalid request body");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleInvalidArgument(
            IllegalArgumentException ex) {

        return respond(
            HttpStatus.BAD_REQUEST,
            "Invalid request parameters");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(
            Exception ex) {

        log.error("Unexpected error during user operation", ex);

        return respond(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected server error occurred");
    }

    private ResponseEntity<ErrorResponse> respond(
            HttpStatus status, String message) {

        ErrorResponse response = new ErrorResponse();
        response.setStatus(status.value());
        response.setMessage(message);
        response.setTimestamp(OffsetDateTime.now());

        return ResponseEntity.status(status).body(response);
    }
}
