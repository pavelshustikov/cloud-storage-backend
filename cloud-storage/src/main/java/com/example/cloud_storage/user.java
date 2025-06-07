package com.example.cloud_storage.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data // Генерирует геттеры, сеттеры, toString, equals, hashCode
@Builder // Генерирует Builder-паттерн
@NoArgsConstructor // Генерирует конструктор без аргументов
@AllArgsConstructor // Генерирует конструктор со всеми аргументами
@Entity // Помечает класс как сущность JPA
@Table(name = "users") // Указывает имя таблицы в базе данных
public class User {

    @Id // Помечает поле как первичный ключ
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Стратегия генерации ID (автоинкремент)
    private Long id;

    @Column(unique = true, nullable = false) // Уникальное и непустое поле
    private String username;

    @Column(nullable = false) // Непустое поле
    private String password; // Храним хешированный пароль!

    @Column(name = "auth_token", unique = true) // Уникальный токен, может быть null
    private String authToken;

    @Column(name = "token_creation_time")
    private Long tokenCreationTime; // Время создания токена в миллисесекундах (для удобства)

    // Если хотите, можно добавить связь @OneToMany с файлами, но для начала не обязательно.
    // @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    // private List<File> files;
}
