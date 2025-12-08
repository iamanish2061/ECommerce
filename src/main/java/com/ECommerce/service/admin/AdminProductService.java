package com.ECommerce.service.admin;

import com.ECommerce.dto.request.product.*;
import com.ECommerce.dto.response.product.*;
import com.ECommerce.exception.ApplicationException;
import com.ECommerce.model.product.*;
import com.ECommerce.repository.product.*;
import com.ECommerce.utils.HelperClass;
import com.ECommerce.utils.ImageUploadHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
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
    private final ProductImageRepository productImageRepository;

    // adding tags
    @Transactional
    public void addTags(AddTagRequest request) throws ApplicationException {
        List<String> incomingNames = request.names().stream()
                .map(String::trim)
                .toList();

        List<String> incomingSlugs = incomingNames.stream()
                .map(HelperClass::generateSlug)
                .toList();

        Set<String> existingSlugs = tagRepository.findSlugsBySlugIn(incomingSlugs);

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
    }

    @Transactional
    public void deleteTag(String name){
        TagModel tag = tagRepository.findByName(name.trim()).orElseThrow(()->
                new ApplicationException("Tag not found !", "INVALID_TAG", HttpStatus.BAD_REQUEST));

        for(ProductModel product : tag.getProducts()){
            product.getTags().remove(tag);
            productRepository.save(product);
        }

        tagRepository.delete(tag);
    }

    @Transactional
    public void addTagToProduct(String name, Long productId) throws ApplicationException{
        ProductModel productModel = productRepository.findById(productId)
                .orElseThrow(()->
                        new ApplicationException("Product not found!", "PRODUCT_NOT_FOUND", HttpStatus.BAD_REQUEST));

        TagModel tagModel = tagRepository.findByName(name.trim())
                .orElseThrow(()->
                        new ApplicationException("Tag not found!", "TAG_NOT_FOUND", HttpStatus.BAD_REQUEST));

        if(productModel.getTags().add(tagModel)){
            productRepository.save(productModel);
        }
    }

    @Transactional
    public void removeTagFromProduct(String name, Long productId) throws ApplicationException{
        ProductModel productModel = productRepository.findById(productId)
                .orElseThrow(()->
                        new ApplicationException("Product not found!", "PRODUCT_NOT_FOUND", HttpStatus.BAD_REQUEST));

        TagModel tagModel = tagRepository.findByName(name.trim())
                .orElseThrow(()->
                        new ApplicationException("Tag not found!", "TAG_NOT_FOUND", HttpStatus.BAD_REQUEST));

        if(productModel.getTags().remove(tagModel)){
            productRepository.save(productModel);
        }
    }


    @Transactional
    public void addBrand(BrandRequest addBrandRequest, MultipartFile logo) {
        BrandModel brandModel = new BrandModel();
        brandModel.setName(addBrandRequest.name().trim());
        brandModel.setSlug(HelperClass.generateSlug(addBrandRequest.name().trim()));
        String url = ImageUploadHelper.uploadImage(logo, addBrandRequest.name().trim());
        brandModel.setLogoUrl(url);

        brandRepository.save(brandModel);
    }

    @Transactional
    public void addCategory(AddCategoryRequest addCategoryRequest, MultipartFile image) {
        CategoryModel categoryParent = categoryRepository.findByName(addCategoryRequest.parentName().trim())
                .orElse(null);

        CategoryModel categoryModel = new CategoryModel();
        categoryModel.setName(addCategoryRequest.name());
        categoryModel.setSlug(HelperClass.generateSlug(addCategoryRequest.name().trim()));
        categoryModel.setParent(categoryParent);
        String url =ImageUploadHelper.uploadImage(image, addCategoryRequest.name().trim());
        categoryModel.setImageUrl(url);

        categoryRepository.save(categoryModel);
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

    @Transactional
    public void addImage(Long productId, AddProductImageRequest addProductImageRequest, MultipartFile image) {

        ProductModel productModel = productRepository.findById(productId)
                .orElseThrow(()->
                        new ApplicationException("Product not found!", "PRODUCT_NOT_FOUND", HttpStatus.BAD_REQUEST));

        ProductImageModel productImageModel= ImageUploadHelper.uploadImage(image, addProductImageRequest);
        productImageModel.setProduct(productModel);

        productImageRepository.save(productImageModel);
    }

    public AdminSingleProductResponse getAdminDetailOfProduct(Long id) {
        ProductModel product = productRepository.findById(id).orElseThrow(
                ()-> new ApplicationException("Product not found!", "PRODUCT_NOT_FOUND", HttpStatus.BAD_REQUEST));

                SingleProductResponse s = new SingleProductResponse(
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
        return new AdminSingleProductResponse(s, product.getBasePrice());
    }

    @Transactional
    public void updatePrice(Long productId, BigDecimal price) {
        ProductModel productModel = productRepository.findById(productId)
                .orElseThrow(()->
                        new ApplicationException("Product not found!", "PRODUCT_NOT_FOUND", HttpStatus.BAD_REQUEST));
        productModel.setSellingPrice(price);
        productRepository.save(productModel);
    }

    @Transactional
    public void updateQuantity(Long productId, int quantity) {
        ProductModel productModel = productRepository.findById(productId)
                .orElseThrow(()->
                        new ApplicationException("Product not found!", "PRODUCT_NOT_FOUND", HttpStatus.BAD_REQUEST));

        Integer newStock = productModel.getStock()+quantity;
        productModel.setStock(newStock);
        productRepository.save(productModel);
    }


}
