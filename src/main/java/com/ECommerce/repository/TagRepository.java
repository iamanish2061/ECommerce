package com.ECommerce.repository;

import com.ECommerce.model.product.TagModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<TagModel, Long> {
    Optional<TagModel> findBySlug(String slug);

}
