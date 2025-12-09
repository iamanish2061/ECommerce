package com.ECommerce.repository.cartAndOrders;

import com.ECommerce.dto.response.cart.CartResponse;
import com.ECommerce.model.cartandorders.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByUserIdAndProductId(Long userId, Long productId);

    List<CartItem> findByUserId(Long userId);

    @Query("""
    SELECT new com.ECommerce.dto.response.cart.CartResponse(
        new com.ECommerce.dto.response.product.AllProductsResponse(
            p.id,
            p.title,
            p.shortDescription,
            p.sellingPrice,
            p.stock,
            pi.url
        ),
        c.quantity
    )
    FROM CartItem c
    JOIN ProductModel p ON p.id = c.productId
    LEFT JOIN ProductImageModel pi WITH
        pi.product = p
        AND pi.thumbnail = true
        AND (pi.sortOrder = (
            SELECT MIN(pi2.sortOrder)
            FROM ProductImageModel pi2
            WHERE pi2.product = p AND pi2.thumbnail = true
        ))
    WHERE c.userId = :userId
    ORDER BY c.createdAt DESC
    """)
    List<CartResponse> findCartItemsWithProductDetails(@Param("userId") Long userId);

}
