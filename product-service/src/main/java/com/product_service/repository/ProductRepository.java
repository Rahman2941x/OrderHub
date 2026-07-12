package com.product_service.repository;

import com.product_service.dto.ProductDTO;
import com.product_service.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {


    @Query(value = "SELECT * FROM product p WHERE p.product_name=:productName AND p.active=true", nativeQuery = true)
    Optional<Product> findByProductName(@Param("productName") String productName);

    @Query("SELECT p FROM Product p WHERE p.category=:category AND p.active=true")
    Optional<Product> findByCategory(@Param("category") String category);


    Optional<Product> findByProductOwnerIdAndActiveTrue(Long productOwnerId);

    Boolean existsByProductNameAndCategory(String s, String category);

    Optional<Product> findByProductNameIn(List<String> productName);
}
