package com.example.cloud_storage.controller;

import com.example.cloud_storage.dto.AuthRequest;
import com.example.cloud_storage.dto.AuthResponse;
import com.example.cloud_storage.exception.CloudStorageException;
import com.example.cloud_storage.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import com.example.cloud_storage.dto.AuthRequest;
import com.example.cloud_storage.dto.AuthResponse;
import com.example.cloud_storage.exception.CloudStorageException;
import com.example.cloud_storage.model.User;
import com.example.cloud_storage.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager; // <-- Важно: импортируем
import org.springframework.security.authentication.BadCredentialsException; // <-- Важно: импортируем
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // <-- Важно: импортируем
import org.springframework.security.core.Authentication; // <-- Важно: импортируем
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RestController // Помечает класс как REST контроллер
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;



    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest) {
        try {
            // Аутентификация пользователя через AuthenticationManager
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getLogin(), authRequest.getPassword())
            );

            // Если аутентификация прошла успешно, генерируем и сохраняем токен
            String authToken = authService.generateAuthToken(authRequest.getLogin());
            log.info("User {} logged in successfully.", authRequest.getLogin());
            return ResponseEntity.ok(new AuthResponse(authToken));

        } catch (BadCredentialsException ex) {
            log.warn("Login failed for user {}: Invalid credentials.", authRequest.getLogin());
            throw new CloudStorageException("Bad credentials: Invalid username or password");
        } catch (Exception ex) {
            log.error("An unexpected error occurred during login for user {}: {}", authRequest.getLogin(), ex.getMessage());
            throw new CloudStorageException("An unexpected error occurred during login: " + ex.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("auth-token") String authToken) {
        log.info("Attempting logout for token: {}", authToken);
        authService.logout(authToken);
        return ResponseEntity.ok().build();
    }
}