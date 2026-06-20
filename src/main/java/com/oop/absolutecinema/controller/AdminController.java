package com.oop.absolutecinema.controller;

import com.oop.absolutecinema.entity.Film;
import com.oop.absolutecinema.entity.SerialTV;
import com.oop.absolutecinema.service.TayanganService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Halaman admin untuk input tayangan baru (beserta URL gambar).
 * Seluruh route /admin/** dilindungi hasRole("ADMIN") oleh SecurityConfig.
 *
 * Catatan Railway: gambar disimpan sebagai URL string di database, bukan
 * file upload — sehingga tidak hilang saat redeploy.
 */
@Controller
public class AdminController {

    @Autowired
    private TayanganService tayanganService;

    @GetMapping("/admin")
    public String adminPage() {
        return "admin";
    }

    @PostMapping("/admin/film")
    public String tambahFilm(
            @RequestParam String judul,
            @RequestParam(required = false) String sinopsis,
            @RequestParam int tahunRilis,
            @RequestParam int durasiMenit,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String sutradara,
            @RequestParam(required = false) String gambarUrl) {
        try {
            Film film = new Film(judul, emptyToNull(sinopsis), tahunRilis,
                    durasiMenit, emptyToNull(genre), emptyToNull(sutradara));
            film.setGambarUrl(emptyToNull(gambarUrl));
            tayanganService.tambahTayangan(film);
            return "redirect:/admin?added=FILM";
        } catch (Exception e) {
            return "redirect:/admin?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }

    @PostMapping("/admin/serial")
    public String tambahSerial(
            @RequestParam String judul,
            @RequestParam(required = false) String sinopsis,
            @RequestParam int tahunRilis,
            @RequestParam int jumlahMusim,
            @RequestParam int totalEpisode,
            @RequestParam(required = false) String jaringanTV,
            @RequestParam(defaultValue = "false") boolean masihBerjalan,
            @RequestParam(required = false) String gambarUrl) {
        try {
            SerialTV serial = new SerialTV(judul, emptyToNull(sinopsis), tahunRilis,
                    jumlahMusim, totalEpisode, emptyToNull(jaringanTV), masihBerjalan);
            serial.setGambarUrl(emptyToNull(gambarUrl));
            tayanganService.tambahTayangan(serial);
            return "redirect:/admin?added=SERIAL_TV";
        } catch (Exception e) {
            return "redirect:/admin?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }

    private String emptyToNull(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }
}
