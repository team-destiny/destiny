package com.destiny.productservice.application.dto;

import java.util.List;

public class ProductValidationSuccess {
    List<ProductValidationMessage> messageList;

    public ProductValidationSuccess(List<ProductValidationMessage> messageList) {
        this.messageList = messageList;
    }
}
