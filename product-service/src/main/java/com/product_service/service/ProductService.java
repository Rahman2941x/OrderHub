package com.product_service.service;

import com.product_service.dto.ProductDTO;
import com.product_service.dto.ProductValidationRequest;
import com.product_service.dto.ProductValidationResponse;
import com.product_service.entity.Operation;
import com.product_service.entity.Product;
import com.product_service.exception.ProductAlreadyExistException;
import com.product_service.exception.ProductNotActiveException;
import com.product_service.exception.ProductNotFoundException;
import com.product_service.repository.ProductRepository;
import com.product_service.util.MapToDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final Logger logger = LoggerFactory.getLogger(ProductService.class);


    @Autowired
    ProductRepository productRepository;

    private static Product getProduct(ProductDTO productDTO) {

        Long ownerId = (Long) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        return new Product(
                productDTO.productName(),
                productDTO.category(),
                productDTO.description(),
                ownerId,
                productDTO.price(),
                productDTO.inStock()
        );
    }

    public Page<ProductDTO> getAllProduct(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPageable = productRepository.findAll(pageable);

        return productPageable.map(product -> new ProductDTO(
                product.getId(),
                product.getProductName(),
                product.getCategory(),
                product.getDescription(),
                product.getProductOwnerId(),
                product.getPrice(),
                product.getInStock()
        ));

    }

    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(String.valueOf(id)));
        if (!product.getActive()) {
            throw new ProductNotActiveException(id.toString());
        }
        return MapToDTO.mapToProduct(product);
    }

    public ProductDTO getProductByName(String productName) {
        Product product = productRepository.findByProductName(productName).orElseThrow(() -> new ProductNotFoundException(productName));
        if (!product.getActive()) {
            throw new ProductNotActiveException(productName);
        }
        return MapToDTO.mapToProduct(product);
    }

    public Object getProductByCategory(String category) {
        Product product = productRepository.findByCategory(category).orElseThrow(() -> new ProductNotFoundException(category
        ));
        if (!product.getActive()) {
            throw new ProductNotActiveException(category);
        }
        return MapToDTO.mapToProduct(product);
    }

    public Object getProductByOwner(Long productOwnerId) {
        Product product = productRepository.findByProductOwnerIdAndActiveTrue(productOwnerId).orElseThrow(() -> new ProductNotFoundException(productOwnerId.toString()));
        if (!product.getActive()) {
            throw new ProductNotActiveException(productOwnerId.toString());
        }
        return MapToDTO.mapToProduct(product);
    }

    public ProductDTO createProduct(ProductDTO productDTO) {
        Boolean isProductAvailable =
                productRepository.existsByProductNameAndCategory(
                        productDTO.productName(),
                        productDTO.category()
                );
        if (isProductAvailable) {
            throw new ProductAlreadyExistException(productDTO.productName());
        }
        Product product = getProduct(productDTO);

        Product savedProduct = productRepository.save(product);

        return MapToDTO.mapToProduct(savedProduct);
    }

    @Transactional
    public List<Product> createListProduct(List<ProductDTO> productDTOs) {


        List<String> productName = productDTOs.stream()
                .map(ProductDTO::productName)
                .distinct()
                .toList();

        List<String> existingProductNames = productRepository
                .findByProductNameIn(productName)
                .stream()
                .map(Product::getProductName)
                .toList();

        logger.warn("These products already exist: {}", existingProductNames);

        Long ownerId = (Long) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        Map<String, Product> productMap = productDTOs.stream()
                .filter(prod -> !existingProductNames.contains(prod.productName()))
                .map(prod -> new Product(
                        prod.productName(),
                        prod.category(),
                        prod.description(),
                        ownerId,
                        prod.price(),
                        prod.inStock()
                )).collect(Collectors.toMap(
                        p -> p.getProductName() + "-" + p.getCategory(),
                        Function.identity(),
                        (ex, dup) -> {
                            ex.setInStock(ex.getInStock() + dup.getInStock());
                            return ex;
                        }
                ));

        return productRepository.saveAll(productMap.values());

    }

    public Product updateProductById(Long id, ProductDTO productDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotActiveException(id.toString()));

        if (!product.getActive()) {
            throw new ProductNotActiveException(product.getProductName());
        }

        Optional.ofNullable(productDTO.productName())
                .ifPresent(product::setProductName);

        Optional.ofNullable(productDTO.category())
                .ifPresent(product::setCategory);

        Optional.ofNullable(productDTO.description())
                .ifPresent(product::setDescription);

        Optional.ofNullable(productDTO.productOwnerId())
                .ifPresent(product::setProductOwnerId);

        Optional.ofNullable(productDTO.inStock())
                .ifPresent(product::setInStock);

        Optional.ofNullable(productDTO.price())
                .ifPresent(product::setPrice);

        return productRepository.save(product);
    }

    public Map<String, String> deleteProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id.toString()));

        Map<String, String> map = new HashMap<>();
        map.put("Product Name", product.getProductName());
        map.put("Product Category", product.getCategory());
        productRepository.delete(product);
        return map;
    }

    public String ActivationAndDeActiveProduct(Long id, Boolean isActive) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id.toString()));

        if (isActive.equals(product.getActive())) {
            return isActive ? "Product is already active" : "Product is already deactivated";
        }
        product.setActive(isActive);
        productRepository.save(product);

        return isActive ? "Product has been activated" : "Product has been deactivated";
    }

    public String updateStockById(Long id, Integer stock) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id.toString()));

        if (!product.getActive()) {
            throw new ProductNotActiveException(product.getProductName());
        }

        if (stock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
        product.setInStock(stock);
        productRepository.save(product);

        return "Product stock has been updated as :" + product.getInStock();
    }

    public String updatePriceById(Long id, BigInteger price) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id.toString()));

        if (!product.getActive()) {
            throw new ProductNotActiveException(product.getProductName());
        }

        if (price.compareTo(BigInteger.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0");
        }
        product.setPrice(price);
        productRepository.save(product);

        return "Product price has been updated as :" + product.getPrice();
    }

    public String adjustStockById(Long id, Integer stock, Operation operation) {

        try {
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new ProductNotFoundException(id.toString()));

            if (stock == 0) {
                throw new IllegalArgumentException("Insufficient stock entered");
            }

            if (!product.getActive()) {
                throw new ProductNotActiveException(product.getProductName());
            }

            Integer finalStock = product.getInStock();
            if (Operation.ADD.equals(operation)) {
                finalStock += stock;
            } else if (Operation.SUBTRACT.equals(operation)) {
                finalStock -= stock;
            }

            if (finalStock < 0) {
                throw new IllegalArgumentException("Insufficient stock");
            }

            product.setInStock(finalStock);
            productRepository.save(product);
            return "Success";
        } catch (Exception e) {
            throw e;

        }
    }

    public String adjustPriceById(Long id, BigInteger price, Operation operation) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id.toString()));

        if (price.compareTo(BigInteger.ZERO) == 0) {
            throw new IllegalArgumentException("Entered Price must be greater than 0");
        }

        if (!product.getActive()) {
            throw new ProductNotActiveException(product.getProductName());
        }

        BigInteger finalPrice = product.getPrice();

        switch (operation) {
            case ADD -> finalPrice = finalPrice.add(price);
            case SUBTRACT -> finalPrice = finalPrice.subtract(price);

        }

        if (finalPrice.compareTo(BigInteger.ZERO) <= 0) {
            throw new IllegalArgumentException("Final price must be greater than 0");
        }
        product.setPrice(finalPrice);
        productRepository.save(product);

        return "Product price has been updated as :" + product.getPrice();
    }


    public Product fetchProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id.toString()));

        if (!product.getActive()) {
            throw new ProductNotActiveException(product.getProductName());
        }

        return product;
    }

    public ProductValidationResponse validateProduct(Long id, Integer quantity) {
        Product product = productRepository
                .findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id.toString()));

        if (!product.getActive()) {
            throw new ProductNotActiveException(product.getProductName());
        }

        if (product.getInStock() < quantity) {
            return new ProductValidationResponse(
                    product.getId(),
                    product.getProductName(),
                    product.getPrice(),
                    false,
                    product.getProductOwnerId(),
                    product.getInStock());
        }

        return new ProductValidationResponse(
                product.getId(),
                product.getProductName(),
                product.getPrice(),
                true,
                product.getProductOwnerId(),
                product.getInStock());
    }

    public List<ProductValidationResponse> validateListProduct(List<ProductValidationRequest> requests) {

        Set<Long> productIds = requests.stream()
                .map(ProductValidationRequest::productId)
                .collect(Collectors.toSet());

        List<Product> productList = productRepository.findAllById(productIds);

        if (!productList.isEmpty()) {
            Set<Long> availableIds = productList.stream()
                    .map(Product::getId)
                    .collect(Collectors.toSet());

            Set<Long> inValidId = new HashSet<>(productIds);
            inValidId.removeAll(availableIds);
            logger.error("Product id not found {}", inValidId);

            Map<Long, Integer> requestMap = requests.stream()
                    .collect(Collectors.toMap(
                            ProductValidationRequest::productId,
                            ProductValidationRequest::quantity,
                            (a, b) -> b
                    ));

            return productList.stream()
                    .map(product -> {
                        Integer requestQuantity = requestMap.get(product.getId());

                        Boolean isAvailable = product.getInStock() >= requestQuantity;

                        return new ProductValidationResponse(
                                product.getId(),
                                product.getProductName(),
                                product.getPrice(),
                                isAvailable,
                                product.getProductOwnerId(),
                                product.getInStock()
                        );
                    }).toList();
        }
        return List.of(new ProductValidationResponse());

    }
}
