package com.example.api.dto;

public class RegisterDTO {
    private String email;
    private String password;

    // Construtor vazio (necessário para frameworks como Jackson)
    public RegisterDTO() {}

    // Construtor completo
    public RegisterDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getters e Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
