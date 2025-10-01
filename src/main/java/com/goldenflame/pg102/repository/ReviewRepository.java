package com.goldenflame.pg102.repository;

import com.goldenflame.pg102.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByCatalogueItemIdOrderByReviewDateDesc(Long catalogueItemId);

    List<Review> findAllByOrderByReviewDateDesc();
}