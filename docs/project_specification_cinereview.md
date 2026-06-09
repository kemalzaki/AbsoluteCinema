# Project Specification: Absolute Cinema (Sistem Review & Rating Film Berbasis OOP)

Selamat datang di dokumen spesifikasi proyek **Absolute Cinema**. Proyek ini dirancang khusus untuk memenuhi kriteria mata kuliah Pemrograman Berorientasi Objek (OOP) menggunakan teknologi **Java** dan **Framework Spring Boot**.

---

## 1. Deskripsi Proyek
**Absolute Cinema** adalah sebuah platform *backend API* (dengan opsional sistem *view* berbasis Thymeleaf) yang memungkinkan pengguna untuk mencari, melihat detail, memberikan rating, dan menulis ulasan untuk berbagai jenis tayangan hiburan (seperti Film Layar Lebar dan Serial TV). Sistem akan mengkalkulasi rating secara otomatis dan dinamis setiap kali ada ulasan baru masuk.

---

## 2. Penerapan 4 Pilar OOP
Proyek ini wajib mendemonstrasikan implementasi nyata dari empat pilar dasar OOP:

* **Inheritance (Pewarisan):** * `Abstract Class` induk bernama `Tayangan`.
    * `Class` anak: `Film` (memiliki atribut khusus seperti `durasiMenit`) dan `SerialTV` (memiliki atribut khusus seperti `jumlahMusim` dan `totalEpisode`). Kedua kelas anak ini mewarisi atribut umum seperti `id`, `judul`, `sinopsis`, dan `tahunRilis`.
* **Encapsulation (Pembungkusan):**
    * Semua variabel/atribut di dalam *class* di-set sebagai `private`.
    * Atribut sensitif seperti `totalSkor` dan `jumlahReviewer` tidak boleh diubah langsung dari luar. Perubahan data hanya bisa dilakukan melalui *method* internal seperti `tambahReview(int skor)`.
    * Akses data dari luar menggunakan *getter* dan *setter* yang diberi validasi khusus (misal: skor rating wajib di antara angka 1 sampai 5).
* **Polymorphism (Banyak Bentuk):**
    * *Method Overriding* pada fungsi `tampilkanDetail()`. Ketika dipanggil pada objek `Film`, ia menampilkan durasi waktu. Ketika dipanggil pada objek `SerialTV`, ia menampilkan jumlah musim dan episode saat ini.
* **Abstraction (Abstraksi):**
    * Membuat sebuah `Interface` bernama `Rateable`. Interface ini memuat kontrak fungsi `hitungRatingRataRata()`. 
    * Di masa depan, interface ini tidak hanya diimplementasikan pada entitas film/serial saja, tetapi bisa juga pada entitas `Aktor` atau `Sutradara`.

---

## 3. Spesifikasi Fitur Utama (Scope)
1.  **Katalog Hiburan:** CRUD (Create, Read, Update, Delete) untuk data Film dan Serial TV (Khusus role Admin).
2.  **Sistem Ulasan:** Pengguna dapat memberikan skor bintang (1-5) dan teks ulasan pada tayangan tertentu.
3.  **Kalkulator Rating Otomatis:** Sistem secara otomatis memperbarui nilai rata-rata rating tayangan secara *real-time* saat ulasan baru masuk menggunakan rumus matematika yang di-capsulate di dalam objek.
4.  **Autentikasi Dasar:** Registrasi dan Login user (untuk membedakan role *Admin* dan *Regular User*).

---

## 4. Pembagian Tugas Kelompok (12 Orang)
Mengingat jumlah anggota kelompok yang cukup besar (12 orang), tim akan dibagi menjadi 3 sub-tim utama: **Backend Core (OOP & DB)**, **API & Integration (Controller & View)**, serta **Support (QA & Management)**.

Berikut adalah rincian pembagian tugas per orang:

### A. Sub-Tim Backend Core (OOP & Domain Model) - 4 Orang
Fokus pada perancangan arsitektur kelas OOP, entitas, dan hubungan database.
* **Orang 1 (Core Architect - Inheritance & Abstraction):** Membuat *abstract class* `Tayangan`, kelas turunan `Film` dan `SerialTV`, serta *interface* `Rateable`. Bertanggung jawab memastikan struktur pewarisan berjalan lancar.
* **Orang 2 (Domain Specialist - Encapsulation & Logic):** Membuat kelas entitas `User` dan `Review`. Menyusun logika *encapsulation* di dalam kelas (validasi setter/getter dan rumus matematika internal kalkulasi rating).
* **Orang 3 (Database Engineer - Relational Mapping):** Mengonfigurasi Spring Data JPA dan mendesain relasi antar objek di database (misalnya relasi *One-to-Many* dari `Tayangan` ke `Review`, dan relasi ke `User`).
* **Orang 4 (Repository Layer Developer):** Membuat semua *interface* repository (Spring Data JPA Repositories) untuk semua entitas serta menyusun *custom query* (misalnya mencari film berdasarkan genre atau tahun).

### B. Sub-Tim Service & API (Logic & Controller) - 4 Orang
Fokus pada alur bisnis aplikasi dan menjembatani data ke bagian tampilan.
* **Orang 5 (Service Layer - Catalog Logic):** Membuat `TayanganService` beserta implementasinya untuk mengatur logika bisnis CRUD katalog film dan serial tv.
* **Orang 6 (Service Layer - Review Logic):** Membuat `ReviewService` beserta implementasinya untuk mengatur logika bisnis pengunggahan ulasan, pencegahan duplikasi ulasan oleh user yang sama, dan penanganan rating.
* **Orang 7 (Controller Layer - REST API Endpoints):** Membuat `TayanganController` dan `ReviewController` untuk menyediakan *endpoint* REST API yang bersih dan rapi.
* **Orang 8 (DTO & Security Specialist):** Membuat Data Transfer Object (DTO) untuk menerapkan enkapsulasi data saat dikirim keluar API, serta mengonfigurasi keamanan dasar (Spring Security / Login-Register).

### C. Sub-Tim View & Integration (Frontend/Thymeleaf) - 2 Orang
Fokus pada penyajian data agar aplikasi bisa didemonstrasikan dengan visual yang baik di depan dosen.
* **Orang 9 (UI Developer - Katalog & Detail):** Membuat halaman HTML/Thymeleaf untuk menampilkan daftar katalog film/serial serta halaman detail informasi per film.
* **Orang 10 (UI Developer - Form Ulasan & Auth):** Membuat halaman form untuk mengisi review/rating serta halaman visual untuk login dan register pengguna.

### D. Sub-Tim Support, Testing, & DevOps - 2 Orang
Fokus pada kualitas kode, integrasi tim, dokumentasi, dan kelancaran presentasi UAS.
* **Orang 11 (Quality Assurance & Unit Tester):** Bertanggung jawab membuat *Unit Testing* menggunakan JUnit/Mockito untuk menguji apakah fungsi-fungsi OOP (seperti kalkulasi rating dan validasi enkapsulasi) berjalan 100% benar tanpa *bug*.
* **Orang 12 (Project Manager, Git Master & Documenter):** Mengatur repositori Git kelompok, menyelesaikan *merge conflict*, merapikan dokumentasi API menggunakan Swagger/OpenAPI, serta menyusun materi/slide untuk presentasi UAS.

---

## 5. Alur Kerja & Target (Timeline Singkat)
1.  **Tahap 1 (Desain & Core):** Orang 1, 2, 3, dan 4 menyelesaikan struktur *class* dasar dan database. (Target: Hari ke-3)
2.  **Tahap 2 (Logika & API):** Orang 5, 6, 7, dan 8 menyambungkan struktur class ke logic bisnis dan menyediakan API. (Target: Hari ke-7)
3.  **Tahap 3 (Visual & Tes):** Orang 9, 10, 11, dan 12 membuat tampilan UI Thymeleaf, menguji aplikasi dengan Unit Test, dan merapikan dokumentasi Swagger. (Target: Hari ke-10)