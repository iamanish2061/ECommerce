package com.ECommerce.service.admin;

import com.ECommerce.dto.request.admin.AddProductRequest;
import com.ECommerce.dto.response.admin.SingleProductResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public class ProductService {



    public SingleProductResponse addNewProduct(@Valid AddProductRequest addProductRequest) {
    }

    public boolean updatePrice(Long id, Double price) {
    }

    public boolean updateQuantity(Long id, int quantity) {
    }


}
