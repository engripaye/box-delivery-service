package com.ipaye.box_delivery_service.dto;

public record ItemResponse(
        Long id,
        String name,
        Integer weight,
        String code
) {
}
