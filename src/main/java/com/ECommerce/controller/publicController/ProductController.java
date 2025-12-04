package com.ECommerce.controller.publicController;

import com.ECommerce.dto.response.ApiResponse;
import com.ECommerce.dto.response.product.*;
import com.ECommerce.service.ProductService;
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
@RequestMapping("/api/public/products")
public class ProductController {
    private final ProductService productService;

    @GetMapping("/tags")
    public ResponseEntity<ApiResponse<List<TagResponse>>> getAllTags(){
        List<TagResponse> tags = productService.getAllTags();
        return ResponseEntity.ok(ApiResponse.ok(tags, "Tags fetched"));
    }

    @GetMapping("/brands")
    public ResponseEntity<ApiResponse<List<BrandResponse>>> getAllBrands(){
        List<BrandResponse> brands = productService.getAllBrands();
        return ResponseEntity.ok(ApiResponse.ok(brands, "Brands fetched"));
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories(){
        List<CategoryResponse> categories = productService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.ok(categories, "Categories fetched"));
    }

//    customization
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
    //brand
//already return gareko xa but still research garam k hunxa ramro practice chai,
//    frontend bata filter garney ki backend maa feri req pathaune


}
