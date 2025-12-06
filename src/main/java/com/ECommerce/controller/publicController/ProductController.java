package com.ECommerce.controller.publicController;

import com.ECommerce.dto.response.ApiResponse;
import com.ECommerce.dto.response.product.*;
import com.ECommerce.model.user.UserPrincipal;
import com.ECommerce.service.products.ProductService;
import com.ECommerce.service.products.RecommendationService;
import com.ECommerce.validation.ValidId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/public/products")
public class ProductController {
    private final ProductService productService;
    private final RecommendationService recommendationService;

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

    @GetMapping()
    public ResponseEntity<ApiResponse<Map<String,List<AllProductsResponse>>>> getAllProducts(
        @AuthenticationPrincipal UserPrincipal currentUser
    ){
        if(currentUser == null || currentUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))){
            Map<String, List<AllProductsResponse>> res = new HashMap<>();
            res.put("products", productService.getAllProducts());
            return ResponseEntity.ok(
                    ApiResponse.ok(res, "Fetched successfully"));
        }
        List<AllProductsResponse> personalizedProducts = recommendationService.getPersonalizedRecommendation(currentUser.getUser().getId());
        List<Long> personalizedIds = personalizedProducts.stream()
                .map(AllProductsResponse::id)
                .toList();
        List<AllProductsResponse> otherProducts = productService.getAllProductsExcept(personalizedIds);
        Map<String, List<AllProductsResponse>> response = new HashMap<>();
        response.put("personalized", personalizedProducts);
        response.put("otherProducts", otherProducts);
        return ResponseEntity.ok(ApiResponse.ok(response, "Fetched successfully"));
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
