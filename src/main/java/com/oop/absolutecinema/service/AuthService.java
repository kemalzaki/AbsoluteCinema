package com.oop.absolutecinema.service;

import com.oop.absolutecinema.entity.User;
import com.oop.absolutecinema.DTO.UserDTO;
import com.oop.absolutecinema.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // PASTIKAN MENERIMA UserDTO.RegisterRequest
    public UserDTO.Response register(UserDTO.RegisterRequest dto) {
        String hashedPassword = passwordEncoder.encode(dto.getPassword());
        
        // Membuat objek user dengan role MEMBER agar lolos validasi entity
        User user = new User(dto.getUsername(), hashedPassword, "MEMBER");
        userRepository.save(user);

        return new UserDTO.Response(
                user.getId(),
                user.getUsername(),
                user.getRole(),
                "Registrasi berhasil!"
        );
    }

    // PASTIKAN MENERIMA UserDTO.LoginRequest
    public UserDTO.Response login(UserDTO.LoginRequest dto) {
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("Username tidak ditemukan"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Password salah");
        }

        return new UserDTO.Response(
                user.getId(),
                user.getUsername(),
                user.getRole(),
                "Login berhasil!"
        );
    }
}