package com.ECommerce.service.user;

import com.ECommerce.dto.response.product.AllProductsResponse;
import com.ECommerce.dto.response.product.SingleProductResponse;
import com.ECommerce.dto.response.product.TagResponse;
import com.ECommerce.model.product.TagModel;
import com.ECommerce.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final TagRepository tagRepository;

    public List<TagResponse> getAllTags(){
        List<TagModel> tagModels = tagRepository.findAll();
        return tagModels.stream()
                .map(tag-> new TagResponse(tag.getName(), tag.getSlug()))
                .toList();
    }


    public List<AllProductsResponse> getAllProducts() {
        return null;
    }


    public SingleProductResponse getDetailOfProduct(Long id) {
        return null;
    }
}
