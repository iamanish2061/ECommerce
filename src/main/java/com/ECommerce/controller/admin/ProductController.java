package com.ECommerce.controller.admin;

import com.ECommerce.dto.request.admin.AddProductRequest;
import com.ECommerce.dto.response.ApiResponse;
import com.ECommerce.dto.response.admin.SingleProductResponse;
import com.ECommerce.service.admin.ProductService;
import com.ECommerce.validation.ValidId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping()
    public ResponseEntity<ApiResponse<SingleProductResponse>> addNewProduct(
            @Valid @RequestBody AddProductRequest addProductRequest
    ){
        SingleProductResponse response = productService.addNewProduct(addProductRequest);
        return ResponseEntity.ok(ApiResponse.ok(response, "New product added successfully"));
    }


    @PutMapping("/price/{id}")
    public ResponseEntity<ApiResponse<?>> updateProductPrice(
            @ValidId @PathVariable Long id,
            @RequestParam Double price
    ){
        if(productService.updatePrice(id, price)){
            return ResponseEntity.ok(ApiResponse.ok("Price updated successfully"));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to update price!", "FAILED_TO_UPDATE_PRICE"));
    }

    @PutMapping("/quantity/{id}")
    public ResponseEntity<ApiResponse<?>> updateProductPrice(
            @ValidId @PathVariable Long id,
            @RequestParam int quantity
    ){
        if(productService.updateQuantity(id, quantity)){
            return ResponseEntity.ok(ApiResponse.ok("Quantity updated successfully"));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to update quantity!", "FAILED_TO_UPDATE_QUANTITY"));
    }

}
