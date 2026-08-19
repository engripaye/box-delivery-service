package com.ipaye.box_delivery_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record LoadItemsRequest(
        @NotEmpty(message = "At least one item is required")
        List<@Valid ItemRequest> items
) {
}
