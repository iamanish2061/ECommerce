package com.ECommerce.service.admin;

import com.ECommerce.dto.request.product.AddProductImageRequest;
import com.ECommerce.dto.request.product.AddProductRequest;

import com.ECommerce.dto.request.product.AddTagRequest;
import com.ECommerce.dto.response.product.BrandResponse;
import com.ECommerce.dto.response.product.CategoryResponse;
import com.ECommerce.dto.response.product.ProductImageResponse;
import com.ECommerce.dto.response.product.TagResponse;
import com.ECommerce.dto.response.product.SingleProductResponse;
import com.ECommerce.exception.ApplicationException;
import com.ECommerce.model.product.*;
import com.ECommerce.repository.BrandRepository;
import com.ECommerce.repository.CategoryRepository;
import com.ECommerce.repository.ProductRepository;
import com.ECommerce.repository.TagRepository;
import com.ECommerce.utils.HelperClass;
import com.ECommerce.utils.ImageUploadHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
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

    @Transactional
    public boolean addTags(AddTagRequest request) throws ApplicationException {
        List<String> incomingNames = request.names().stream()
                .map(String::trim)
                .toList();

        List<String> incomingSlugs = incomingNames.stream()
                .map(HelperClass::generateSlug)
                .toList();

        Set<String> existingSlugs = tagRepository.findSlugsBySlugsIn(incomingSlugs);

        List<TagModel> newTagModels = new ArrayList<>();

        for (String name : incomingNames) {
            String slug = HelperClass.generateSlug(name);

            if (!existingSlugs.contains(slug)) {
                newTagModels.add(new TagModel(name, slug));
            }
        }

        if (!newTagModels.isEmpty()) {
            tagRepository.saveAll(newTagModels);
        }
        return true;
    }

    @Transactional
    public boolean deleteTag(String name){
        TagModel tag = tagRepository.findByName(name).orElseThrow(()->
                new ApplicationException("Tag not found!", "INVALID_TAG", HttpStatus.BAD_REQUEST));

        tagRepository.delete(tag);
        return true;
    }







    @Transactional
    public SingleProductResponse addNewProduct(AddProductRequest request, List<MultipartFile> imageFiles) throws ApplicationException{

        BrandModel brand = brandRepository.findByName(request.brandName().trim())
                .orElseThrow(()->new ApplicationException("Brand not found", "BRAND_NOT_FOUND", HttpStatus.BAD_REQUEST));

        CategoryModel category = categoryRepository.findByName(request.categoryName().trim())
                .orElseThrow(()->new ApplicationException("Category not found!", "CATEGORY_NOT_FOUND", HttpStatus.BAD_REQUEST));

        // Handling Tags
        Set<TagModel> tags = new HashSet<>();
        for (String slug : request.tagSlugs()) {
            String cleanSlug = slug.trim().toLowerCase();
            if (cleanSlug.isBlank()) continue;
            TagModel tag = tagRepository.findBySlug(cleanSlug)
                    .orElseThrow(()-> new ApplicationException("Tag not found!", "TAG_NOT_FOUND", HttpStatus.BAD_REQUEST));
            tags.add(tag);
        }

        // Creating Product
        ProductModel product = new ProductModel();
        product.setSku(request.sku().trim().toUpperCase());
        product.setTitle(request.title().trim());
        product.setSlug(HelperClass.generateSlug(request.title().trim()));
        product.setShortDescription(request.shortDescription().trim());
        product.setDescription(request.description().trim());
        product.setBrand(brand);
        product.setCategory(category);
        product.setBasePrice(request.basePrice());
        product.setSellingPrice(request.sellingPrice());
        product.setStock(request.stock() != null ? request.stock() : 0);
        product.setSizeMl(request.sizeMl());
        product.setActive(request.active());
        product.setTags(tags);


        List<ProductImageModel> imageModels = new ArrayList<>();

        if (imageFiles != null && !imageFiles.isEmpty()) {
            for (int i = 0; i < imageFiles.size(); i++) {
                MultipartFile file = imageFiles.get(i);
                AddProductImageRequest imgReq = request.images().get(i);

                ProductImageModel imageModel = ImageUploadHelper.uploadImage(file, imgReq);
                imageModel.setProduct(product);
                imageModels.add(imageModel);
            }
        }
        product.setImages(imageModels);


        // 6. Save everything (cascade handles images + tags)
        ProductModel savedProduct = productRepository.save(product);


        return new SingleProductResponse(
                savedProduct.getId(),
                savedProduct.getSku(),
                savedProduct.getTitle(),
                savedProduct.getShortDescription(),
                savedProduct.getDescription(),
                new BrandResponse(
                        savedProduct.getBrand().getName(),
                        savedProduct.getBrand().getSlug(),
                        savedProduct.getBrand().getLogoUrl()
                ),
                new CategoryResponse(
                        savedProduct.getCategory().getName(),
                        savedProduct.getCategory().getSlug(),
                        savedProduct.getCategory().getImageUrl()
                ),
                savedProduct.getSellingPrice(),
                savedProduct.getStock(),
                savedProduct.getSizeMl(),
                savedProduct.getTags()
                        .stream()
                        .map(tag-> new TagResponse(tag.getName(), tag.getSlug()))
                        .toList(),
                savedProduct.getImages()
                        .stream()
                        .map(img-> new ProductImageResponse(img.getUrl(), img.getAltText(), img.isThumbnail()))
                        .toList()
                );
    }

    public boolean updatePrice(Long id, Double price) {
        return false;
    }

    public boolean updateQuantity(Long id, int quantity) {
        return false;
    }




}
