package com.ipaye.box_delivery_service.dto;

import jakarta.validation.constraints.*;

public record ItemRequest(
        @NotBlank(message = "Item name is required")
        @Size(max = 100, message = "Item name must not exceed 100 characters")
        @Pattern(
                regexp = "^[a-zA-Z0-9_-]+$",
                message = "Item name may contain only letters, numbers, hyphens and underscores"
        )
        String name,

        @NotNull(message = "Item weight is required")
        @Positive(message = "Item weight must be greater than 0")
        Integer weight,

        @NotBlank(message = "Item code is required")
        @Size(max = 100, message = "Item code must not exceed 100 characters")
        @Pattern(
                regexp = "^[A-Z0-9_]+$",
                message = "Item code may contain only uppercase letters, numbers and underscores"
        )
        String code
) {
}
