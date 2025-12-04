package com.ECommerce.repository;

import com.ECommerce.model.product.ProductImageModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImageModel, Long> {

}
