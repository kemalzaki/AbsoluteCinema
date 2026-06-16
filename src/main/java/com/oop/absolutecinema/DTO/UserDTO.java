package com.oop.absolutecinema.DTO;

import jakarta.validation.constraints.*;

public class UserDTO {

    public static class RegisterRequest {

        @NotBlank(message = "Username tidak boleh kosong")
        @Size(min = 3, max = 20, message = "Username 3-20 karakter")
        private String username;

        @NotBlank(message = "Password tidak boleh kosong")
        @Size(min = 8, message = "Password minimal 8 karakter")
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class LoginRequest {

        @NotBlank(message = "Username tidak boleh kosong")
        private String username;

        @NotBlank(message = "Password tidak boleh kosong")
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    // Response TANPA password
    public static class Response {

        private Long id;
        private String username;
        private String role;
        private String message;

        public Response(Long id, String username, String role, String message) {
            this.id = id;
            this.username = username;
            this.role = role;
            this.message = message;
        }

        public Long getId() { return id; }
        public String getUsername() { return username; }
        public String getRole() { return role; }
        public String getMessage() { return message; }
    }
}