package com.oop.absolutecinema.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Auth API (register/login) & Swagger stay as before
                .requestMatchers("/api/auth/**").permitAll()

                // Public pages
                .requestMatchers("/", "/katalog", "/katalog/**").permitAll()
                .requestMatchers("/login", "/register").permitAll()
                // Pre-login public flows (OTP verification + forgot/reset password)
                .requestMatchers("/verify-otp", "/forgot-password", "/reset-password").permitAll()

                // Static resources
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()

                // Image upload — admin only (only admins add tayangan)
                .requestMatchers("/api/upload/**").hasRole("ADMIN")

                // Review form page REQUIRES login (currentUserId needed in controller)
                .requestMatchers(HttpMethod.GET, "/tayangan/*/ulas").authenticated()

                // Public detail pages (GET /tayangan/{id})
                .requestMatchers(HttpMethod.GET, "/tayangan/**").permitAll()

                // Admin area
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // Everything else (POST /api/reviews, etc.) needs login
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/katalog", true)
                .failureHandler((request, response, exception) -> {
                    // Redirect disabled (unverified) users to a dedicated message
                    if (exception instanceof DisabledException) {
                        response.sendRedirect("/login?disabled");
                    } else {
                        response.sendRedirect("/login?error");
                    }
                })
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }
}

