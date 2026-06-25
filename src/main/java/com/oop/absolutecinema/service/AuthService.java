package com.oop.absolutecinema.service;

import com.oop.absolutecinema.entity.User;
import com.oop.absolutecinema.DTO.UserDTO;
import com.oop.absolutecinema.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private OTPMailService otpService;

    @Autowired
    private EmailService emailService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // =====================
    // REGISTER
    // =====================
    public UserDTO.Response register(UserDTO.RegisterRequest dto) {

        // Cek apakah email sudah dipakai akun existing. Kalau ada akun
        // belum terverifikasi (aktif=false) dari percobaan register sebelumnya
        // yang gagal kirim OTP / ditinggal user, kita reuse row-nya supaya
        // UNIQUE constraint di kolom email tidak menghalangi retry. Username
        // dan password ditimpa dari permintaan baru, lalu OTP di-regenerate.
        Optional<User> existing = userRepository.findByEmail(dto.getEmail());

        User user;
        if (existing.isPresent()) {
            User u = existing.get();
            if (u.isAktif()) {
                throw new RuntimeException(
                    "Email sudah terdaftar dan terverifikasi. Silakan login.");
            }
            u.setUsername(dto.getUsername());
            u.setPassword(passwordEncoder.encode(dto.getPassword()));
            user = u;
        } else {
            user = new User(dto.getUsername(), passwordEncoder.encode(dto.getPassword()), "MEMBER");
            user.setEmail(dto.getEmail());
            user.setAktif(false); // belum aktif sampai OTP diverifikasi
        }
        userRepository.save(user);

        // Generate OTP dan kirim ke email
        String otp = otpService.generateOtp(dto.getEmail());
        emailService.kirimOtp(dto.getEmail(), otp);

        return new UserDTO.Response(
            user.getId(),
            user.getUsername(),
            user.getRole(),
            "OTP telah dikirim ke " + dto.getEmail()
        );
    }

    // =====================
    // LOGIN
    // =====================
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

    // =====================
    // VERIFIKASI OTP
    // =====================
    public String verifikasiOtp(String email, String otp) {

        if (!otpService.validateOtp(email, otp)) {
            throw new RuntimeException("Kode OTP salah atau sudah expired");
        }

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Email tidak ditemukan"));

        user.setAktif(true);
        userRepository.save(user);

        return "Akun berhasil diaktifkan, silakan login!";
    }

    // =====================
    // LUPA PASSWORD
    // =====================
    public String lupaPassword(String email) {

        userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Email tidak terdaftar"));

        String otp = otpService.generateOtp(email);
        emailService.kirimOtp(email, otp);

        return "OTP reset password telah dikirim ke " + email;
    }

    // =====================
    // RESET PASSWORD
    // =====================
    public String resetPassword(String email, String otp, String passwordBaru) {

        if (!otpService.validateOtp(email, otp)) {
            throw new RuntimeException("Kode OTP salah atau sudah expired");
        }

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Email tidak ditemukan"));

        user.setPassword(passwordEncoder.encode(passwordBaru));
        userRepository.save(user);

        return "Password berhasil direset!";
    }
}