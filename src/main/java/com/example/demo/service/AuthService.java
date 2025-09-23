package com.example.demo.service;

import com.example.demo.dto.AuthResponseDto;
import com.example.demo.dto.LoginRequestDto;
import com.example.demo.entity.User;
// import com.example.demo.entity.Role;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponseDto login(LoginRequestDto loginRequest) {
    User user = userRepository.findByEmail(loginRequest.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));

    String rawPassword = loginRequest.getPassword();
    String storedPassword = user.getPassword();

    System.out.println("Raw password: " + rawPassword);
    System.out.println("Stored password: " + storedPassword);

    if (storedPassword == null) {
        throw new RuntimeException("User password is null");
    }

    // Compare plaintext passwords directly (no bcrypt)
    if (!rawPassword.equals(storedPassword)) {
        throw new RuntimeException("Invalid credentials");
    }

    // Generate token as usual
    String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getTenant().getId(), user.getId());

    AuthResponseDto authResponse = new AuthResponseDto();
    authResponse.setToken(token);
    authResponse.setRole(user.getRole().name());
    return authResponse;
}

}