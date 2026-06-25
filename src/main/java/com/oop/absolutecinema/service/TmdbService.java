package com.oop.absolutecinema.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.oop.absolutecinema.DTO.TmdbDTO;
import com.oop.absolutecinema.entity.Film;
import com.oop.absolutecinema.entity.SerialTV;
import com.oop.absolutecinema.entity.Tayangan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Klien TMDB (The Movie Database) + mapper ke entitas lokal.
 *
 * Membaca konfigurasi dari app.tmdb.* (application.yml). Jika api-key kosong,
 * app tetap booting — error baru muncul saat admin memanggil endpoint TMDB.
 *
 * Endpoint TMDB yang dipakai (semua GET, auth via query param api_key):
 *   - GET /search/{movie|tv}?query=&language=
 *   - GET /movie/{id}?append_to_response=credits&language=
 *   - GET /tv/{id}?language=
 *
 * Poster URL: {image-base}{poster_path}. image-base default w500.
 *
 * Pola meniru ImageKitService: RestClient + Jackson JsonNode.
 */
@Service
public class TmdbService {

    @Value("${app.tmdb.api-key:}")
    private String apiKey;

    // baseUrl di-set di konstruktor (dipakai oleh RestClient builder).
    private String baseUrl;

    @Value("${app.tmdb.image-base:https://image.tmdb.org/t/p/w500}")
    private String imageBase;

    @Value("${app.tmdb.language:id-ID}")
    private String language;

    private final RestClient restClient;

    // Constructor injection: @Value dievaluasi Spring saat membuat bean,
    // sehingga baseUrl bisa langsung diset ke RestClient builder (sama seperti
    // pola ImageKitService). baseUrl per-request TIDAK didukung oleh spec.
    public TmdbService(@Value("${app.tmdb.base-url:https://api.themoviedb.org/3}") String baseUrl) {
        this.baseUrl = baseUrl;
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    // =========================================================
    // PENCARIAN
    // =========================================================

    /**
     * Cari judul di TMDB. type="movie" → /search/movie, type="tv" → /search/tv.
     *
     * @param query kata kunci judul (tidak boleh kosong)
     * @param type  "movie" atau "tv"
     * @return list SearchResult (maks ~20); list kosong jika tak ada hasil
     */
    public List<TmdbDTO.SearchResult> search(String query, String type) {
        ensureConfigured();
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("Kata kunci pencarian kosong.");
        }
        String path = searchPath(type);
        String q = query.trim();

        JsonNode root = getJson(b -> b
                .path(path)
                .queryParam("api_key", apiKey)
                .queryParam("language", language)
                .queryParam("query", q)
                .queryParam("include_adult", "false")
                .build());

        List<TmdbDTO.SearchResult> out = new ArrayList<>();
        for (JsonNode r : root.path("results")) {
            TmdbDTO.SearchResult sr = new TmdbDTO.SearchResult();
            sr.setTmdbId(r.path("id").asLong());
            // movie pakai "title", tv pakai "name"
            sr.setTitle(firstNonBlank(r.path("title").asText(), r.path("name").asText()));
            // movie pakai "release_date", tv pakai "first_air_date"
            sr.setYear(yearOf(firstNonBlank(
                    r.path("release_date").asText(),
                    r.path("first_air_date").asText())));
            sr.setPosterUrl(posterUrl(r.path("poster_path").asText()));
            sr.setOverview(r.path("overview").asText());
            sr.setType(type);
            out.add(sr);
        }
        return out;
    }

    // =========================================================
    // IMPORT — bangun entity (belum di-persist)
    // =========================================================

    /**
     * Ambil detail TMDB lalu petakan ke Film atau SerialTV (belum di-persist).
     * Pemanggil bertanggung jawab menyimpan via TayanganService.tambahTayangan.
     *
     * @param type    "movie" atau "tv"
     * @param tmdbId  id TMDB
     * @return entity Tayangan (Film / SerialTV) siap di-save
     * @throws RuntimeException bila field wajib hilang/tidak valid, atau
     *                          TMDB mengembalikan error HTTP.
     */
    public Tayangan importItem(String type, Long tmdbId) {
        ensureConfigured();
        if (tmdbId == null || tmdbId <= 0) {
            throw new IllegalArgumentException("TMDB id tidak valid.");
        }
        if ("movie".equalsIgnoreCase(type)) {
            return buildFilm(tmdbId);
        } else if ("tv".equalsIgnoreCase(type)) {
            return buildSerialTV(tmdbId);
        }
        throw new IllegalArgumentException("Tipe harus 'movie' atau 'tv'.");
    }

    // ----------------- MOVIE → FILM -----------------

    private Film buildFilm(Long tmdbId) {
        JsonNode root = getJson(b -> b
                .path("/movie/" + tmdbId)
                .queryParam("api_key", apiKey)
                .queryParam("language", language)
                .queryParam("append_to_response", "credits")
                .build());

        String judul = root.path("title").asText().trim();
        if (judul.isEmpty()) {
            throw new RuntimeException("Judul kosong di TMDB, pilih judul lain.");
        }

        int tahunRilis = requireYear(root.path("release_date").asText());
        int runtime = root.path("runtime").asInt();
        if (runtime <= 0) {
            throw new RuntimeException("Durasi (runtime) kosong di TMDB, pilih judul lain.");
        }

        String sinopsis = truncate(root.path("overview").asText(), 1000);
        String genre = joinNames(root.path("genres"));
        String sutradara = firstDirector(root.path("credits").path("crew"));
        String poster = posterUrl(root.path("poster_path").asText());

        Film film = new Film(judul, sinopsis, tahunRilis, runtime, genre, sutradara);
        film.setGambarUrl(poster);
        return film;
    }

    // ----------------- TV → SERIALTV -----------------

    private SerialTV buildSerialTV(Long tmdbId) {
        JsonNode root = getJson(b -> b
                .path("/tv/" + tmdbId)
                .queryParam("api_key", apiKey)
                .queryParam("language", language)
                .build());

        String judul = root.path("name").asText().trim();
        if (judul.isEmpty()) {
            throw new RuntimeException("Judul kosong di TMDB, pilih judul lain.");
        }

        int tahunRilis = requireYear(root.path("first_air_date").asText());
        int jumlahMusim = root.path("number_of_seasons").asInt();
        int totalEpisode = root.path("number_of_episodes").asInt();
        if (jumlahMusim <= 0) {
            throw new RuntimeException("Jumlah musim kosong di TMDB, pilih judul lain.");
        }
        if (totalEpisode <= 0) {
            throw new RuntimeException("Total episode kosong di TMDB, pilih judul lain.");
        }

        String sinopsis = truncate(root.path("overview").asText(), 1000);
        String jaringanTV = firstNetwork(root.path("networks"));
        boolean masihBerjalan = root.path("in_production").asBoolean();
        String poster = posterUrl(root.path("poster_path").asText());

        SerialTV serial = new SerialTV(judul, sinopsis, tahunRilis, jumlahMusim,
                totalEpisode, jaringanTV, masihBerjalan);
        serial.setGambarUrl(poster);
        return serial;
    }

    // =========================================================
    // HTTP
    // =========================================================

    /**
     * Jalankan GET ke TMDB. URI dibangun oleh builderFn (path + queryParam + build),
     * baseUrl diset per-request dari config. Melempar RuntimeException dengan
     * pesan ramah pengguna bila TMDB error / tak terjangkau.
     */
    private JsonNode getJson(Function<UriBuilder, URI> builderFn) {
        try {
            JsonNode body = restClient.get()
                    .uri(builderFn)
                    .retrieve()
                    .body(JsonNode.class);
            if (body == null) {
                throw new RuntimeException("Response TMDB kosong.");
            }
            return body;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            int code = e.getStatusCode().value();
            if (code == 401) {
                throw new RuntimeException("TMDB API key tidak valid (401).", e);
            }
            if (code == 429) {
                throw new RuntimeException("Rate limit TMDB tercapai (429), coba lagi nanti.", e);
            }
            if (code == 404) {
                throw new RuntimeException("Judul tidak ditemukan di TMDB (404).", e);
            }
            throw new RuntimeException("TMDB error (HTTP " + code + "): "
                    + e.getResponseBodyAsString(), e);
        } catch (RestClientException e) {
            throw new RuntimeException("Gagal menghubungi TMDB: " + e.getMessage(), e);
        }
    }

    // =========================================================
    // HELPER PARSING
    // =========================================================

    private void ensureConfigured() {
        if (apiKey == null || apiKey.isBlank()) {
            throw new RuntimeException("TMDB belum dikonfigurasi (TMDB_API_KEY kosong).");
        }
    }

    private String searchPath(String type) {
        if ("movie".equalsIgnoreCase(type)) return "/search/movie";
        if ("tv".equalsIgnoreCase(type)) return "/search/tv";
        throw new IllegalArgumentException("Tipe pencarian harus 'movie' atau 'tv'.");
    }

    // Utility pengubah date "YYYY-MM-DD" → tahun int (validasi 1888–2100).
    private int requireYear(String date) {
        if (date == null || date.isBlank()) {
            throw new RuntimeException("Tanggal rilis kosong di TMDB, pilih judul lain.");
        }
        int year;
        try {
            year = Integer.parseInt(date.trim().substring(0, 4));
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            throw new RuntimeException("Format tanggal rilis TMDB tidak dikenal: " + date, e);
        }
        if (year < 1888 || year > 2100) {
            throw new RuntimeException("Tahun rilis di luar rentang valid (1888–2100): " + year);
        }
        return year;
    }

    private String yearOf(String date) {
        if (date == null || date.length() < 4) return "";
        String y = date.substring(0, 4);
        // hanya kembalikan jika numerik (agar UI tidak menampilkan "----")
        return y.matches("\\d{4}") ? y : "";
    }

    private String posterUrl(String posterPath) {
        if (posterPath == null || posterPath.isBlank()) return null;
        return imageBase + posterPath;
    }

    private String truncate(String s, int max) {
        if (s == null) return null;
        return s.length() <= max ? s : s.substring(0, max);
    }

    private String joinNames(JsonNode array) {
        if (array == null || !array.isArray() || array.isEmpty()) return null;
        StringBuilder sb = new StringBuilder();
        for (JsonNode n : array) {
            String name = n.path("name").asText();
            if (name.isBlank()) continue;
            if (sb.length() > 0) sb.append(", ");
            sb.append(name);
        }
        return sb.length() == 0 ? null : sb.toString();
    }

    private String firstDirector(JsonNode crew) {
        if (crew == null || !crew.isArray()) return null;
        for (JsonNode c : crew) {
            if ("Director".equals(c.path("job").asText())) {
                String name = c.path("name").asText();
                if (!name.isBlank()) return name;
            }
        }
        return null;
    }

    private String firstNetwork(JsonNode networks) {
        if (networks == null || !networks.isArray() || networks.isEmpty()) return null;
        String name = networks.get(0).path("name").asText();
        return name.isBlank() ? null : name;
    }

    private String firstNonBlank(String a, String b) {
        if (a != null && !a.isBlank()) return a;
        if (b != null && !b.isBlank()) return b;
        return "";
    }
}
