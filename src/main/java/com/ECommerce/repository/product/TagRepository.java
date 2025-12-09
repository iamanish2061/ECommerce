package com.ECommerce.repository.product;

import com.ECommerce.model.product.TagModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TagRepository extends JpaRepository<TagModel, Long> {
    Optional<TagModel> findBySlug(String slug);

    Set<String> findSlugsBySlugIn(List<String> incomingSlugs);

    Optional<TagModel> findByName(String name);

    @Query("SELECT t FROM TagModel t JOIN FETCH t.products WHERE t.slug = :slug")
    Optional<TagModel> findBySlugWithProduct(@Param("slug") String slugName);


}
