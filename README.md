# Absolute Cinema
**Sistem Review & Rating Film Berbasis Object-Oriented Programming (OOP)**

Repositori ini merupakan backend service untuk aplikasi Absolute Cinema. Proyek ini adalah sebuah platform backend API (dengan antarmuka web) yang memungkinkan pengguna untuk mencari tayangan, melihat detail, memberikan rating, menulis ulasan, serta mengelola profil. Proyek ini dirancang khusus untuk memenuhi kriteria dan mendemonstrasikan pemahaman mata kuliah Pemrograman Berorientasi Objek (OOP) menggunakan bahasa Java dan framework Spring Boot.

---

## Fitur Utama
* **Kalkulasi Rating Otomatis:** Perhitungan rata-rata skor tayangan secara real-time menggunakan prinsip enkapsulasi OOP saat ulasan baru masuk.
* **Autentikasi & Keamanan:** Sistem login dan registrasi yang diamankan dengan Spring Security, password hashing (BCrypt), dan verifikasi akun berbasis Email OTP.
* **Manajemen File (Local Storage):** Fitur unggah gambar dengan penamaan unik (UUID) untuk poster tayangan dan foto profil pengguna.
* **Katalog Dinamis:** Pemisahan entitas antara Film dan Serial TV dengan properti dan detail yang spesifik untuk masing-masing jenis tayangan.

---

## Penerapan 4 Pilar OOP
Fokus utama arsitektur sistem ini adalah mendemonstrasikan empat pilar utama OOP:

* **Inheritance:** Class anak seperti `Film` (memiliki atribut durasiMenit) dan `SerialTV` (memiliki atribut jumlahMusim dan totalEpisode) mewarisi atribut dasar dari Abstract Class induk `Tayangan`.
* **Encapsulation:** Semua variabel di-set private. Atribut sensitif seperti totalSkor dan jumlahReviewer tidak bisa diubah langsung dari luar, melainkan harus melalui method internal `tambahReview(int skor)`.
* **Polymorphism:** Penerapan Method Overriding pada fungsi `tampilkanDetail()` yang menghasilkan output informasi yang berbeda untuk objek Film dan SerialTV.
* **Abstraction:** Menggunakan Interface bernama `Rateable` yang memuat kontrak fungsi `hitungRatingRataRata()`.

---

## Tech Stack & Arsitektur
Proyek ini dibangun menggunakan teknologi berikut:
* **Bahasa Pemrograman:** Java
* **Framework Backend:** Spring Boot, Spring Security, Spring Data JPA
* **Database:** MySQL
* **Mail Server:** Java Mail Sender (untuk pengiriman OTP)
* **View Template:** Thymeleaf / HTML

### Arsitektur Sistem Layering
Diagram ini menunjukkan alur data dari Frontend hingga tersimpan ke Database.

```mermaid
graph LR
    subgraph UI/View Layer
        A[Halaman Thymeleaf / HTML]
    end

    subgraph Controller Layer
        B(REST Controllers & UI Controllers)
    end

    subgraph Service Layer
        C{Service Logic & File Handling}
    end

    subgraph Repository & Model
        D[Spring Data JPA]
        E((Database MySQL))
    end

    A -- "HTTP Request" --> B
    B -- "Kirim DTO" --> C
    C -- "Terapkan Logika OOP" --> D
    D -- "Query SQL Otomatis" --> E
    E -- "Kembalikan Data" --> D
    D -- "Kembalikan Objek/Entity" --> C
    C -- "Kirim DTO" --> B
    B -- "Render Tampilan" --> A
```

---

## Class Diagram Komprehensif
Diagram kelas ini telah disesuaikan untuk mencakup struktur lapisan (layer) arsitektur secara lengkap.

**1. Domain Model (Entity & DTO Layer)**
![Class Diagram Domain Model](docs/Domain%20Model.png)

**2. Business Model (Service & Repository Layer)**
![Class Diagram Business Model](docs/Business%20Logic.png)

**3. API & Presentation (Controller & Security)**
![Class Diagram API & Presentation](docs/API%20Presentation.png)

---

## Alur Logika Sistem (Flowcharts)

### 1. User Journey Utama
Alur dari sudut pandang pengguna saat membuka aplikasi hingga memberikan ulasan.

```mermaid
flowchart TD
    A([Buka Aplikasi]) --> B{Sudah punya akun?}
    B -- Belum --> C[Masuk Halaman Register]
    B -- Sudah --> D[Masuk Halaman Login]
    C -->|Kirim OTP & Verifikasi| D
    D -->|Login Sukses| E[Masuk Halaman Utama / Katalog]
    E --> F[Cari & Pilih Tayangan]
    F --> G[Buka Halaman Detail Tayangan]
    G --> H{Ingin Beri Ulasan?}
    H -- Tidak --> I([Selesai / Kembali ke Katalog])
    H -- Ya --> J[Isi Form Bintang 1-5 & Komentar]
    J --> K[Sistem Memvalidasi & Menghitung Ulang Rating]
    K --> G
```

### 2. Logika Hitung Rating Otomatis (OOP Core)
Alur ini menerapkan enkapsulasi untuk mencegah manipulasi skor secara langsung.

```mermaid
flowchart TD
    A([Mulai Tambah Ulasan]) --> B[/Terima Input: ID Tayangan, ID User, Skor 1-5, Teks/]
    B --> C[Validasi: Cek Tayangan & User di Database]
    C --> D{Apakah data valid?}
    D -- Tidak --> E[Lempar Exception: Data Tidak Valid]
    D -- Ya --> F{Apakah User sudah pernah mereview?}
    F -- Ya --> G[Lempar Exception: Dilarang Review Ganda]
    F -- Tidak --> H[Buat Objek Review Baru]
    H --> I[Panggil method tayangan.tambahReview]
    I --> J[Logika OOP: Skor ditambahkan, dibagi jumlah reviewer]
    J --> K[Simpan Objek Review & Update Tayangan ke Database]
    K --> L([Selesai - Ulasan & Rating Berhasil Diperbarui])
    E --> M([Selesai - Error])
    G --> M
```

---

## Pembagian Tugas Kelompok (12 Orang)
Tim dibagi menjadi 3 divisi utama untuk memastikan pengembangan yang terstruktur.

| Divisi | Peran | Deskripsi Tugas Utama |
| :--- | :--- | :--- |
| **Backend Core** | Orang 1 | Membuat abstract class `Tayangan`, turunannya, dan interface `Rateable`. |
| | Orang 2 | Membuat entitas `User`, `Review`, dan logika enkapsulasi kalkulasi rating. |
| | Orang 3 | Mengonfigurasi Spring Data JPA dan mendesain relasi entitas. |
| | Orang 4 | Membuat seluruh antarmuka Repository dan custom query. |
| **Service & API** | Orang 5 | Membuat `TayanganService` untuk logika bisnis CRUD katalog film. |
| | Orang 6 | Membuat `ReviewService` untuk logika ulasan dan pembuatan `FileStorageService`. |
| | Orang 7 | Membuat kumpulan Controller untuk API tayangan, ulasan, dan integrasi upload. |
| | Orang 8 | Membuat DTO, konfigurasi Spring Security, dan logika Email OTP. |
| **View & QA** | Orang 9 | Membuat halaman Thymeleaf untuk antarmuka katalog dan detail. |
| | Orang 10 | Membuat antarmuka form interaktif dan halaman autentikasi/profil. |
| | Orang 11 | Membuat Unit Testing untuk validasi logika menggunakan JUnit/Mockito. |
| | Orang 12 | Project Manager, pengelola Git, dan penyusun dokumentasi Swagger. |

---

## Dokumentasi API

Untuk menguji API endpoint secara langsung, sistem ini telah terintegrasi dengan Swagger UI.
* **Swagger UI Endpoint:** `http://localhost:8080/swagger-ui/index.html`
* **OpenAPI Specification:** Silakan lihat file `openapi.yaml` pada direktori root proyek ini.