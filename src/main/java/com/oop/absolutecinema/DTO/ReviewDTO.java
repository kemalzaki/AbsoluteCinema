package com.oop.absolutecinema.DTO;

import jakarta.validation.constraints.*;

public class ReviewDTO {

    // Sesuai diagram: skor, teks, userId (Long), tayanganId (Long)
    public static class Request {

        @NotNull(message = "Skor tidak boleh kosong")
        @Min(value = 1, message = "Skor minimal 1")
        @Max(value = 5, message = "Skor maksimal 5")
        private int skor;

        @NotBlank(message = "Teks review tidak boleh kosong")
        @Size(max = 1000, message = "Review maksimal 1000 karakter")
        private String teks;

        @NotNull(message = "User ID tidak boleh kosong")
        private Long userId;      // Long sesuai diagram

        @NotNull(message = "Tayangan ID tidak boleh kosong")
        private Long tayanganId;  // Long sesuai diagram

        public int getSkor() { return skor; }
        public void setSkor(int skor) { this.skor = skor; }

        public String getTeks() { return teks; }
        public void setTeks(String teks) { this.teks = teks; }

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public Long getTayanganId() { return tayanganId; }
        public void setTayanganId(Long tayanganId) { this.tayanganId = tayanganId; }
    }

    public static class Response {

        private Long id;
        private String usernameUser;
        private String judulTayangan;
        private int skor;
        private String teks;

        public Response(Long id, String usernameUser,
                        String judulTayangan, int skor, String teks) {
            this.id = id;
            this.usernameUser = usernameUser;
            this.judulTayangan = judulTayangan;
            this.skor = skor;
            this.teks = teks;
        }

        public Long getId() { return id; }
        public String getUsernameUser() { return usernameUser; }
        public String getJudulTayangan() { return judulTayangan; }
        public int getSkor() { return skor; }
        public String getTeks() { return teks; }
    }
}