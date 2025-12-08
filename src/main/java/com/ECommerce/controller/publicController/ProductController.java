package com.ECommerce.controller.publicController;

import com.ECommerce.dto.response.ApiResponse;
import com.ECommerce.dto.response.product.*;
import com.ECommerce.model.user.UserPrincipal;
import com.ECommerce.service.products.ProductService;
import com.ECommerce.service.recommendation.RecommendationService;
import com.ECommerce.validation.ValidId;
import jakarta.validation.constraints.NotBlank;
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

//end point for getting brand name that can be used in dropdowns (admin) useful while adding products
// and for displaying brands we have in brand section (customer sees it)
    @GetMapping("/brand-name")
    public ResponseEntity<ApiResponse<List<BrandResponse>>> getAllBrands(){
        List<BrandResponse> brands = productService.getAllBrands();
        return ResponseEntity.ok(ApiResponse.ok(brands, "Brand name fetched"));
    }

//    after customer clicks particular brand from the list this end point return brand info
    // and products of that brand that we have
    //NOTE SEND SLUG WHILE SENDING DATA IN PATH VARIABLE
    @GetMapping("/brand-products/{brandSlug}")
    public ResponseEntity<ApiResponse<BrandWithProductResponse>> getProductsOfBrand(
        @NotBlank(message = "Brand is required")
        @PathVariable String brandSlug
    ){
        BrandWithProductResponse response = productService.getProductsOfBrand(brandSlug);
        return ResponseEntity.ok(ApiResponse.ok(response, "Products of: "+ brandSlug));
    }


    //end point for getting category name that can be used in dropdowns (admin) useful while adding products
// and for displaying categories we have in brand section (customer sees it)
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories(){
        List<CategoryResponse> categories = productService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.ok(categories, "Categories fetched"));
    }

    @GetMapping("/categories/{categorySlug}")
    public ResponseEntity<ApiResponse<?>> getProductsOfCategory(
            @NotBlank(message = "Category is required")
            @PathVariable String categorySlug
    ){
        List<CategoryResponse> categories = productService.getProductsOfCategory(categorySlug);
        return ResponseEntity.ok(ApiResponse.ok(categories, "Products fetched of: "+categorySlug));
    }



    @GetMapping("/tags")
    public ResponseEntity<ApiResponse<List<TagResponse>>> getAllTags(){
        List<TagResponse> tags = productService.getAllTags();
        return ResponseEntity.ok(ApiResponse.ok(tags, "Tags fetched"));
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
            @AuthenticationPrincipal UserPrincipal currentUser,
            @ValidId @PathVariable Long id
    ){
        SingleProductResponse product = productService.getDetailOfProduct(currentUser, id);
        return ResponseEntity.ok(ApiResponse.ok(product, "Fetched successfully"));
    }

    //searched product


    //category
    //brand
//already return gareko xa but still research garam k hunxa ramro practice chai,
//    frontend bata filter garney ki backend maa feri req pathaune


}
