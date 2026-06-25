package com.oop.absolutecinema.controller;

import com.oop.absolutecinema.service.ImageKitService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Upload gambar ke ImageKit (CDN eksternal) dan kembalikan hosted URL.
 *
 * Dipakai halaman admin (admin.html) untuk mengisi otomatis field gambarUrl.
 * Route /api/upload/** dilindungi hasRole("ADMIN") oleh SecurityConfig.
 */
@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private final ImageKitService imageKitService;

    public UploadController(ImageKitService imageKitService) {
        this.imageKitService = imageKitService;
    }

    @PostMapping("/gambar")
    public ResponseEntity<String> uploadGambar(@RequestParam("file") MultipartFile file) {
        try {
            String url = imageKitService.upload(file);
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Gagal upload gambar: " + e.getMessage());
        }
    }
}
