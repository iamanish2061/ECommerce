package com.ECommerce.repository.product;

import com.ECommerce.dto.response.product.AllProductsResponse;
import com.ECommerce.model.product.ProductModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductModel, Long> {

    //tags related
    @Query("SELECT p from ProductModel p JOIN FETCH p.tags WHERE p.id = :productId")
    Optional<ProductModel> findByIdWithTags(@Param("productId") Long productId);

    @Query("""
    SELECT
        t.name, t.slug,
        p.id, p.title, p.shortDescription, p.sellingPrice, p.stock,
        pi.url AS imageUrl
    FROM ProductModel p
    JOIN p.tags t
    LEFT JOIN ProductImageModel pi ON pi.product = p AND pi.thumbnail = true
    WHERE t.slug = :tagSlug
      AND (pi.id IS NULL OR pi.sortOrder = (
          SELECT MIN(pi_sub.sortOrder)
          FROM ProductImageModel pi_sub
          WHERE pi_sub.product = p AND pi_sub.thumbnail = true
      ))
    ORDER BY p.id
    """)
    List<Object[]> findAllByTagSlug(@Param("tagSlug") String tagSlug);

    //brands related
    @Query("""
    SELECT
        b.name, b.slug, b.logoUrl,
        p.id, p.title, p.shortDescription, p.sellingPrice, p.stock,
        pi.url AS imageUrl
    FROM ProductModel p
    JOIN p.brand b
    LEFT JOIN ProductImageModel pi ON pi.product = p AND pi.thumbnail = true
    WHERE b.slug = :brandSlug
      AND (pi.id IS NULL OR pi.sortOrder = (
          SELECT MIN(pi_sub.sortOrder)
          FROM ProductImageModel pi_sub
          WHERE pi_sub.product = p AND pi_sub.thumbnail = true
      ))
    ORDER BY p.id
    """)
    List<Object[]> findAllByBrandSlug(@Param("brandSlug") String brandSlug);

    //category related
    @Query("""
    SELECT
        c.name, c.slug, c.imageUrl,
        p.id, p.title, p.shortDescription, p.sellingPrice, p.stock,
        pi.url AS imageUrl
    FROM ProductModel p
    JOIN p.category c
    LEFT JOIN ProductImageModel pi ON pi.product = p AND pi.thumbnail = true
    WHERE c.slug = :slug
      AND (pi.id IS NULL OR pi.sortOrder = (
          SELECT MIN(pi_sub.sortOrder)
          FROM ProductImageModel pi_sub
          WHERE pi_sub.product = p AND pi_sub.thumbnail = true
      ))
    ORDER BY p.id
    """
    )
    List<Object[]> findAllByCategorySlug(@Param("slug") String categorySlug);


    //getting products
    @Query("""
    SELECT
        p.id, p.title, p.shortDescription, p.sellingPrice, p.stock,
        pi.url AS imageUrl
        FROM ProductModel p
        LEFT JOIN ProductImageModel pi ON pi.product = p AND pi.thumbnail = true
      WHERE (pi.id IS NULL OR pi.sortOrder = (
          SELECT MIN(pi_sub.sortOrder)
          FROM ProductImageModel pi_sub
          WHERE pi_sub.product = p AND pi_sub.thumbnail = true
      ))
      ORDER BY p.id
    """)
    List<AllProductsResponse> findAllWithImage();

    @Query("""
    SELECT
        p.id, p.title, p.shortDescription, p.sellingPrice, p.stock,
        pi.url AS imageUrl
        FROM ProductModel p
        LEFT JOIN ProductImageModel pi ON pi.product = p AND pi.thumbnail = true
      WHERE p.id IN (:recommendedIds)
        AND (pi.id IS NULL OR pi.sortOrder = (
          SELECT MIN(pi_sub.sortOrder)
          FROM ProductImageModel pi_sub
          WHERE pi_sub.product = p AND pi_sub.thumbnail = true
      ))
      ORDER BY p.id
    """)
    List<AllProductsResponse> findAllByIdWithImage(@Param("recommendedIds") List<Long> recommendedIds);

    @Query("""
    SELECT
        p.id, p.title, p.shortDescription, p.sellingPrice, p.stock,
        pi.url AS imageUrl
        FROM ProductModel p
        LEFT JOIN ProductImageModel pi ON pi.product = p AND pi.thumbnail = true
      WHERE p.id NOT IN (:personalizedIds)
        AND (pi.id IS NULL OR pi.sortOrder = (
          SELECT MIN(pi_sub.sortOrder)
          FROM ProductImageModel pi_sub
          WHERE pi_sub.product = p AND pi_sub.thumbnail = true
      ))
      ORDER BY p.id
    """)
    List<AllProductsResponse> findAllWithImageNotIn(@Param("personalizedIds") List<Long> personalizedProductIds);

    @Query("""
    SELECT p FROM ProductModel p
    JOIN FETCH p.brand
    JOIN FETCH p.category
    LEFT JOIN FETCH p.images
    LEFT JOIN FETCH p.tags
    WHERE p.id = :productId
    """)
    Optional<ProductModel> findByIdWithAllDetails(@Param("productId") Long id);
}
