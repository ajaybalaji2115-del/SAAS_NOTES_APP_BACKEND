package com.example.demo.dto;

import lombok.Data;

@Data
public class AuthResponseDto {
    private String token;
    private String role;
    private String email;
}