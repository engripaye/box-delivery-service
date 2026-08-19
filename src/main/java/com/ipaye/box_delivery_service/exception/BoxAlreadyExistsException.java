package com.ipaye.box_delivery_service.exception;

public class BoxAlreadyExistsException extends RuntimeException {
    public BoxAlreadyExistsException(String txref) {
        super("Box with transaction reference '" + txref + "' already exists");
    }
}
