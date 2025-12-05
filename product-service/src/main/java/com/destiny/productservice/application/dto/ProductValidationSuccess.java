package com.destiny.productservice.application.dto;

import java.util.List;

public record ProductValidationSuccess (
    List<ProductValidationMessage> messageList
) { }
