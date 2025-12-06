package com.ECommerce.service.products;

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

        Set<String> similarUserKeys = redisTemplate.keys("user_vector:*");
        List<UserSimilarity> similarities = new ArrayList<>();

        for(String key : similarUserKeys){
            if(key.equals(vectorKey)) continue;
            Long otherUserId = Long.parseLong(key.split(":")[1]);
            Double score = cosineSimilarity(userId, otherUserId);
            if(score>0.3){
                similarities.add(new UserSimilarity(otherUserId, score));
            }
        }

        similarities.sort((a,b)->Double.compare(b.score, a.score));
        List<Long> similarUserIds = similarities.stream()
                .limit(15)
                .map(u->u.userId)
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

    private Double cosineSimilarity(Long user1, Long user2) {
        String key1 = "user_vector:" + user1;
        String key2 = "user_vector:" + user2;

        Map<Object, Object> vec1 = redisTemplate.opsForHash().entries(key1);
        Map<Object, Object> vec2 = redisTemplate.opsForHash().entries(key2);

        double dot = 0.0, norm1 = 0.0, norm2 = 0.0;
        Set<String> allPids = new HashSet<>();
        vec1.keySet().forEach(k-> allPids.add(k.toString()));
        vec2.keySet().forEach(k-> allPids.add(k.toString()));

        for (String pid: allPids) {
            double score1 = Double.parseDouble(vec1.getOrDefault(pid, "0").toString());
            double score2 = Double.parseDouble(vec2.getOrDefault(pid, "0").toString());

            dot += score1 * score2;
            norm1 += score1 * score1;
            norm2 += score2 * score2;
        }

        return (norm1 == 0 || norm2 == 0) ? 0.0 : dot / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    record UserSimilarity(Long userId, Double score){}

}
