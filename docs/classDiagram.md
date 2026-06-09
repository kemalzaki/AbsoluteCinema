# Class Diagram Proyek Absolute Cinema

Diagram kelas berikut memvisualisasikan struktur dan arsitektur sistem proyek **Absolute Cinema** berdasarkan spesifikasi proyek dan penerapan pilar-pilar Pemrograman Berorientasi Objek (OOP) yang meliputi **Inheritance**, **Encapsulation**, **Polymorphism**, dan **Abstraction**.

```mermaid
classDiagram
    %% Abstraction (Interface)
    class Rateable {
        <<interface>>
        +hitungRatingRataRata() double
    }

    %% Abstract Class (Inheritance Parent)
    class Tayangan {
        <<abstract>>
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

    %% Concrete Classes (Inheritance Child & Polymorphism)
    class Film {
        -int durasiMenit
        +tampilkanDetail() void
    }

    class SerialTV {
        -int jumlahMusim
        -int totalEpisode
        +tampilkanDetail() void
    }

    %% Entities
    class User {
        -String id
        -String username
        -String password
        -String role
    }

    class Review {
        -String id
        -int skor
        -String teks
        -User user
        -Tayangan tayangan
    }

    %% Services (Logic Layer)
    class TayanganService {
        +tambahTayangan(Tayangan tayangan)
        +getSemuaTayangan()
        +getTayanganById(String id)
    }

    class ReviewService {
        +tambahReview(String tayanganId, String userId, int skor, String teks)
    }

    %% Controllers (API/Presentation Layer)
    class TayanganController {
        -TayanganService tayanganService
        +tampilkanKatalog()
        +tampilkanDetail()
    }

    class ReviewController {
        -ReviewService reviewService
        +submitReview()
    }

    %% --- Hubungan antar Class (Relationships) ---
    
    %% Abstraction
    Rateable <|.. Tayangan : implements

    %% Inheritance
    Tayangan <|-- Film : extends
    Tayangan <|-- SerialTV : extends

    %% Relasi Database (One-to-Many dll)
    Tayangan "1" -- "*" Review : memiliki (One-to-Many)
    User "1" -- "*" Review : menulis (One-to-Many)
    Review "1" --> "1" User : dimiliki oleh
    Review "1" --> "1" Tayangan : terkait pada

    %% Dependensi Layer Aplikasi
    TayanganController --> TayanganService : uses
    ReviewController --> ReviewService : uses
    TayanganService --> Tayangan : manages
    ReviewService --> Review : manages
```

## Penjelasan Singkat (4 Pilar OOP):
1. **Inheritance**: `Film` dan `SerialTV` mewarisi atribut dan perilaku dari *abstract class* `Tayangan`.
2. **Encapsulation**: Atribut yang bersifat *sensitive* (seperti `totalSkor` dan `jumlahReviewer`) diakses melalui mekanisme yang aman seperti *method* `tambahReview(int skor)` dan *getter*. Atribut-atribut juga dilindungi dengan modifier `private`.
3. **Polymorphism**: *Method Overriding* terjadi pada fungsi `tampilkanDetail()`. `Film` akan menampilkan durasi, sedangkan `SerialTV` akan menampilkan season/episode.
4. **Abstraction**: `Tayangan` mengimplementasikan *interface* `Rateable` untuk menjamin adanya fungsi standard komputasi rating (`hitungRatingRataRata()`).
