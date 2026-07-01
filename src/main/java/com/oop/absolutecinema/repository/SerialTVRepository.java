package com.oop.absolutecinema.repository;

import com.oop.absolutecinema.entity.SerialTV;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
  public interface SerialTVRepository extends JpaRepository<SerialTV, Long> {

    // Mencari Serial TV berdasarkan jaringan TV (Contoh: "Netflix", "HBO", "Disney+")
    // Field 'jaringanTV' ada di SerialTV.java
    List<SerialTV> findByJaringanTV(String jaringanTV);

    // Mencari Serial TV berdasarkan status tayang (true = masih berjalan / On-Going, false = tamat)
    // Field 'masihBerjalan' ada di SerialTV.java
    List<SerialTV> findByMasihBerjalan(boolean masihBerjalan);

    // Mencari Serial TV berdasarkan tahun rilis
    // Field 'tahunRilis' diwarisi dari Tayangan.java
    List<SerialTV> findByTahunRilis(int tahunRilis);

    // Mencari Serial TV berdasarkan judul, tidak case-sensitive
    // Field 'judul' diwarisi dari Tayangan.java
    List<SerialTV> findByJudulContainingIgnoreCase(String judul);

    // Mencari Serial TV berdasarkan kombinasi jaringanTV DAN masihBerjalan
    List<SerialTV> findByJaringanTVAndMasihBerjalan(String jaringanTV, boolean masihBerjalan);
  }
