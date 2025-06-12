//package com.example.cloud_storage.controller;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//
//
//import com.example.cloud_storage.FileService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.web.multipart.MultipartFile;
//
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//
//@SpringBootTest
//@AutoConfigureMockMvc // Автоматически настраивает MockMvc
//class FileControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc; // Инструмент для отправки тестовых HTTP-запросов
//
//    @MockBean // Заменяем реальный бин FileService на мок
//    private FileService fileService;
//
//    // ВАЖНО: Предполагается, что у вас есть зависимость spring-security-test
//    // для работы @WithMockUser. Если ее нет, добавьте в pom.xml.
//    @Test
//    @WithMockUser(username = "testuser") // "Логиним" тестового пользователя. Spring Security создаст для нас Principal.
//    void uploadFile_ShouldReturnOk_WhenRequestIsValid() throws Exception {
//        // --- Arrange ---
//        MockMultipartFile mockFile = new MockMultipartFile(
//                "file", // Имя параметра, как в @RequestPart("file")
//                "test.txt",
//                "text/plain",
//                "test data".getBytes()
//        );
//
//        // --- ИСПРАВЛЕНИЕ ЗДЕСЬ ---
//        // Мы настраиваем мок-сервис, чтобы он ничего не делал при вызове uploadFile.
//        // Используем any(MultipartFile.class) для второго аргумента и any(User.class) для третьего для точности.
//        doNothing().when(fileService).uploadFile(anyString(), any(MultipartFile.class), any(User.class));
//
//        // --- Act & Assert ---
//        mockMvc.perform(multipart("/file") // Создаем multipart-запрос на эндпоинт /file
//                        .file(mockFile) // Прикрепляем файл
//                        .param("filename", "test.txt") // Добавляем параметр запроса
//                        .header("auth-token", "some-dummy-token")) // Не забываем заголовок, который ожидает контроллер
//                .andExpect(status().isOk()); // Ожидаем HTTP статус 200 OK
//    }
//}