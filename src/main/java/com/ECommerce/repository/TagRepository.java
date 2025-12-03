package com.ECommerce.repository;

import com.ECommerce.model.product.TagModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TagRepository extends JpaRepository<TagModel, Long> {
    Optional<TagModel> findBySlug(String slug);

    Set<String> findSlugsBySlugsIn(List<String> incomingSlugs);

    Optional<TagModel> findByName(String name);
}
