package com.oop.absolutecinema.DTO;

import jakarta.validation.constraints.*;

public class FilmDTO {

    // Sesuai diagram: judul, sinopsis, durasiMenit
    public static class Request {

        @NotBlank(message = "Judul tidak boleh kosong")
        private String judul;

        @Size(max = 500, message = "Sinopsis maksimal 500 karakter")
        private String sinopsis;

        @NotNull(message = "Durasi tidak boleh kosong")
        @Min(value = 1, message = "Durasi minimal 1 menit")
        private Integer durasiMenit;

        public String getJudul() { return judul; }
        public void setJudul(String judul) { this.judul = judul; }

        public String getSinopsis() { return sinopsis; }
        public void setSinopsis(String sinopsis) { this.sinopsis = sinopsis; }

        public Integer getDurasiMenit() { return durasiMenit; }
        public void setDurasiMenit(Integer durasiMenit) { this.durasiMenit = durasiMenit; }
    }

    public static class Response {

        private Long id;
        private String judul;
        private String sinopsis;
        private int durasiMenit;
        private int tahunRilis;
        private double ratingRataRata;

        public Response(Long id, String judul, String sinopsis,
                        int durasiMenit, int tahunRilis, double ratingRataRata) {
            this.id = id;
            this.judul = judul;
            this.sinopsis = sinopsis;
            this.durasiMenit = durasiMenit;
            this.tahunRilis = tahunRilis;
            this.ratingRataRata = ratingRataRata;
        }

        public Long getId() { return id; }
        public String getJudul() { return judul; }
        public String getSinopsis() { return sinopsis; }
        public int getDurasiMenit() { return durasiMenit; }
        public int getTahunRilis() { return tahunRilis; }
        public double getRatingRataRata() { return ratingRataRata; }
    }
}