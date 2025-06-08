package com.example.cloud_storage.repository;


import com.example.cloud_storage.model.File;
import com.example.cloud_storage.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    // Пользовательский метод для поиска файла по имени файла и пользователю
    Optional<File> findByFilenameAndUser(String filename, User user);

    // Пользовательский метод для поиска всех файлов конкретного пользователя, отсортированных по дате загрузки
    List<File> findAllByUserOrderByUploadDateDesc(User user);

    // Пользовательский метод для поиска всех файлов конкретного пользователя с ограничением (limit)
    // Spring Data JPA позволяет передавать Pageable или использовать 'first' / 'top' для ограничения
    // Однако, для простоты и соответствия заданию, которое предполагает 'limit' как int,
    // мы можем либо использовать Pageable, либо извлекать все, а затем обрезать (что неэффективно для очень больших списков).
    // Более правильный подход для 'limit' - использовать Pageable. Давайте пока используем его.
    List<File> findByUser(User user, org.springframework.data.domain.Pageable pageable);

    // Метод для удаления файла по имени файла и пользователю
    void deleteByFilenameAndUser(String filename, User user);
}