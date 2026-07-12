package com.order_service.dto;

import com.order_service.entity.PaymentMode;

import java.util.List;

public record OrderRequest(
        String address,
        List<ProductValidationRequest> productQuantityList,
        PaymentMode paymentMode
) {
}
