package com.oop.absolutecinema.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Brevo's SMTP "Login" (afedde001@smtp-brevo.com) is just an auth identifier,
    // not a deliverable From address. We must send From a sender that's been
    // verified in Brevo's dashboard (e.g. absolutecinema0119@gmail.com) or Brevo
    // rejects with "sender not allowed". Defaults to MAIL_USERNAME when unset
    // (backward-compat for providers where login == from, like raw Gmail SMTP).
    @Value("${app.mail.from:${spring.mail.username:}}")
    private String fromAddress;

    // Kirim OTP ke email user
    public void kirimOtp(String tujuan, String otp) {
        SimpleMailMessage pesan = new SimpleMailMessage();
        if (fromAddress != null && !fromAddress.isBlank()) {
            pesan.setFrom(fromAddress);
        }
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