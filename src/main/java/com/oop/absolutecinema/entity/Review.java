package com.oop.absolutecinema.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    private String id;

    @Column(nullable = false)
    private int skor;

    @Column(length = 1000)
    private String teks;

    /**
     * Hubungan ManyToOne wajib di-set LAZY secara eksplisit karena default JPA adalah EAGER.
     * Ini krusial agar performa query aplikasi tidak drop saat data review membengkak.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tayangan_id", nullable = false)
    private Tayangan tayangan;

    protected Review() {}

    public Review(int skor, String teks, User user, Tayangan tayangan) {
        this.id = UUID.randomUUID().toString();
        setSkor(skor);
        setTeks(teks);
        setUser(user);
        setTayangan(tayangan);
    }

    // --- ENCAPSULATION & VALIDATION (GUARD CLAUSES) ---

    public String getId() {
        return id;
    }

    public int getSkor() {
        return skor;
    }

    public void setSkor(int skor) {
        // Mengunci Business Rule: Skor wajib berskala 1 - 5
        if (skor < 1 || skor > 5) {
            throw new IllegalArgumentException("Skor manipulatif! Range skor valid adalah 1 hingga 5.");
        }
        this.skor = skor;
    }

    public String getTeks() {
        return teks;
    }

    public void setTeks(String teks) {
        if (teks != null && teks.length() > 1000) {
            throw new IllegalArgumentException("Teks review terlalu panjang, maksimal 1000 karakter.");
        }
        this.teks = (teks == null) ? "" : teks.trim();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Integritas data rusak: Objek Review harus terikat pada User.");
        }
        this.user = user;
    }

    public Tayangan getTayangan() {
        return tayangan;
    }

    public void setTayangan(Tayangan tayangan) {
        if (tayangan == null) {
            throw new IllegalArgumentException("Integritas data rusak: Objek Review harus terikat pada Tayangan.");
        }
        this.tayangan = tayangan;
    }
}
