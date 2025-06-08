package com.example.cloud_storage.controller;

        import com.example.cloud_storage.dto.FileResponse;
        import com.example.cloud_storage.model.User;
        import com.example.cloud_storage.service.AuthService;
        import com.example.cloud_storage.service.FileService;
        import lombok.extern.slf4j.Slf4j;
        import org.springframework.http.HttpStatus;
        import org.springframework.http.ResponseEntity;
        import org.springframework.security.core.Authentication;
        import org.springframework.security.core.context.SecurityContextHolder;
        import org.springframework.web.bind.annotation.*;
        import org.springframework.web.multipart.MultipartFile;

        import java.util.List;

@Slf4j
@RestController
public class FileController {

    private final FileService fileService;
    private final AuthService authService; // Нужен для получения текущего пользователя

    public FileController(FileService fileService, AuthService authService) {
        this.fileService = fileService;
        this.authService = authService;
    }

    // Вспомогательный метод для получения текущего аутентифицированного пользователя
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        // Этого не должно произойти, если наш AuthTokenFilter работает корректно
        log.error("Could not retrieve current authenticated user from SecurityContext.");
        return null; // или throw exception
    }

    @PostMapping("/file")
    public ResponseEntity<Void> uploadFile(
            @RequestHeader("auth-token") String authToken, // Получаем токен, хотя Spring Security уже его проверил
            @RequestParam("filename") String filename,
            @RequestPart("file") MultipartFile file // Получаем файл из тела запроса
    ) {
        log.info("Attempting to upload file '{}' for user with token: {}", filename, authToken);
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // В теории, сюда не дойдем
        }
        fileService.uploadFile(filename, file, currentUser);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/file")
    public ResponseEntity<Void> deleteFile(
            @RequestHeader("auth-token") String authToken,
            @RequestParam("filename") String filename
    ) {
        log.info("Attempting to delete file '{}' for user with token: {}", filename, authToken);
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        fileService.deleteFile(filename, currentUser);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/list")
    public ResponseEntity<List<FileResponse>> getFileList(
            @RequestHeader("auth-token") String authToken,
            @RequestParam("limit") int limit
    ) {
        log.info("Attempting to get file list with limit {} for user with token: {}", limit, authToken);
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<FileResponse> files = fileService.getFileList(limit, currentUser);
        return ResponseEntity.ok(files);
    }
}