package com.oop.absolutecinema.controller;

import com.oop.absolutecinema.DTO.UserDTO;
import com.oop.absolutecinema.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserDTO.RegisterRequest dto) {
        try {
            // Memanggil method register yang sudah menerima RegisterRequest
            return ResponseEntity.ok(authService.register(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserDTO.LoginRequest dto) {
        try {
            // Memanggil method login yang menerima LoginRequest
            return ResponseEntity.ok(authService.login(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // POST /api/auth/verifikasi-otp
@PostMapping("/verifikasi-otp")
public ResponseEntity<?> verifikasiOtp(
        @RequestParam String email,
        @RequestParam String otp) {
    try {
        return ResponseEntity.ok(authService.verifikasiOtp(email, otp));
    } catch (RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}

// POST /api/auth/lupa-password
@PostMapping("/lupa-password")
public ResponseEntity<?> lupaPassword(@RequestParam String email) {
    try {
        return ResponseEntity.ok(authService.lupaPassword(email));
    } catch (RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}

// POST /api/auth/reset-password
@PostMapping("/reset-password")
public ResponseEntity<?> resetPassword(
        @RequestParam String email,
        @RequestParam String otp,
        @RequestParam String passwordBaru) {
    try {
        return ResponseEntity.ok(authService.resetPassword(email, otp, passwordBaru));
    } catch (RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
}