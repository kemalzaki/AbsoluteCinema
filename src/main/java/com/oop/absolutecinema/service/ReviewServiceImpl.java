package com.oop.absolutecinema.service;

import com.oop.absolutecinema.entity.Film;
import com.oop.absolutecinema.entity.Review;
import com.oop.absolutecinema.entity.SerialTV;
import com.oop.absolutecinema.entity.Tayangan;
import com.oop.absolutecinema.entity.User;
import com.oop.absolutecinema.exception.DataTidakDitemukanException;
import com.oop.absolutecinema.repository.FilmRepository;
import com.oop.absolutecinema.repository.ReviewRepository;
import com.oop.absolutecinema.repository.SerialTVRepository;
import com.oop.absolutecinema.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final FilmRepository filmRepository;
    private final SerialTVRepository serialTvRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository,
                             UserRepository userRepository,
                             FilmRepository filmRepository,
                             SerialTVRepository serialTvRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.filmRepository = filmRepository;
        this.serialTvRepository = serialTvRepository;
    }

    @Override
    public Review tambahReview(String userId, Long tayanganId, int skor, String teks) {
        // 1. Pencegahan review ganda
        // Menggunakan method dari repository untuk mengecek apakah user sudah pernah mereview
        if (reviewRepository.existsByUserIdAndTayanganId(userId, tayanganId)) {
            throw new IllegalArgumentException("User sudah memberikan review untuk tayangan ini. Tidak diizinkan review ganda.");
        }

        // 2. Ambil entitas User dari database
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataTidakDitemukanException("User dengan ID " + userId + " tidak ditemukan!"));

        // 3. Ambil entitas Tayangan dari database
        // Karena Tayangan adalah MappedSuperclass dan tidak memiliki repository mandiri,
        // kita perlu mencari di FilmRepository dan SerialTVRepository.
        Tayangan tayangan = null;
        Optional<Film> filmOpt = filmRepository.findById(tayanganId);
        if (filmOpt.isPresent()) {
            tayangan = filmOpt.get();
        } else {
            Optional<SerialTV> serialOpt = serialTvRepository.findById(tayanganId);
            if (serialOpt.isPresent()) {
                tayangan = serialOpt.get();
            } else {
                throw new DataTidakDitemukanException("Tayangan dengan ID " + tayanganId + " tidak ditemukan!");
            }
        }

        // 4. Update rating otomatis 
        // Memanfaatkan encapsulation dari OOP di mana rating dikalkulasi di dalam object Tayangan
        tayangan.tambahReview(skor);

        // Simpan perubahan rating di class parent (Tayangan) ke tabel spesifik (Film atau SerialTV)
        if (tayangan instanceof Film) {
            filmRepository.save((Film) tayangan);
        } else if (tayangan instanceof SerialTV) {
            serialTvRepository.save((SerialTV) tayangan);
        }

        // 5. Simpan Review ke database
        Review review = new Review(skor, teks, user, tayangan);
        return reviewRepository.save(review);
    }

    @Override
    public List<Review> lihatReviewBerdasarkanTayangan(Long tayanganId) {
        return reviewRepository.findByTayanganId(tayanganId);
    }

    @Override
    public List<Review> lihatReviewBerdasarkanUser(String userId) {
        return reviewRepository.findByUserId(userId);
    }
}
