package com.oop.absolutecinema.repository;

import com.oop.absolutecinema.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
  public interface ReviewRepository extends JpaRepository<Review, String> {

    // Mencari semua review untuk tayangan tertentu
    // untuk menampilkan daftar ulasan di halaman detail tayangan
    List<Review> findByTayanganId(Long tayanganId);

    // Mencari semua review yang ditulis oleh  user tertentu
    // untuk menampilkan riwayat ulasan seorang user
    List<Review> findByUserId(String userId);

    // Mengecek apakah user sudah pernah mereview tayangan tertentu
    // Untuk mencegah duplikasi ulasan dari user yang sama
    boolean existsByUserIdAndTayanganId(String userId, Long tayanganId);

    // Mengambil satu review spesifik dari user tertentu pada tayangan tertentu
    // untuk diedit jika user ingin mengedit ulasannya
    Optional<Review> findByUserIdAndTayanganId(String userId, Long tayanganId);
  }
