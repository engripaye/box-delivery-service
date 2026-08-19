package com.ipaye.box_delivery_service.dto;

import jakarta.validation.constraints.*;

public record CreateBoxRequest(
        @NotBlank(message = "Transaction reference is required")
        @Size(max = 20, message = "Transaction reference must not exceed 20 characters")
        String txref,

        @NotNull(message = "Weight limit is required")
        @Min(value = 1, message = "Weight limit must be greater than 0")
        @Max(value = 500, message = "Weight limit must not exceed 500 grams")
        Integer weightLimit,

        @NotNull(message = "Battery capacity is required")
        @Min(value= 0, message = "Battery capacity cannot be below 0")
        @Max(value = 100, message = "Battery capacity cannot exceed 100")
        Integer batteryCapacity


) {
}
