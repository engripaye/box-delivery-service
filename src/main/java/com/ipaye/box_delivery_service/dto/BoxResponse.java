package com.ipaye.box_delivery_service.dto;

import com.ipaye.box_delivery_service.enums.BoxState;

public record BoxResponse(
        Long id,
        String txref,
        Integer weightLimit,
        Integer batteryCapacity,
        BoxState state
) {
}
