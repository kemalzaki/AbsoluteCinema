package com.oop.absolutecinema.service;

import com.oop.absolutecinema.entity.User;
import com.oop.absolutecinema.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Bridge antara UserRepository aplikasi dan Spring Security.
 *
 * Spring Security form login memanggil loadUserByUsername(username) saat
 * memvalidasi kredensial. Authority diberi prefix "ROLE_" agar cocok dengan
 * ekspresi hasRole("ADMIN") di SecurityConfig (role disimpan tanpa prefix
 * di entitas User).
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User dengan username '" + username + "' tidak ditemukan."));

        SimpleGrantedAuthority authority =
                new SimpleGrantedAuthority("ROLE_" + user.getRole());

        return new CustomUserDetails(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.isAktif(),
                Collections.singletonList(authority)
        );
    }
}
