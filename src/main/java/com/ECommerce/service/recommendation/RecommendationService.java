package com.ECommerce.service.recommendation;

import com.ECommerce.dto.response.product.AllProductsResponse;
import com.ECommerce.model.product.ProductImageModel;
import com.ECommerce.model.product.ProductModel;
import com.ECommerce.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RecommendationService  {

    private final ProductRepository productRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private static final int RECOMMENDATION_COUNT =15;

    public List<AllProductsResponse> getPersonalizedRecommendation(Long userId){
        List<ProductModel> recommendationProducts;
        String vectorKey = "user_vector:" + userId;

        Long size = redisTemplate.opsForHash().size(vectorKey);
        if(size == null || size == 0){
            return new ArrayList<>();
        }

        Set<Object> userProductIds = redisTemplate.opsForHash().keys(vectorKey);

        Set<String> similarUserIdStrings = redisTemplate.opsForZSet()
                .reverseRange("user_similar:"+userId, 0,29);

        List<Long> similarUserIds = similarUserIdStrings ==null ?
                new ArrayList<>(): similarUserIdStrings.stream()
                .map(Long::parseLong)
                .toList();

        // Aggregate products from similar users (exclude already interacted)
        Map<String, Double> productScores = new HashMap<>();
        for (Long simUserId : similarUserIds) {
            Map<Object, Object> map = redisTemplate.opsForHash().entries("user_vector:" + simUserId);
            map.forEach((pid, score) -> {
                String productIdStr = pid.toString();
                if (userProductIds.contains(productIdStr)) return; // skip already seen
                productScores.merge(productIdStr, Double.valueOf(score.toString()), Double::sum);
            });
        }

        List<Long> recommendedIds = productScores.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(RECOMMENDATION_COUNT)
                .map(e -> Long.parseLong(e.getKey()))
                .toList();

        recommendationProducts = productRepository.findAllById(recommendedIds);
        return recommendationProducts.stream()
                .sorted(Comparator.comparingLong(p->recommendedIds.indexOf(p.getId())))
                .map(r-> new AllProductsResponse(
                        r.getId(),
                        r.getTitle(),
                        r.getShortDescription(),
                        r.getSellingPrice(),
                        r.getStock(),
                        r.getImages().stream()
                                .filter(ProductImageModel::isThumbnail)
                                .map(ProductImageModel::getUrl)
                                .findFirst().orElse(null)
                ))
                .toList();
    }

    record UserSimilarity(Long userId, Double score){}

}
