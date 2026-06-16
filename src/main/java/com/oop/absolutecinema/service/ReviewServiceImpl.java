package com.oop.absolutecinema.service;

import com.oop.absolutecinema.DTO.ReviewDTO;
import com.oop.absolutecinema.entity.Review;
import com.oop.absolutecinema.entity.Tayangan;
import com.oop.absolutecinema.entity.User;
import com.oop.absolutecinema.exception.DataTidakDitemukanException;
import com.oop.absolutecinema.repository.ReviewRepository;
import com.oop.absolutecinema.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final TayanganService tayanganService;

    public ReviewServiceImpl(ReviewRepository reviewRepository,
                             UserRepository userRepository,
                             TayanganService tayanganService) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.tayanganService = tayanganService;
    }

    @Override
    public Review tambahReview(ReviewDTO.Request reviewDto) {
        // Ekstraksi data dari DTO sesuai kebutuhan
        Long tayanganId = reviewDto.getTayanganId();
        Long userId = reviewDto.getUserId();
        int skor = reviewDto.getSkor();
        String teks = reviewDto.getTeks();

        // 1. Pencegahan review ganda
        // Menggunakan method dari repository untuk mengecek apakah user sudah pernah mereview
        if (reviewRepository.existsByUserIdAndTayanganId(userId, tayanganId)) {
            throw new IllegalArgumentException("User sudah memberikan review untuk tayangan ini. Tidak diizinkan review ganda.");
        }

        // 2. Ambil entitas User dari database
        if (userId == null) {
            throw new DataTidakDitemukanException("User ID tidak boleh null!");
        }

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new DataTidakDitemukanException("User dengan ID " + userId + " tidak ditemukan!"));

        // 3. Ambil entitas Tayangan menggunakan TayanganService (sesuai Class Diagram)
        Tayangan tayangan = tayanganService.lihatTayanganBerdasarkanId(tayanganId);
        if (tayangan == null) {
            throw new DataTidakDitemukanException("Tayangan dengan ID " + tayanganId + " tidak ditemukan!");
        }

        // 4. Update rating otomatis 
        // Memanfaatkan encapsulation dari OOP di mana rating dikalkulasi di dalam object Tayangan
        tayangan.tambahReview(skor);

        // Delegasikan penyimpanan tayangan kembali ke TayanganService
        tayanganService.perbaruiDataTayangan(tayangan);



        // 5. Simpan Review ke database
        Review review = new Review(skor, teks, user, tayangan);
        return reviewRepository.save(review);
    }

    @Override
    public List<Review> lihatReviewBerdasarkanTayangan(Long tayanganId) {
        return reviewRepository.findByTayanganId(tayanganId);
    }

    @Override
    public List<Review> lihatReviewBerdasarkanUser(Long userId) {
        return reviewRepository.findByUserId(userId);
    }
}
