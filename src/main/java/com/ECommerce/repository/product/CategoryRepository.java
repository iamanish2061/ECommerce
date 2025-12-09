package com.ECommerce.repository.product;

import com.ECommerce.model.product.CategoryModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryModel, Long> {

    Optional<CategoryModel> findBySlug(String slug);

    Optional<CategoryModel> findByName(String parentName);
}
