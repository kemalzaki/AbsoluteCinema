package com.oop.absolutecinema.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Mengirim OTP via Brevo HTTP API (POST https://api.brevo.com/v3/smtp/email)
 * alih-alih SMTP.
 *
 * Alasan: Railway memblokir outbound SMTP (port 25/465/587/2525) sebagai
 * anti-spam measure — koneksi TCP ke smtp.gmail.com maupun smtp-relay.brevo.com
 * sama-sama timeout pada level SYN. HTTP API berjalan di port 443 (HTTPS biasa)
 * yang tidak pernah diblokir, jadi ini solusi yang robust di environment Railway.
 *
 * Konfigurasi (application.properties):
 *   app.brevo.api-key = ${BREVO_API_KEY}   ← generate dari Brevo dashboard
 *   app.mail.from      = ${MAIL_FROM}       ← sender yang sudah diverified di Brevo
 */
@Service
public class EmailService {

    private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    @Value("${app.brevo.api-key:}")
    private String brevoApiKey;

    @Value("${app.mail.from:}")
    private String fromAddress;

    /**
     * Kirim OTP ke email user.
     *
     * @throws RuntimeException jika BREVO_API_KEY belum di-set, atau Brevo
     *                           merespons non-2xx (auth gagal, sender tidak
     *                           terverifikasi, dll). Pesan error asli Brevo
     *                           ikut disertakan untuk debugging.
     */
    public void kirimOtp(String tujuan, String otp) {
        if (brevoApiKey == null || brevoApiKey.isBlank()) {
            throw new RuntimeException(
                "BREVO_API_KEY belum dikonfigurasi — generate di Brevo dashboard.");
        }
        if (fromAddress == null || fromAddress.isBlank()) {
            throw new RuntimeException(
                "app.mail.from (MAIL_FROM) belum dikonfigurasi.");
        }

        try {
            ObjectNode body = objectMapper.createObjectNode();
            ObjectNode sender = body.putObject("sender");
            sender.put("name", "Absolute Cinema");
            sender.put("email", fromAddress);
            body.putArray("to").addObject().put("email", tujuan);
            body.put("subject", "Kode OTP Absolute Cinema");
            body.put("textContent",
                "Halo!\n\n" +
                "Kode OTP kamu adalah: " + otp + "\n\n" +
                "Kode ini berlaku selama 5 menit.\n" +
                "Jangan bagikan kode ini kepada siapapun.\n\n" +
                "- Tim Software Absolute Cinema"
            );

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(BREVO_API_URL))
                    .header("accept", "application/json")
                    .header("content-type", "application/json")
                    .header("api-key", brevoApiKey)
                    .timeout(Duration.ofSeconds(5))
                    .POST(HttpRequest.BodyPublishers.ofString(
                            objectMapper.writeValueAsString(body)))
                    .build();

            HttpResponse<String> res = httpClient.send(req,
                    HttpResponse.BodyHandlers.ofString());

            if (res.statusCode() < 200 || res.statusCode() >= 300) {
                throw new RuntimeException(
                    "Brevo API gagal (HTTP " + res.statusCode() + "): " + res.body());
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Gagal mengirim OTP via Brevo: "
                    + e.getMessage(), e);
        }
    }
}
