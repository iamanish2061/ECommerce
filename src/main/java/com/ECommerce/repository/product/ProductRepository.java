package com.ECommerce.repository.product;

import com.ECommerce.dto.projection.BrandWithProductProjection;
import com.ECommerce.model.product.ProductModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<ProductModel, Long> {

    @Query("""
    SELECT
        b.name, b.slug, b.logoUrl,
        p.id, p.title, p.shortDescription, p.sellingPrice, p.stock,
        (SELECT pi.url FROM ProductImageModel pi
         WHERE pi.product = p AND pi.thumbnail = true
         ORDER BY pi.sortOrder ASC, pi.id ASC LIMIT 1) AS imageUrl
    FROM ProductModel p
    JOIN p.brand b
    WHERE b.slug = :brandSlug AND p.active = true
    ORDER BY p.id
    """)
    List<Object[]> findAllByBrandSlug(@Param("brandSlug") String brandSlug);



    List<ProductModel> findByIdNotIn(List<Long> personalizedProductIds);
}
