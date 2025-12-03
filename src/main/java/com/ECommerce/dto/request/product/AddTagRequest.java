package com.ECommerce.dto.request.product;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record AddTagRequest (
    @NotEmpty(message = "Tag names list cannot be empty.")
    List<String> names
){}
