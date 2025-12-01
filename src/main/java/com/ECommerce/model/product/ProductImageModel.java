package com.ECommerce.model.product;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_images", indexes = {
@Index(name = "idx_product_images_product_id", columnList = "product_id")
})
public class ProductImageModel {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnoreProperties("images")
    private ProductModel product;

    @Column(nullable = false, length = 500)
    private String url;

    private String altText;

    private int sortOrder = 0;

    private boolean thumbnail = false;
}
