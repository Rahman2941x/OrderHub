package com.product_service.controller;

import com.product_service.dto.ProductDTO;
import com.product_service.dto.ProductValidationRequest;
import com.product_service.dto.ResponseDTO;
import com.product_service.entity.Operation;
import com.product_service.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    ProductService productService;

    @GetMapping("/test")
    public ResponseEntity<ResponseDTO<?>> testAPI() {
        return ResponseEntity.ok(new ResponseDTO<>("Success"));
    }

    @GetMapping("/get/all")
    public ResponseEntity<ResponseDTO<Page<ProductDTO>>> getAllProduct(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return ResponseEntity.ok(new ResponseDTO<>(productService.getAllProduct(page, size)));
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseDTO<?>> createProduct(
            @RequestBody ProductDTO productDTO
    ) {
        return ResponseEntity.ok(new ResponseDTO<>("Product has been successfully created.", productService.createProduct(productDTO)));
    }

    @PostMapping("/create/list")
    public ResponseEntity<ResponseDTO<?>> createListProduct(
            @RequestBody List<ProductDTO> productDTO
    ) {
        return ResponseEntity.ok(new ResponseDTO<>(
                "Product has been successfully created.",
                productService.createListProduct(productDTO)));
    }

    @GetMapping("/get/id")
    public ResponseEntity<ResponseDTO<?>> getProductById(
            @RequestParam Long id) {
        return ResponseEntity.ok(new ResponseDTO<>(productService.getProductById(id)));
    }

    @GetMapping("/get/name")
    public ResponseEntity<ResponseDTO<ProductDTO>> getProductByName(
            @RequestParam String productName
    ) {
        return ResponseEntity.ok(new ResponseDTO<>(productService.getProductByName(productName)));
    }

    @GetMapping("/get/category")
    public ResponseEntity<ResponseDTO<?>> getProductByCategory(
            @RequestParam String category
    ) {
        return ResponseEntity.ok(new ResponseDTO<>(productService.getProductByCategory(category)));
    }

    @GetMapping("/get/productOwner")
    public ResponseEntity<ResponseDTO<?>> getProductByOwner(
            @RequestParam Long productOwnerId
    ) {
        return ResponseEntity.ok(new ResponseDTO<>(productService.getProductByOwner(productOwnerId)));
    }

    @PatchMapping("/update/id")
    public ResponseEntity<ResponseDTO<?>> updateProductById(@RequestParam Long id,
                                                            @RequestBody ProductDTO productDTO) {
        return ResponseEntity.ok(new ResponseDTO<>(productService.updateProductById(id, productDTO)));
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("delete/id")
    public ResponseEntity<ResponseDTO<?>> deleteProductById(@RequestParam Long id) {
        return ResponseEntity.ok(
                new ResponseDTO<>(
                        "Product has been deleted",
                        productService.deleteProductById(id)
                )
        );
    }

    @PatchMapping("/activation/id")
    public ResponseEntity<ResponseDTO<String>> activateProduct(@RequestParam Long id, @RequestParam Boolean isActive) {
        return ResponseEntity.ok(
                new ResponseDTO<>(
                        "Request has been process successfully",
                        productService.ActivationAndDeActiveProduct(id, isActive)
                )
        );
    }

    @PatchMapping("/update/stock/id")
    public ResponseEntity<ResponseDTO<String>> updateStockById(@RequestParam Long id,
                                                               @RequestParam Integer stock) {
        return ResponseEntity.ok(
                new ResponseDTO<>(
                        productService.updateStockById(id, stock)
                )
        );
    }

    @PatchMapping("/update/price/id")
    public ResponseEntity<ResponseDTO<String>> updatePriceById(@RequestParam Long id,
                                                               @RequestParam BigInteger price) {
        return ResponseEntity.ok(
                new ResponseDTO<>(
                        productService.updatePriceById(id, price)
                )
        );
    }

    @PatchMapping("/stock/adjust")
    public ResponseEntity<ResponseDTO<String>> adjustStockToProduct(@RequestParam Long id,
                                                                    @RequestParam Integer stock,
                                                                    @RequestParam Operation operation) {
        return ResponseEntity.ok(
                new ResponseDTO<>(
                        productService.adjustStockById(id, stock, operation)
                )
        );
    }

    @PatchMapping("/price/adjust")
    public ResponseEntity<ResponseDTO<String>> adjustPriceToProduct(@RequestParam Long id,
                                                                    @RequestParam BigInteger price,
                                                                    @RequestParam Operation operation) {
        return ResponseEntity.ok(
                new ResponseDTO<>(
                        productService.adjustPriceById(id, price, operation)
                )
        );
    }

    @GetMapping("/validate/stock")
    public ResponseEntity<ResponseDTO<?>> validateProduct(@RequestParam Long id, @RequestParam Integer quantity) {
        return ResponseEntity.ok(
                new ResponseDTO<>(
                        productService.validateProduct(id, quantity)
                )
        );
    }

    @GetMapping("/validate/list/stock")
    public ResponseEntity<ResponseDTO<?>> validateListProduct(@RequestBody List<ProductValidationRequest> requests) {
        return ResponseEntity.ok(
                new ResponseDTO<>(
                        productService.validateListProduct(requests)
                )
        );
    }
}
