package com.goldenflame.pg102.repository;

import com.goldenflame.pg102.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    // Find all reviews for a specific catalogue item, ordered by date
    List<Review> findByCatalogueItemIdOrderByReviewDateDesc(Long catalogueItemId);

    // Find all reviews, ordered by the most recent first, for the manager's dashboard
    List<Review> findAllByOrderByReviewDateDesc();
}