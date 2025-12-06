package com.ECommerce.service.products;

import com.ECommerce.dto.response.product.*;
import com.ECommerce.exception.ApplicationException;
import com.ECommerce.model.product.*;
import com.ECommerce.repository.product.BrandRepository;
import com.ECommerce.repository.product.CategoryRepository;
import com.ECommerce.repository.product.ProductRepository;
import com.ECommerce.repository.product.TagRepository;
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
                                .filter(ProductImageModel::isThumbnail)
                                .map(ProductImageModel::getUrl)
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

    public List<AllProductsResponse> getAllProductsExcept(List<Long> personalizedProductIds) {
        if (personalizedProductIds == null || personalizedProductIds.isEmpty()) {
            return getAllProducts();
        }
        List<ProductModel> products = productRepository.findByIdNotIn(personalizedProductIds);
        return products.stream()
                .map(p -> new AllProductsResponse(
                        p.getId(),
                        p.getTitle(),
                        p.getShortDescription(),
                        p.getSellingPrice(),
                        p.getStock(),
                        p.getImages().stream()
                                .filter(ProductImageModel::isThumbnail)
                                .map(ProductImageModel::getUrl)
                                .findFirst().orElse(null)
                ))
            .toList();
    }



}
