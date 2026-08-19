package com.ipaye.box_delivery_service.dto;

import java.time.LocalDateTime;

public record ErrorResponse(

        LocalDateTime timeStamp,

        int Status,

        String error,

        String message,

        String path
) {
}
