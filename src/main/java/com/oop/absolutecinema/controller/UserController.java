package com.oop.absolutecinema.controller;

import com.oop.absolutecinema.DTO.UserDTO;
import com.oop.absolutecinema.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
  
  @Autowired
  private UserService userService;

  @GetMapping("/{id}/profile")
  public ResponseEntity<?> getProfile(@PathVariable Long id) {
    try {
      // Memanggil fungsi getProfile yang mengembalikan UserDTO.Response
      UserDTO.Response response = userService.getProfile(id);
      return ResponseEntity.ok(response);
    } catch (RuntimeException e) {
      // Menangkap RuntimeException jika user tidak ditemukan, lalu kirim status 404
      return ResponseEntity.status(404).body(e.getMessage());
    }
  }
}
