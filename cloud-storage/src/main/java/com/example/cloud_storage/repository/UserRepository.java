package com.example.cloud_storage.repository;


import com.example.cloud_storage.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // Помечает интерфейс как компонент репозитория Spring
public interface UserRepository extends JpaRepository<User, Long> {
    // JpaRepository предоставляет базовые CRUD-операции (сохранить, найти по ID, удалить и т.д.)

    // Пользовательский метод для поиска пользователя по имени пользователя
    Optional<User> findByUsername(String username);

    // Пользовательский метод для поиска пользователя по токену авторизации
    Optional<User> findByAuthToken(String authToken);
}