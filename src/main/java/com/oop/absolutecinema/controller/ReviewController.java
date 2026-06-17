package com.oop.absolutecinema.controller;

import com.oop.absolutecinema.DTO.ReviewDTO;
import com.oop.absolutecinema.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public ResponseEntity<?> tambahReview(@Valid @RequestBody ReviewDTO.Request request) {
        try {

            Object reviewBaru = reviewService.tambahReview(request);

            return new ResponseEntity<>(reviewBaru, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            return new ResponseEntity<>("Terjadi kesalahan pada server saat menyimpan review.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}