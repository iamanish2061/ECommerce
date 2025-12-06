package com.ECommerce.controller.admin;

import com.ECommerce.dto.request.product.*;
import com.ECommerce.dto.response.ApiResponse;
import com.ECommerce.dto.response.product.AdminSingleProductResponse;
import com.ECommerce.dto.response.product.SingleProductResponse;
import com.ECommerce.exception.ApplicationException;
import com.ECommerce.service.products.ProductService;
import com.ECommerce.service.admin.AdminProductService;
import com.ECommerce.validation.ValidId;
import com.ECommerce.validation.ValidPrice;
import com.ECommerce.validation.ValidQuantity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {

    private final AdminProductService adminProductService;
    private final ProductService productService;

    //for adding tags
    //comma separated value ko list banayera pathauney
    @PostMapping("/tags")
    public ResponseEntity<ApiResponse<String>> addTags(
            @Valid @RequestBody AddTagRequest tagRequests
    )throws ApplicationException{
        adminProductService.addTags(tagRequests);
        return ResponseEntity.ok(
                ApiResponse.ok("Tags added successfully"));
    }

    //for deleting tag
//    dropdown bata select garera pathaune
    @PutMapping("/tags")
    public ResponseEntity<ApiResponse<String>> deleteTag(
        @Pattern(regexp = "^[a-zA-Z0-9\\s&()\\-.,:]{2,100}$", message ="Invalid Tag!")
        @RequestParam String name
    ){
        adminProductService.deleteTag(name);
        return ResponseEntity.ok(ApiResponse.ok("Tags deleted successfully"));
    }

    //for adding tag to specific product
    //like offer tag, discount tag
    // drop down bata tag select garney and product ni garera pathaune (checking if the product already has that tag frintend mai )
    @PutMapping("/add-tag-to-product")
    public ResponseEntity<ApiResponse<?>> addTagToProduct(
            @Pattern(regexp = "^[a-zA-Z0-9\\s&()\\-.,:]{2,100}$", message ="Invalid Tag!")
            @RequestParam String tag,
            @ValidId
            @RequestParam Long productId
    ){
        adminProductService.addTagToProduct(tag, productId);
        return ResponseEntity.ok(
                ApiResponse.ok("Tag added to product.")
        );
    }

    //for removing tag from specific product
    //like offer tag, discount tag
    // drop down bata tag select garney and product ni garera pathaune (checking if the product already has that tag frintend mai )
    @PutMapping("/remove-tag-from-product")
    public ResponseEntity<ApiResponse<?>> removeTagFromProduct(
            @Pattern(regexp = "^[a-zA-Z0-9\\s&()\\-.,:]{2,100}$", message ="Invalid Tag!")
            @RequestParam String tag,
            @ValidId
            @RequestParam Long productId
    ){
        adminProductService.removeTagFromProduct(tag, productId);
        return ResponseEntity.ok(
                ApiResponse.ok("Tag removed from product.")
        );
    }

    //for adding brand
    @PostMapping(value = "/brand", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<?>> addBrand(
        @Valid @RequestPart("addBrandRequest") AddBrandRequest addBrandRequest,
        @RequestPart("logo") MultipartFile logo
    ){
        adminProductService.addBrand(addBrandRequest, logo);
        return ResponseEntity.ok(ApiResponse.ok("Brand added successfully"));
    }

    //for adding category
    @PostMapping(value = "/category", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<?>> addCategory(
        @Valid @RequestPart("addCategoryRequest")AddCategoryRequest addCategoryRequest,
        @RequestPart("image") MultipartFile image
    ){
        adminProductService.addCategory(addCategoryRequest, image);
        return ResponseEntity.ok(ApiResponse.ok("Category added successfully"));
    }

    //for adding product
    @PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<SingleProductResponse>> addNewProduct(
        @Valid @RequestPart("addProductRequest") AddProductRequest addProductRequest,
        @RequestPart("imageFiles") List<MultipartFile> imageFiles
    )throws ApplicationException {
        SingleProductResponse response = adminProductService.addNewProduct(addProductRequest, imageFiles);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response, "New product added successfully"));
    }

    //for adding image for products
    @PostMapping(value = "/{productId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<?>> addImage(
            @Valid @RequestPart("addProductImageRequest") AddProductImageRequest addProductImageRequest,
            @RequestPart("image") MultipartFile image,
            @ValidId @PathVariable Long productId
    ){
        adminProductService.addImage(productId,addProductImageRequest, image);
        return ResponseEntity.ok(ApiResponse.ok("Brand added successfully"));
    }

    // for getting detail of specific product
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminSingleProductResponse>> getAdminDetailOfProduct(
            @ValidId @PathVariable Long id
    ){
        AdminSingleProductResponse product = adminProductService.getAdminDetailOfProduct(id);
        return ResponseEntity.ok(ApiResponse.ok(product, "Fetched successfully"));
    }

    //for updating price of product
    @PutMapping("/{id}/price")
    public ResponseEntity<ApiResponse<?>> updateProductPrice(
            @ValidId @PathVariable Long id,
            @ValidPrice @RequestParam BigDecimal price
    ){
        adminProductService.updatePrice(id, price);
        return ResponseEntity.ok(ApiResponse.ok("Price updated successfully"));
    }

    //for updating quantity/stock of product
    @PutMapping("/{id}/quantity")
    public ResponseEntity<ApiResponse<?>> updateProductQuantity(
            @ValidId @PathVariable Long id,
            @ValidQuantity @RequestParam int quantity
    ){
        adminProductService.updateQuantity(id, quantity);
        return ResponseEntity.ok(ApiResponse.ok("Quantity updated successfully"));
    }

}
