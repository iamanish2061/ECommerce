package com.ECommerce.controller.admin;

import com.ECommerce.dto.request.admin.AddProductRequest;
import com.ECommerce.dto.response.ApiResponse;
import com.ECommerce.dto.response.admin.product.AllProductsResponse;
import com.ECommerce.dto.response.admin.product.SingleProductResponse;
import com.ECommerce.exception.ApplicationException;
import com.ECommerce.service.admin.AdminProductService;
import com.ECommerce.service.user.ProductService;
import com.ECommerce.validation.ValidId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {

    private final AdminProductService adminProductService;
    private final ProductService productService;

    @PostMapping("/")
    public ResponseEntity<ApiResponse<SingleProductResponse>> addNewProduct(
            @Valid @RequestPart("addProductRequest") AddProductRequest addProductRequest,
            @RequestPart("imageFiles") List<MultipartFile> imageFiles
    )throws ApplicationException {
        SingleProductResponse response = adminProductService.addNewProduct(addProductRequest, imageFiles);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response, "New product added successfully"));
    }

   @GetMapping("/")
    public ResponseEntity<ApiResponse<List<AllProductsResponse>>> getAllProducts(){
        List<AllProductsResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(ApiResponse.ok(products, "Fetched successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SingleProductResponse>> getDetailOfProduct(
            @ValidId @PathVariable Long id
    ){
        SingleProductResponse product = productService.getDetailOfProduct(id);
        return ResponseEntity.ok(ApiResponse.ok(product, "Fetched successfully"));
    }

    @PutMapping("/price/{id}")
    public ResponseEntity<ApiResponse<?>> updateProductPrice(
            @ValidId @PathVariable Long id,
            @RequestParam Double price
    ){
        if(adminProductService.updatePrice(id, price)){
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
        if(adminProductService.updateQuantity(id, quantity)){
            return ResponseEntity.ok(ApiResponse.ok("Quantity updated successfully"));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to update quantity!", "FAILED_TO_UPDATE_QUANTITY"));
    }

    


}
