package com.goldenflame.pg102.repository;

import com.goldenflame.pg102.model.CatalogueItem;
import com.goldenflame.pg102.model.CatalogueItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CatalogueItemRepository extends JpaRepository<CatalogueItem, Long> {
    // Spring Data JPA automatically creates the query for this method based on its name
    List<CatalogueItem> findByItemType(CatalogueItemType itemType);
}