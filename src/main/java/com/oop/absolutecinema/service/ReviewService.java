package com.oop.absolutecinema.service;

import com.oop.absolutecinema.dto.ReviewDTO;
import com.oop.absolutecinema.entity.Review;

import java.util.List;

public interface ReviewService {
    Review tambahReview(ReviewDTO reviewDto);
    List<Review> lihatReviewBerdasarkanTayangan(Long tayanganId);
    List<Review> lihatReviewBerdasarkanUser(String userId);
}
