package com.product_service.util;

import com.product_service.dto.ProductDTO;
import com.product_service.dto.ProductDetailResponseDTO;
import com.product_service.entity.Product;

public class MapToDTO {
    public static ProductDTO mapToProduct(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getProductName(),
                product.getCategory(),
                product.getDescription(),
                product.getProductOwnerId(),
                product.getPrice(),
                product.getInStock()
        );
    }

    public static ProductDetailResponseDTO mapToProductDetailResponse(Product product) {
        return new ProductDetailResponseDTO(
                product.getId(),
                product.getProductName(),
                product.getCategory(),
                product.getDescription(),
                product.getProductOwnerId(),
                product.getPrice(),
                product.getActive(),
                product.getInStock(),
                null
        );
    }
}
