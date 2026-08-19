package com.ipaye.box_delivery_service.dto;

public record BatteryResponse(
        String txref,

        Integer batteryCapacity
) {
}
