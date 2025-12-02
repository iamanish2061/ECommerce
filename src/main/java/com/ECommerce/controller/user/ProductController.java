package com.ECommerce.controller.user;

import com.ECommerce.dto.response.ApiResponse;
import com.ECommerce.dto.response.admin.product.AllProductsResponse;
import com.ECommerce.dto.response.admin.product.SingleProductResponse;
import com.ECommerce.service.user.ProductService;
import com.ECommerce.validation.ValidId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping()
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

    //searched product

    //category


}
