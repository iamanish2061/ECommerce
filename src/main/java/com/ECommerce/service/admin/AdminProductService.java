package com.ECommerce.service.admin;

import com.ECommerce.dto.request.admin.AddProductImageRequest;
import com.ECommerce.dto.request.admin.AddProductRequest;
import com.ECommerce.dto.response.admin.product.SingleProductResponse;

import com.ECommerce.exception.ApplicationException;
import com.ECommerce.model.product.*;
import com.ECommerce.repository.BrandRepository;
import com.ECommerce.repository.CategoryRepository;
import com.ECommerce.repository.ProductRepository;
import com.ECommerce.repository.TagRepository;
import com.ECommerce.utils.HelperClass;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class AdminProductService {

    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final ProductRepository productRepository;

    public SingleProductResponse addNewProduct(AddProductRequest request, List<MultipartFile> imageFiles) throws ApplicationException{

        //code for saving images here


        BrandModel brand = brandRepository.findByName(request.brandName().trim())
                .orElseThrow(()->new ApplicationException("Brand not found", "BRAND_NOT_FOUND", HttpStatus.BAD_REQUEST));

        CategoryModel category = categoryRepository.findByName(request.categoryName().trim())
                .orElseThrow(()->new ApplicationException("Category not found!", "CATEGORY_NOT_FOUND", HttpStatus.BAD_REQUEST));

        // Creating Product
        ProductModel product = new ProductModel();
        product.setSku(request.sku().trim().toUpperCase());
        product.setTitle(request.title().trim());
        product.setSlug(HelperClass.generateSlug(request.title().trim()));
        product.setShortDescription(request.shortDescription());
        product.setDescription(request.description());
        product.setBrand(brand);
        product.setCategory(category);
        product.setBasePrice(request.basePrice());
        product.setSellingPrice(request.sellingPrice());
        product.setStock(request.stock() != null ? request.stock() : 0);
        product.setSizeMl(request.sizeMl());
        product.setActive(request.active());

        // Handling Tags
        Set<TagModel> tags = new HashSet<>();
        for (String slug : request.tagSlugs()) {
            String cleanSlug = slug.trim().toLowerCase();
            if (cleanSlug.isBlank()) continue;
            TagModel tag = tagRepository.findBySlug(cleanSlug)
                    .orElseThrow(()-> new ApplicationException("Tag not found!", "TAG_NOT_FOUND", HttpStatus.BAD_REQUEST));
            tags.add(tag);
        }
        product.setTags(tags);

        // Handling Images (which are already uploaded & saved permanently)
        List<ProductImageModel> images = new ArrayList<>();
        for (int i = 0; i < request.images().size(); i++) {
            AddProductImageRequest imgReq = request.images().get(i);

            ProductImageModel img = new ProductImageModel();
            img.setUrl(imgReq.name());           // this is the final URL: /uploads/products/xxx.jpg
            img.setAltText(imgReq.altText() != null ? imgReq.altText() : request.title());
            img.setSortOrder(imgReq.sortOrder());
            img.setThumbnail(imgReq.thumbnail());
            img.setProduct(product);             // back reference
            images.add(img);
        }
        product.setImages(images);

        // 6. Save everything (cascade handles images + tags)
        ProductModel savedProduct = productRepository.save(product);
        return null;
    }

    public boolean updatePrice(Long id, Double price) {
        return false;
    }

    public boolean updateQuantity(Long id, int quantity) {
        return false;
    }




}
