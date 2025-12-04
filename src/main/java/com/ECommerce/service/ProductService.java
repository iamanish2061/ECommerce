package com.ECommerce.service;

import com.ECommerce.dto.response.product.*;
import com.ECommerce.exception.ApplicationException;
import com.ECommerce.model.product.*;
import com.ECommerce.repository.BrandRepository;
import com.ECommerce.repository.CategoryRepository;
import com.ECommerce.repository.ProductRepository;
import com.ECommerce.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final TagRepository tagRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public List<TagResponse> getAllTags(){
        List<TagModel> tagModels = tagRepository.findAll();
        return tagModels.stream()
                .map(tag-> new TagResponse(tag.getName(), tag.getSlug()))
                .toList();
    }

    public List<BrandResponse> getAllBrands() {
        List<BrandModel> brands = brandRepository.findAll();
        return brands.stream()
                .map(brand-> new BrandResponse(brand.getName(), brand.getSlug(), brand.getLogoUrl()))
                .toList();
    }

    public List<CategoryResponse> getAllCategories() {
        List<CategoryModel> categories = categoryRepository.findAll();
        return categories.stream()
                .map(c-> new CategoryResponse(c.getName(), c.getSlug(), c.getImageUrl()))
                .toList();
    }

    //pagination
//    also according to logged in or not
//    if role is admin or user not logged in , display product as it is
//    if user is logged in, kaam garna baki xa algorithm halera preference anushar
    public List<AllProductsResponse> getAllProducts() {
        List<ProductModel> products = productRepository.findAll();
        return products.stream()
                .map(p-> new AllProductsResponse(
                        p.getId(),
                        p.getTitle(),
                        p.getShortDescription(),
                        p.getSellingPrice(),
                        p.getStock(),
                        p.getImages().stream()
                                .filter(img-> img.isThumbnail())
                                .map(img-> img.getUrl())
                                .findFirst()
                                .orElse(null)
                        ))
                .toList();
    }

    public SingleProductResponse getDetailOfProduct(Long id) {
        ProductModel product = productRepository.findById(id).orElseThrow(()->
                new ApplicationException("Product not found!", "PRODUCT_NOT_FOUND", HttpStatus.BAD_REQUEST));

        return new SingleProductResponse(
                product.getId(),
                product.getSku(),
                product.getTitle(),
                product.getShortDescription(),
                product.getDescription(),
                new BrandResponse(
                    product.getBrand().getName(),
                    product.getBrand().getSlug(),
                    product.getBrand().getLogoUrl()
                ),
                new CategoryResponse(
                    product.getCategory().getName(),
                    product.getCategory().getSlug(),
                    product.getCategory().getImageUrl()
                ),
                product.getSellingPrice(),
                product.getStock(),
                product.getSizeMl(),
                product.getTags().stream()
                        .map(tag-> new TagResponse(tag.getName(), tag.getSlug()))
                        .toList(),
                product.getImages().stream()
                        .map(img-> new ProductImageResponse(img.getUrl(), img.getAltText(), img.isThumbnail()))
                        .toList()

        );
    }



}
