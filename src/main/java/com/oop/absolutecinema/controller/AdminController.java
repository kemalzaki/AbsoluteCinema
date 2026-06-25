package com.oop.absolutecinema.controller;

import com.oop.absolutecinema.entity.Film;
import com.oop.absolutecinema.entity.SerialTV;
import com.oop.absolutecinema.entity.Tayangan;
import com.oop.absolutecinema.service.TayanganService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    /**
     * Form edit tayangan. Memilih field sesuai jenis (FILM / SERIAL_TV),
     * pre-filled dari instance yang ada di database.
     */
    @GetMapping("/admin/tayangan/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Tayangan tayangan = tayanganService.lihatTayanganBerdasarkanId(id);
        model.addAttribute("tayangan", tayangan);

        if (tayangan instanceof Film) {
            model.addAttribute("jenis", "FILM");
            model.addAttribute("film", tayangan);
        } else if (tayangan instanceof SerialTV) {
            model.addAttribute("jenis", "SERIAL_TV");
            model.addAttribute("serialTv", tayangan);
        }
        return "edit-tayangan";
    }

    /**
     * Update tayangan. Membangun entity bertipe konkret (Film/SerialTV)
     * sesuai hidden field "jenis" agar cabang instanceof di editTayangan tercapai.
     */
    @PostMapping("/admin/tayangan/{id}")
    public String updateTayangan(
            @PathVariable Long id,
            @RequestParam String jenis,
            @RequestParam String judul,
            @RequestParam(required = false) String sinopsis,
            @RequestParam int tahunRilis,
            @RequestParam(required = false) String gambarUrl,
            @RequestParam(required = false) Integer durasiMenit,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String sutradara,
            @RequestParam(required = false) Integer jumlahMusim,
            @RequestParam(required = false) Integer totalEpisode,
            @RequestParam(required = false) String jaringanTV,
            @RequestParam(defaultValue = "false") boolean masihBerjalan) {
        try {
            Tayangan dataEdit;
            if ("FILM".equals(jenis)) {
                Film film = new Film(judul, emptyToNull(sinopsis), tahunRilis,
                        durasiMenit == null ? 0 : durasiMenit,
                        emptyToNull(genre), emptyToNull(sutradara));
                film.setGambarUrl(emptyToNull(gambarUrl));
                dataEdit = film;
            } else if ("SERIAL_TV".equals(jenis)) {
                SerialTV serial = new SerialTV(judul, emptyToNull(sinopsis), tahunRilis,
                        jumlahMusim == null ? 0 : jumlahMusim,
                        totalEpisode == null ? 0 : totalEpisode,
                        emptyToNull(jaringanTV), masihBerjalan);
                serial.setGambarUrl(emptyToNull(gambarUrl));
                dataEdit = serial;
            } else {
                return "redirect:/admin/tayangan/" + id + "/edit?error="
                        + URLEncoder.encode("Jenis tayangan tidak dikenal", StandardCharsets.UTF_8);
            }

            tayanganService.editTayangan(id, dataEdit);
            return "redirect:/tayangan/" + id + "?edited";
        } catch (RuntimeException e) {
            return "redirect:/admin/tayangan/" + id + "/edit?error="
                    + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }

    /**
     * Hapus tayangan. Jika tayangan masih memiliki ulasan (FK review→tayangan
     * tidak cascade), DataIntegrityViolationException ditangkap → flash
     * ?deleteBlocked; tayangan tetap utuh.
     */
    @PostMapping("/admin/tayangan/{id}/delete")
    public String deleteTayangan(@PathVariable Long id) {
        try {
            tayanganService.hapusTayangan(id);
            return "redirect:/katalog?deleted";
        } catch (DataIntegrityViolationException e) {
            return "redirect:/tayangan/" + id + "?deleteBlocked";
        }
    }

    private String emptyToNull(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }
}
