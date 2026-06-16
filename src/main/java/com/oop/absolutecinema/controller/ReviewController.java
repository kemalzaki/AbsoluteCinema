package com.oop.absolutecinema.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/review")
public class ReviewController {

    @GetMapping("/tayangan/{tayanganId}")
    public String tampilkanReviewTayangan(
            @PathVariable Long tayanganId) {

        return "Menampilkan review untuk tayangan " + tayanganId;
    }

    @PostMapping
    public String tambahReview() {

        return "Review berhasil ditambahkan";
    }

    @DeleteMapping("/{id}")
    public String hapusReview(
            @PathVariable String id) {

        return "Review berhasil dihapus";
    }
}
