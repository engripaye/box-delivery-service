package com.ipaye.box_delivery_service.exception;

public class BoxNotFoundException extends RuntimeException {

    public BoxNotFoundException(String txref) {
        super("Box with transaction reference '" + txref + "' was not found");
    }
}
