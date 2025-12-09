package com.ECommerce.repository.product;

import com.ECommerce.model.product.ProductModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductModel, Long> {

    @Query("SELECT p from ProductModel p  JOIN FETCH p.tags WHERE p.id = :productId")
    Optional<ProductModel> findByIdWithTags(@Param("productId") Long productId);


    @Query("""
    SELECT
        b.name, b.slug, b.logoUrl,
        p.id, p.title, p.shortDescription, p.sellingPrice, p.stock,
        (SELECT pi.url FROM ProductImageModel pi
         WHERE pi.product = p AND pi.thumbnail = true
         ORDER BY pi.sortOrder ASC, pi.id ASC LIMIT 1) AS imageUrl
    FROM ProductModel p
    JOIN p.brand b
    WHERE b.slug = :brandSlug
    ORDER BY p.id
    """)
    List<Object[]> findAllByBrandSlug(@Param("brandSlug") String brandSlug);

    List<ProductModel> findByIdNotIn(List<Long> personalizedProductIds);


    @Query("""
    SELECT c.name, c.slug, c.imageUrl,
    p.id, p.title, p.shortDescription, p.sellingPrice, p.stock,
        (SELECT pi.url FROM ProductImageModel pi
         WHERE pi.product = p AND pi.thumbnail = true
         ORDER BY pi.sortOrder ASC, pi.id ASC LIMIT 1) AS productUrl
    FROM ProductModel p
    JOIN p.category c
    WHERE c.slug = :slug
    ORDER BY p.id
    """
    )
    List<Object[]> findAllByCategorySlug(@Param("slug") String categorySlug);




}
