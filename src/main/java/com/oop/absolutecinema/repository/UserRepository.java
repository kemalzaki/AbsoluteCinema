package com.oop.absolutecinema.repository;

import com.oop.absolutecinema.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
  public interface UserRepository extends JpaRepository<User, Long> {

    // Mencari user berdasarkan username
    // untuk mencocokkan username yang diinput dengan yang ada di database saat proses LOGIN
    Optional<User> findByUsername(String username);

    // Mencari semua user berdasarkan role tertentu
    // Field 'role' di User.java hanya boleh bernilai "ADMIN" atau "MEMBER"
    List<User> findByRole(String role);

    // Mengecek apakah usernamse sudah terdaftar di database
    // untuk mencegah username duplikat saat proses REGISTRASI
    boolean existsByUsername(String username);
  }
