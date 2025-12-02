package com.ECommerce.repository;

import com.ECommerce.model.product.BrandModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface BrandRepository extends JpaRepository<BrandModel, Long> {

    Optional<BrandModel> findByName(String name);

}
