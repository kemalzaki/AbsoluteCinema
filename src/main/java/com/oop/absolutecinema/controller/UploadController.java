package com.oop.absolutecinema.controller;

import com.oop.absolutecinema.service.FileStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private final FileStorageService fileStorageService;

    public UploadController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/gambar")
    public ResponseEntity<String> uploadGambar(@RequestParam("file") MultipartFile file) {
        try {
            String namaFile = fileStorageService.simpanFile(file);
            
            return ResponseEntity.ok(namaFile); 
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Gagal upload gambar: " + e.getMessage());
        }
    }
}