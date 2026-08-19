package com.ipaye.box_delivery_service.exception;

public class InvalidBoxStateException extends RuntimeException {
    public InvalidBoxStateException(String message) {
        super(message);
    }
}
