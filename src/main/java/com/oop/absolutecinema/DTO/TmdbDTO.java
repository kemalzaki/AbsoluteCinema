package com.oop.absolutecinema.DTO;

/**
 * DTO ringan untuk integrasi TMDB di halaman admin.
 * Tidak memakai validasi bean karena data berasal dari API eksternal,
 * bukan input form langsung.
 */
public class TmdbDTO {

    /**
     * Satu hasil pencarian TMDB. Dipakai oleh /api/tmdb/search untuk
     * merender kartu hasil di admin.html.
     */
    public static class SearchResult {

        private Long tmdbId;
        private String title;
        private String year;
        private String posterUrl;
        private String overview;
        /** "movie" atau "tv" — sama dengan parameter type di endpoint. */
        private String type;

        public SearchResult() {
        }

        public SearchResult(Long tmdbId, String title, String year,
                            String posterUrl, String overview, String type) {
            this.tmdbId = tmdbId;
            this.title = title;
            this.year = year;
            this.posterUrl = posterUrl;
            this.overview = overview;
            this.type = type;
        }

        public Long getTmdbId() { return tmdbId; }
        public void setTmdbId(Long tmdbId) { this.tmdbId = tmdbId; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getYear() { return year; }
        public void setYear(String year) { this.year = year; }

        public String getPosterUrl() { return posterUrl; }
        public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

        public String getOverview() { return overview; }
        public void setOverview(String overview) { this.overview = overview; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }

    /**
     * Hasil operasi import. Dipakai oleh /api/tmdb/import.
     * Saat success=false, field id & judul null dan message berisi alasan.
     */
    public static class ImportResponse {

        private boolean success;
        private Long id;
        private String judul;
        private String message;

        public ImportResponse() {
        }

        public ImportResponse(boolean success, Long id, String judul, String message) {
            this.success = success;
            this.id = id;
            this.judul = judul;
            this.message = message;
        }

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getJudul() { return judul; }
        public void setJudul(String judul) { this.judul = judul; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
