package com.destiny.productservice.application.dto;

import java.util.List;

public record ProductValidationFail (
    List<ProductFailDetail> failDetails
) { }
