package com.ipaye.box_delivery_service.exception;

public class InsufficientBatteryException extends RuntimeException {

    public InsufficientBatteryException(String message) {
        super(message);
    }
}
