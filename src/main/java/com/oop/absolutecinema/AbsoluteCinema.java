package com.oop.absolutecinema;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AbsoluteCinema {

    public static void main(String[] args) {
        // Force IPv4. WSL2 dev environments often have IPv6 DNS resolution
        // but no IPv6 routing, which makes outbound SMTP hang on the AAAA
        // record. Railway (and most clouds) are properly dual-stack, so
        // preferring IPv4 is safe everywhere.
        System.setProperty("java.net.preferIPv4Stack", "true");
        SpringApplication.run(AbsoluteCinema.class, args);
    }

}