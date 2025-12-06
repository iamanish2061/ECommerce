package com.ECommerce.service.products;

import com.ECommerce.dto.response.cart.CartResponse;
import com.ECommerce.dto.response.product.AllProductsResponse;
import com.ECommerce.exception.ApplicationException;
import com.ECommerce.model.ActivityType;
import com.ECommerce.model.cartandorders.CartItem;
import com.ECommerce.model.product.ProductImageModel;
import com.ECommerce.model.product.ProductModel;
import com.ECommerce.redis.RedisService;
import com.ECommerce.repository.cartAndOrders.CartRepository;
import com.ECommerce.repository.product.ProductRepository;
import com.ECommerce.service.recommendation.SimilarUserUpdater;
import com.ECommerce.service.recommendation.UserActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserActivityService userActivityService;
    private final RedisService redisService;
    private final SimilarUserUpdater similarUserUpdater;

    @Transactional
    public String addToCart(Long userId, Long productId, int quantity){
        ProductModel product = productRepository.findById(productId)
                .orElseThrow(()-> new ApplicationException("Product not found!", "PRODUCT_NOT_FOUND", HttpStatus.BAD_REQUEST));

        if(product.getStock() < quantity)
            throw new ApplicationException("Not enough stock", "NOT_ENOUGH_STOCK", HttpStatus.BAD_REQUEST);

        CartItem cartItem = cartRepository.findByUserIdAndProductId(userId, productId)
                .orElseGet(()-> CartItem.builder()
                        .userId(userId)
                        .productId(productId)
                        .quantity(0)
                        .build()
                );

        cartItem.setQuantity(cartItem.getQuantity() + quantity);
        cartRepository.save(cartItem);

        userActivityService.recordActivity(userId, productId, ActivityType.CART_ADD, 5);

        redisService.incrementUserVector(userId, productId, 5);
        similarUserUpdater.updateSimilarUsersAsync(userId);

        return "Added to cart! Quantity: "+ cartItem.getQuantity();
    }

    @Transactional
    public int getCartCount(Long userId){
        List<CartItem> cartItems = cartRepository.findByUserId(userId);

        return cartItems.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    @Transactional
    public List<CartResponse> getCartItems(Long userId) {
        List<CartItem> cartItems = cartRepository.findByUserId(userId);
        return cartItems.stream()
                .map(item-> {
                    ProductModel product = productRepository.findById(item.getProductId())
                            .orElseThrow(()-> new ApplicationException("Product not found!", "PRODUCT_NOT_FOUND", HttpStatus.BAD_REQUEST));
                    AllProductsResponse productsResponse = new AllProductsResponse(
                            product.getId(),
                            product.getTitle(),
                            product.getDescription(),
                            product.getSellingPrice(),
                            product.getStock(),
                            product.getImages().stream()
                                    .filter(ProductImageModel::isThumbnail)
                                    .map(ProductImageModel::getUrl)
                                    .findFirst().orElse(null)
                    );
                    return new CartResponse(productsResponse, item.getQuantity());
                }).toList();
    }

    @Transactional
    public String updateCart(Long id, Long productId, int newQuantity) {
        if(newQuantity<0)
            throw new ApplicationException("Invalid quantity!", "INVALID_QUANTITY", HttpStatus.BAD_REQUEST);

        CartItem cartItem = cartRepository.findByUserIdAndProductId(id, productId)
                .orElseThrow(()->new ApplicationException("Invalid request!", "CART_ITEM_NOT_FOUND", HttpStatus.BAD_REQUEST));

        if(newQuantity == 0){
            deleteFromCart(id, productId);
            return "Removed from cart!";
        }

        ProductModel product = productRepository.findById(productId)
                .orElseThrow(()-> new ApplicationException("Product not found!", "PRODUCT_NOT_FOUND", HttpStatus.BAD_REQUEST));
        if(product.getStock() < newQuantity){
            throw new ApplicationException("Not enough stock", "NOT_ENOUGH_STOCK", HttpStatus.BAD_REQUEST);
        }

        cartItem.setQuantity(newQuantity);
        cartRepository.save(cartItem);

        redisService.incrementUserVector(id, productId, 2);
        similarUserUpdater.updateSimilarUsersAsync(id);

        return "Cart updated successfully! Quantity: "+ newQuantity;
    }

    @Transactional
    public String deleteFromCart(Long userId, Long productId) {
        CartItem cartItem = cartRepository
                .findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new ApplicationException("Item not in cart", "INVALID_ACTION", HttpStatus.BAD_REQUEST));

        cartRepository.delete(cartItem);

        redisService.incrementUserVector(userId, productId, -5);
        similarUserUpdater.updateSimilarUsersAsync(userId);

        return "Item removed form cart!";
    }


}
