package com.oop.absolutecinema.controller;

import com.oop.absolutecinema.DTO.TmdbDTO;
import com.oop.absolutecinema.entity.Tayangan;
import com.oop.absolutecinema.exception.JudulDuplikatException;
import com.oop.absolutecinema.service.TayanganService;
import com.oop.absolutecinema.service.TmdbService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API TMDB untuk admin: cari judul & import ke katalog lokal.
 * Route /api/tmdb/** dilindungi hasRole("ADMIN") oleh SecurityConfig,
 * sehingga api-key TMDB tidak pernah sampai ke browser.
 */
@RestController
@RequestMapping("/api/tmdb")
public class TmdbController {

    private final TmdbService tmdbService;
    private final TayanganService tayanganService;

    public TmdbController(TmdbService tmdbService, TayanganService tayanganService) {
        this.tmdbService = tmdbService;
        this.tayanganService = tayanganService;
    }

    /**
     * GET /api/tmdb/search?q=&type=movie|tv
     * Mengembalikan list SearchResult sebagai JSON.
     */
    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam("q") String query,
                                    @RequestParam(defaultValue = "movie") String type) {
        try {
            List<TmdbDTO.SearchResult> results = tmdbService.search(query, type);
            return ResponseEntity.ok(results);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(errorJson(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(errorJson(e.getMessage()));
        }
    }

    /**
     * POST /api/tmdb/import?type=movie|tv&tmdbId=
     * Bangun entity dari TMDB, lalu persist lewat TayanganService (cek duplikat).
     */
    @PostMapping("/import")
    public ResponseEntity<TmdbDTO.ImportResponse> importItem(
            @RequestParam(defaultValue = "movie") String type,
            @RequestParam Long tmdbId) {
        try {
            Tayangan entity = tmdbService.importItem(type, tmdbId);
            Tayangan saved = tayanganService.tambahTayangan(entity);
            TmdbDTO.ImportResponse ok = new TmdbDTO.ImportResponse(
                    true, saved.getId(), saved.getJudul(), "Import berhasil");
            return ResponseEntity.ok(ok);
        } catch (JudulDuplikatException e) {
            // Judul sudah ada di katalog → bukan error server, kirim success=false
            TmdbDTO.ImportResponse dup = new TmdbDTO.ImportResponse(
                    false, null, null, "sudah ada di katalog");
            return ResponseEntity.ok(dup);
        } catch (RuntimeException e) {
            TmdbDTO.ImportResponse fail = new TmdbDTO.ImportResponse(
                    false, null, null, e.getMessage());
            return ResponseEntity.badRequest().body(fail);
        }
    }

    /** Body error sederhana {success:false, message:...} agar UI bisa .message. */
    private static java.util.Map<String, Object> errorJson(String message) {
        java.util.Map<String, Object> m = new java.util.LinkedHashMap<>();
        m.put("success", false);
        m.put("message", message);
        return m;
    }
}
