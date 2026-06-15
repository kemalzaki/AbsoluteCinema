package com.oop.absolutecinema.service;

import com.oop.absolutecinema.entity.Review;

import java.util.List;

public interface ReviewService {
    Review tambahReview(String userId, Long tayanganId, int skor, String teks);
    List<Review> lihatReviewBerdasarkanTayangan(Long tayanganId);
    List<Review> lihatReviewBerdasarkanUser(String userId);
}
