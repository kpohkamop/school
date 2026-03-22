package ru.hogwarts.school.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.service.AvatarService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/avatar")
public class AvatarController {

    private final AvatarService avatarService;

    @Value("${avatars.preview.width}")
    private int previewWidth;

    @Value("${avatars.preview.height}")
    private int previewHeight;

    @Autowired
    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    /**
     * Эндпоинт 1: Загрузка аватарки
     */
    @PostMapping(value = "/{studentId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadAvatar(
            @PathVariable Long studentId,
            @RequestParam MultipartFile avatar) {
        try {
            avatarService.uploadAvatar(studentId, avatar);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Аватарка успешно загружена для студента с ID: " + studentId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при загрузке файла: " + e.getMessage());
        }
    }

    /**
     * Эндпоинт 2: Получение аватарки из БД
     */
    @GetMapping(value = "/{studentId}/db", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> getAvatarFromDb(@PathVariable Long studentId) {
        try {
            Avatar avatar = avatarService.getAvatarFromDb(studentId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(avatar.getMediaType()));
            headers.setContentLength(avatar.getFileSize());
            headers.setContentDispositionFormData("attachment", "avatar_" + studentId + ".jpg");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(avatar.getData());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
    }

    /**
     * Эндпоинт 3: Получение аватарки из файловой системы
     */
    @GetMapping(value = "/{studentId}/file", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> getAvatarFromFile(@PathVariable Long studentId) {
        try {
            byte[] data = avatarService.getAvatarFromFile(studentId);
            Avatar avatar = avatarService.getAvatarFromDb(studentId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(avatar.getMediaType()));
            headers.setContentLength(avatar.getFileSize());
            headers.setContentDispositionFormData("attachment", "avatar_" + studentId + ".jpg");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(data);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    /**
     * Эндпоинт 4: Получение аватарок с пагинацией
     */
    @GetMapping("/page")
    public ResponseEntity<Page<Avatar>> getAllAvatars(
            @RequestParam int page,
            @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Avatar> avatarPage = avatarService.getAllAvatars(pageable);
        return ResponseEntity.ok(avatarPage);
    }

    /**
     * Эндпоинт 5: Получение превью аватарки
     */
    @GetMapping(value = "/{studentId}/preview", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getAvatarPreview(@PathVariable Long studentId) {
        try {
            byte[] preview = avatarService.getAvatarPreview(studentId, previewWidth, previewHeight);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            headers.setContentLength(preview.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(preview);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    /**
     * Эндпоинт 6: Удаление аватарки
     */
    @DeleteMapping("/{studentId}")
    public ResponseEntity<String> deleteAvatar(@PathVariable Long studentId) {
        try {
            avatarService.deleteAvatar(studentId);
            return ResponseEntity.ok("Аватарка для студента " + studentId + " удалена");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при удалении файла: " + e.getMessage());
        }
    }
}