package com.oop.absolutecinema.service;

import com.oop.absolutecinema.DTO.ReviewDTO;
import com.oop.absolutecinema.entity.Review;

import java.util.List;

public interface ReviewService {
    Review tambahReview(ReviewDTO.Request reviewDto);
    List<Review> lihatReviewBerdasarkanTayangan(Long tayanganId);
    List<Review> lihatReviewBerdasarkanUser(Long userId);
}
