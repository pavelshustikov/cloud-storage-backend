package com.example.cloud_storage.service;

import com.example.cloud_storage.exception.CloudStorageException;
import com.example.cloud_storage.model.User;
import com.example.cloud_storage.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager; // <--- Этот импорт пока оставьте
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.UUID;
import com.example.cloud_storage.dto.AuthRequest;
import com.example.cloud_storage.exception.CloudStorageException;
import com.example.cloud_storage.model.User;
import com.example.cloud_storage.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager; // <--- Этот импорт пока оставьте
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
@Service
@Slf4j
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Будем использовать для хеширования паролей

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String generateAuthToken(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        String authToken = UUID.randomUUID().toString();
        user.setAuthToken(authToken);
        user.setTokenCreationTime(System.currentTimeMillis()); // Устанавливаем время создания токена
        userRepository.save(user); // Сохраняем обновленного пользователя с токеном

        log.info("Generated token for user {}: {}", username, authToken);
        return authToken;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    @Transactional
    public String login(String username, String password) {
        // 1. Ищем пользователя по логину
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CloudStorageException("Bad credentials: User not found"));

        // 2. Проверяем пароль
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CloudStorageException("Bad credentials: Invalid password");
        }

        // 3. Генерируем новый токен
        String authToken = UUID.randomUUID().toString();
        user.setAuthToken(authToken);
        user.setTokenCreationTime(System.currentTimeMillis()); // Сохраняем время создания токена
        userRepository.save(user); // Сохраняем токен в БД

        return authToken;
    }

    @Transactional
    public void logout(String authToken) {
        // 1. Ищем пользователя по токену
        User user = userRepository.findByAuthToken(authToken)
                .orElseThrow(() -> new CloudStorageException("Unauthorized: Invalid auth token"));

        // 2. Удаляем токен (делаем его недействительным)
        user.setAuthToken(null);
        user.setTokenCreationTime(null);
        userRepository.save(user);
    }

    // Метод для получения пользователя по токену (будет использоваться в фильтрах безопасности)
    public User getUserByAuthToken(String authToken) {
        return userRepository.findByAuthToken(authToken)
                .orElseThrow(() -> new CloudStorageException("Unauthorized: Invalid auth token"));
    }

    // Вспомогательный метод для регистрации тестового пользователя (можно удалить в продакшене)
    @Transactional
    public void registerUser(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new CloudStorageException("User with this username already exists");
        }
        User newUser = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password)) // Хешируем пароль!
                .build();
        userRepository.save(newUser);
    }

//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
//        return new org.springframework.security.core.userdetails.User(
//                user.getUsername(), user.getPassword(), Collections.emptyList());
//    }
@Override
public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            Collections.emptyList()
    );
}





}