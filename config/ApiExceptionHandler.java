package com.arabbank.hdf.uam.brain.config;

import com.arabbank.hdf.uam.brain.auth.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUserNotFoundException(UserNotFoundException e) {
        return getErrorEntity(e, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUserNotFoundException(Exception e) {
        return new ResponseEntity<>(new ApiErrorResponse(e), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity<ApiErrorResponse> getErrorEntity(Exception e, HttpStatus status) {
        return new ResponseEntity<>(new ApiErrorResponse(e), status);
    }
}
