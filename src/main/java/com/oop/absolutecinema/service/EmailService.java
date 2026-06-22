package com.oop.absolutecinema.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Kirim OTP ke email user
    public void kirimOtp(String tujuan, String otp) {
        SimpleMailMessage pesan = new SimpleMailMessage();
        pesan.setTo(tujuan);
        pesan.setSubject("Kode OTP Absolute Cinema");
        pesan.setText(
            "Halo!\n\n" +
            "Kode OTP kamu adalah: " + otp + "\n\n" +
            "Kode ini berlaku selama 5 menit.\n" +
            "Jangan bagikan kode ini kepada siapapun.\n\n" +
            "- Tim Software Absolute Cinema"
        );
        mailSender.send(pesan);
    }
}