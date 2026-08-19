package com.ipaye.box_delivery_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BoxNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBoxNotFound(
            BoxNotFoundException exception) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );
    }

    @ExceptionHandler(BoxAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleBoxAlreadyExists(
            BoxAlreadyExistsException exception) {

        return buildResponse(
                HttpStatus.CONFLICT,
                exception.getMessage()
        );
    }

    @ExceptionHandler(InsufficientBatteryException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientBattery(
            InsufficientBatteryException exception) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                exception.getMessage()
        );
    }

    @ExceptionHandler(InsufficientCapacityException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientCapacity(
            InsufficientCapacityException exception) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                exception.getMessage()
        );
    }

    @ExceptionHandler(InvalidBoxStateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidBoxState(
            InvalidBoxStateException exception) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                exception.getMessage()
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException exception) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                exception.getMessage()
        );
    }

    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status,
            String message) {

        ErrorResponse response = new ErrorResponse(
                status.value(),
                message,
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(status)
                .body(response);
    }

}
