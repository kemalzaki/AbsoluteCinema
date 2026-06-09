# Class Diagram Proyek Absolute Cinema

Diagram kelas berikut memvisualisasikan struktur dan arsitektur sistem proyek **Absolute Cinema**. Diagram ini telah disesuaikan secara komprehensif untuk mencakup pembagian tugas ke-12 anggota tim, mulai dari **Core Model**, **DTO**, **Repository (JPA)**, **Service Layer (Interface & Impl)**, **Controller**, hingga **Security**.

```mermaid
classDiagram
    %% ==========================================
    %% 1. CORE & DOMAIN MODEL (Orang 1 & 2)
    %% ==========================================
    class Rateable {
        <<interface>>
        +hitungRatingRataRata() double
    }

    class Tayangan {
        <<abstract entity>>
        -String id
        -String judul
        -String sinopsis
        -int tahunRilis
        -int totalSkor
        -int jumlahReviewer
        +tambahReview(int skor) void
        +tampilkanDetail() void
        +getTotalSkor() int
        +getJumlahReviewer() int
    }

    class Film {
        <<entity>>
        -int durasiMenit
        +tampilkanDetail() void
    }

    class SerialTV {
        <<entity>>
        -int jumlahMusim
        -int totalEpisode
        +tampilkanDetail() void
    }

    class User {
        <<entity>>
        -String id
        -String username
        -String password
        -String role
    }

    class Review {
        <<entity>>
        -String id
        -int skor
        -String teks
        -User user
        -Tayangan tayangan
    }

    %% ==========================================
    %% 2. DATA TRANSFER OBJECT (DTO) (Orang 8)
    %% ==========================================
    class FilmDTO {
        <<DTO>>
        +String judul
        +String sinopsis
        +int durasiMenit
    }
    class ReviewDTO {
        <<DTO>>
        +int skor
        +String teks
        +String userId
        +String tayanganId
    }
    class UserDTO {
        <<DTO>>
        +String username
        +String password
    }

    %% ==========================================
    %% 3. REPOSITORY LAYER (Orang 4)
    %% ==========================================
    class UserRepository {
        <<interface>>
        +findByUsername(String username) User
    }
    class ReviewRepository {
        <<interface>>
        +findByTayanganId(String id) List~Review~
    }
    class FilmRepository {
        <<interface>>
        +findByJudul(String judul) List~Film~
    }
    class SerialTVRepository {
        <<interface>>
    }

    %% ==========================================
    %% 4. SERVICE LAYER (Orang 5 & 6)
    %% ==========================================
    class TayanganService {
        <<interface>>
        +tambahTayangan(Tayangan tayangan)
        +getSemuaTayangan()
        +getTayanganById(String id)
    }
    class TayanganServiceImpl {
        <<Service>>
        -FilmRepository filmRepo
        -SerialTVRepository serialTvRepo
    }

    class ReviewService {
        <<interface>>
        +tambahReview(ReviewDTO reviewDto)
    }
    class ReviewServiceImpl {
        <<Service>>
        -ReviewRepository reviewRepo
        -TayanganService tayanganService
        -UserRepository userRepo
    }

    %% ==========================================
    %% 5. CONTROLLER LAYER & SECURITY (Orang 7 & 8)
    %% ==========================================
    class TayanganController {
        <<RestController>>
        -TayanganService tayanganService
        +tampilkanKatalog()
        +tampilkanDetail()
    }
    class ReviewController {
        <<RestController>>
        -ReviewService reviewService
        +submitReview(ReviewDTO dto)
    }
    class SecurityConfig {
        <<Configuration>>
        +securityFilterChain()
        +passwordEncoder()
    }

    %% ==========================================
    %% RELATIONSHIPS
    %% ==========================================
    
    %% OOP Core: Abstraction & Inheritance
    Rateable <|.. Tayangan : implements
    Tayangan <|-- Film : extends
    Tayangan <|-- SerialTV : extends

    %% Relasi Entitas Database (Orang 3 - JPA Relations)
    Tayangan "1" -- "*" Review : memiliki (OneToMany)
    User "1" -- "*" Review : menulis (OneToMany)
    Review "*" --> "1" User : dimiliki (ManyToOne)
    Review "*" --> "1" Tayangan : terkait pada (ManyToOne)

    %% Dependensi Repository (Orang 4)
    UserRepository ..> User : manages
    ReviewRepository ..> Review : manages
    FilmRepository ..> Film : manages
    SerialTVRepository ..> SerialTV : manages

    %% Implementasi Service (Orang 5 & 6)
    TayanganService <|.. TayanganServiceImpl : implements
    ReviewService <|.. ReviewServiceImpl : implements
    TayanganServiceImpl --> FilmRepository : uses
    TayanganServiceImpl --> SerialTVRepository : uses
    ReviewServiceImpl --> ReviewRepository : uses
    ReviewServiceImpl --> UserRepository : uses
    ReviewServiceImpl --> TayanganService : uses

    %% Dependensi Controller (Orang 7)
    TayanganController --> TayanganService : uses
    ReviewController --> ReviewService : uses

    %% DTO Binding & Security (Orang 8)
    ReviewController ..> ReviewDTO : receives
    TayanganController ..> FilmDTO : receives
    SecurityConfig ..> User : secures
```

## Penjelasan Kelengkapan Sesuai Pembagian Tugas (12 Orang)

1. **Orang 1 (Core Architect)**: Sudah lengkap dengan `Tayangan` (Abstract), `Film`, `SerialTV`, dan `Rateable` (Interface).
2. **Orang 2 (Domain Specialist)**: Tersedia entitas `User` dan `Review`. Sifat *encapsulation* terlihat melalui modifier akses atribut yang bersifat `private` dan setter validasinya.
3. **Orang 3 (Database Engineer)**: Relasi relasional JPA digambarkan dengan asosiasi **OneToMany** dan **ManyToOne** di bagian entitas `User`, `Tayangan`, dan `Review`.
4. **Orang 4 (Repository Layer)**: Tersedia blok *Repository Layer* berupa interface `UserRepository`, `ReviewRepository`, `FilmRepository`, dan `SerialTVRepository`.
5. **Orang 5 (Service - Catalog)**: Pola *dependency injection* yang direpresentasikan oleh *Interface* `TayanganService` dan implementasinya `TayanganServiceImpl`.
6. **Orang 6 (Service - Review)**: Hal yang sama diterapkan pada *Interface* `ReviewService` dan implementasinya `ReviewServiceImpl`.
7. **Orang 7 (Controller Layer)**: Tersedia blok untuk `TayanganController` dan `ReviewController`.
8. **Orang 8 (DTO & Security)**: Telah ditambahkan layer `DTO` (`FilmDTO`, `ReviewDTO`, `UserDTO`) dan komponen konfigurasi keamanan dasar, `SecurityConfig`.
9. **Orang 9 & 10 (UI Developer)**: Halaman statis HTML (contoh: `catalog.html`, `login.html`) tidak divisualisasikan dalam *Class Diagram Back-End Java*, karena UI/Frontend HTML secara teknis bukanlah Class di Java, namun fungsinya direpresentasikan sebagai klien/konsumen dari *Controller*.
10. **Orang 11 & 12 (QA, PM, Documenter)**: Sama seperti poin di atas, file Test Unit (`TayanganServiceTest.java`) dan dokumentasi (`README.md`, slide UAS) tidak masuk struktur OOP Class Diagram murni, namun mereka akan bekerja secara eksternal dari arsitektur diagram di atas.
