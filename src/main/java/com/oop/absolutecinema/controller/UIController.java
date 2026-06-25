package com.oop.absolutecinema.controller;

import com.oop.absolutecinema.DTO.UserDTO;
import com.oop.absolutecinema.entity.Film;
import com.oop.absolutecinema.entity.SerialTV;
import com.oop.absolutecinema.entity.Tayangan;
import com.oop.absolutecinema.service.AuthService;
import com.oop.absolutecinema.service.CustomUserDetails;
import com.oop.absolutecinema.service.ReviewService;
import com.oop.absolutecinema.service.TayanganService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class UIController {

    @Autowired
    private TayanganService tayanganService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private AuthService authService;

    @GetMapping("/")
    public String index(Model model) {
        // Featured movies on home — ambil maksimal 4 tayangan
        List<Tayangan> featured = tayanganService.lihatSemuaTayangan().stream()
                .limit(4)
                .collect(Collectors.toList());
        model.addAttribute("tayangans", featured);
        return "index";
    }

    @GetMapping("/katalog")
    public String katalog(@RequestParam(required = false) String type, Model model) {
        List<Tayangan> semua = tayanganService.lihatSemuaTayangan();
        List<Tayangan> filtered;

        String normalized = (type == null || type.isBlank()) ? "ALL" : type.toUpperCase();

        switch (normalized) {
            case "FILM":
                filtered = semua.stream().filter(t -> t instanceof Film).collect(Collectors.toList());
                break;
            case "SERIAL_TV":
                filtered = semua.stream().filter(t -> t instanceof SerialTV).collect(Collectors.toList());
                break;
            default:
                filtered = semua;
                normalized = "ALL";
        }

        model.addAttribute("tayangans", filtered);
        model.addAttribute("activeType", normalized);
        return "catalog";
    }

    @GetMapping("/tayangan/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Tayangan tayangan = tayanganService.lihatTayanganBerdasarkanId(id);
        model.addAttribute("tayangan", tayangan);

        // Kirim jenis + objek bertipe agar template bisa akses getter spesifik
        if (tayangan instanceof Film film) {
            model.addAttribute("jenis", "FILM");
            model.addAttribute("film", film);
        } else if (tayangan instanceof SerialTV serialTv) {
            model.addAttribute("jenis", "SERIAL_TV");
            model.addAttribute("serialTv", serialTv);
        }

        model.addAttribute("reviews", reviewService.lihatReviewBerdasarkanTayangan(id));
        return "detail-film";
    }

    @GetMapping("/tayangan/{id}/ulas")
    public String reviewForm(@PathVariable Long id, Model model,
                             @AuthenticationPrincipal CustomUserDetails currentUser) {
        Tayangan tayangan = tayanganService.lihatTayanganBerdasarkanId(id);
        model.addAttribute("tayangan", tayangan);
        model.addAttribute("currentUserId", currentUser.getId());
        return "review-form";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String email) {
        try {
            UserDTO.RegisterRequest req = new UserDTO.RegisterRequest();
            req.setUsername(username);
            req.setPassword(password);
            req.setEmail(email);
            authService.register(req);
            // AuthService.register() sets aktif=false and emails an OTP;
            // send the user straight to the OTP page so they can verify.
            return "redirect:/verify-otp?email=" + URLEncoder.encode(email, StandardCharsets.UTF_8);
        } catch (RuntimeException e) {
            // Includes: email already verified, username collision (UNIQUE),
            // SMTP send failure — surface as a friendly flash on /register.
            return "redirect:/register?error="
                    + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }

    @GetMapping("/verify-otp")
    public String verifyOtpPage(@RequestParam(required = false) String email, Model model) {
        model.addAttribute("email", email);
        return "verify-otp";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam String email, @RequestParam String otp) {
        try {
            authService.verifikasiOtp(email, otp);
            return "redirect:/login?verified";
        } catch (RuntimeException e) {
            return "redirect:/verify-otp?email=" + URLEncoder.encode(email, StandardCharsets.UTF_8)
                    + "&error";
        }
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email) {
        try {
            authService.lupaPassword(email);
            return "redirect:/reset-password?email=" + URLEncoder.encode(email, StandardCharsets.UTF_8)
                    + "&sent";
        } catch (RuntimeException e) {
            return "redirect:/forgot-password?error";
        }
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam(required = false) String email, Model model) {
        model.addAttribute("email", email);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String email,
                                @RequestParam String otp,
                                @RequestParam String passwordBaru) {
        try {
            authService.resetPassword(email, otp, passwordBaru);
            return "redirect:/login?reset";
        } catch (RuntimeException e) {
            return "redirect:/reset-password?email=" + URLEncoder.encode(email, StandardCharsets.UTF_8)
                    + "&error";
        }
    }
}
