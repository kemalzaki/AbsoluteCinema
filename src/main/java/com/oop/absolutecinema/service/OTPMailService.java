package com.oop.absolutecinema.service;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OTPMailService {

    // Simpan OTP sementara di email : Kode OTP
    private final Map<String, String> otpStorage = new HashMap<>();

    // Generate kode OTP 6 digit
    public String generateOtp(String email) {
        String otp = String.valueOf(new Random().nextInt(900000) + 100000);// pastikan OTP 6 digit
        otpStorage.put(email, otp);
        return otp;
    }

    // Validasi OTP 
    public boolean validateOtp(String email, String inputOtp) {
        String storedOtp = otpStorage.get(email);
        if (storedOtp != null && storedOtp.equals(inputOtp)) {
            otpStorage.remove(email); // hapus setelah berhasil dipakai
            return true;
        }
        return false;
    }
}