package com.goldenflame.pg102.repository;

import com.goldenflame.pg102.model.CatalogueItem;
import com.goldenflame.pg102.model.Category; // Import the new Category class
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CatalogueItemRepository extends JpaRepository<CatalogueItem, Long> {
    List<CatalogueItem> findByCategory(Category category);
    List<CatalogueItem> findByCategory_Name(String categoryName);

}