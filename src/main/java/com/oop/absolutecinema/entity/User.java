package com.cinereview.model; // Disesuaikan dengan folder com/cinereview/model

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;

    /**
     * Alasan FetchType.LAZY:
     * Mencegah "N+1 Query Problem". Data review hanya akan ditarik dari database
     * saat benar-benar dipanggil via getter, menghemat memori (O(1) di awal).
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();

    /**
     * No-Arg Constructor dengan akses 'protected' agar mematuhi spesifikasi Hibernate/JPA,
     * sekaligus mencegah instansiasi objek kosong secara ilegal dari layer luar.
     */
    protected User() {}

    public User(String username, String password, String role) {
        this.id = UUID.randomUUID().toString();
        setUsername(username); // Memaksa validasi guard clause berjalan saat objek dibuat
        setPassword(password);
        setRole(role);
    }

    // --- ENCAPSULATION & VALIDATION (GUARD CLAUSES) ---

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username tidak boleh kosong atau null.");
        }
        if (username.length() < 4) {
            throw new IllegalArgumentException("Username minimal harus 4 karakter.");
        }
        this.username = username.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password tidak boleh kosong atau null.");
        }
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        if (role == null || (!role.equalsIgnoreCase("ADMIN") && !role.equalsIgnoreCase("MEMBER"))) {
            throw new IllegalArgumentException("Role tidak valid! Pilihannya hanya ADMIN atau MEMBER.");
        }
        this.role = role.toUpperCase();
    }
    
    public List<Review> getReviews() {
        return reviews;
    }
}