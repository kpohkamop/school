package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
public class AvatarService {

    private static final Logger logger = LoggerFactory.getLogger(AvatarService.class);

    @Value("${avatars.dir.path}")
    private String avatarsDir;

    private final AvatarRepository avatarRepository;
    private final StudentService studentService;

    @Autowired
    public AvatarService(AvatarRepository avatarRepository, StudentService studentService) {
        this.avatarRepository = avatarRepository;
        this.studentService = studentService;
    }

    /**
     * Загрузка аватарки для студента
     */
    public void uploadAvatar(Long studentId, MultipartFile file) throws IOException {
        logger.info("Загрузка аватарки для студента с ID: {}", studentId);

        // Проверяем существование студента
        Student student = studentService.findStudent(studentId);
        if (student == null) {
            throw new IllegalArgumentException("Студент с ID " + studentId + " не найден");
        }

        // Создаем директорию для аватарок, если её нет
        Path avatarsPath = Paths.get(avatarsDir);
        if (!Files.exists(avatarsPath)) {
            Files.createDirectories(avatarsPath);
        }

        // Генерируем путь для сохранения файла
        String fileExtension = getExtension(file.getOriginalFilename());
        String fileName = studentId + "_" + System.currentTimeMillis() + fileExtension;
        Path filePath = Paths.get(avatarsDir, fileName);

        // Сохраняем файл на диск
        Files.write(filePath, file.getBytes(), CREATE_NEW);
        logger.debug("Файл сохранен на диск: {}", filePath);

        // Создаем или обновляем запись в БД
        Avatar avatar = avatarRepository.findByStudentId(studentId)
                .orElse(new Avatar());

        avatar.setStudent(student);
        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(file.getSize());
        avatar.setMediaType(file.getContentType());

        // Для демонстрации сохраняем также и данные в БД
        // (можно убрать, если не нужно хранить в БД)
        avatar.setData(file.getBytes());

        avatarRepository.save(avatar);
        logger.info("Аватарка для студента {} успешно загружена", studentId);
    }

    /**
     * Получение аватарки из БД
     */
    public Avatar getAvatarFromDb(Long studentId) {
        logger.info("Получение аватарки из БД для студента: {}", studentId);
        return avatarRepository.findByStudentId(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Аватарка для студента " + studentId + " не найдена"));
    }

    /**
     * Получение аватарки из файловой системы
     */
    public byte[] getAvatarFromFile(Long studentId) throws IOException {
        logger.info("Получение аватарки из файла для студента: {}", studentId);

        Avatar avatar = avatarRepository.findByStudentId(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Аватарка для студента " + studentId + " не найдена"));

        Path filePath = Paths.get(avatar.getFilePath());
        if (!Files.exists(filePath)) {
            throw new IllegalArgumentException("Файл аватарки не найден: " + avatar.getFilePath());
        }

        return Files.readAllBytes(filePath);
    }

    /**
     * Получение аватарки с предварительным сжатием для превью
     */
    public byte[] getAvatarPreview(Long studentId, int width, int height) throws IOException {
        logger.info("Получение превью аватарки {}x{} для студента: {}", width, height, studentId);

        byte[] originalData = getAvatarFromFile(studentId);

        BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(originalData));
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D graphics = resizedImage.createGraphics();
        graphics.drawImage(originalImage, 0, 0, width, height, null);
        graphics.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "jpg", baos);

        return baos.toByteArray();
    }

    /**
     * Удаление аватарки
     */
    public void deleteAvatar(Long studentId) throws IOException {
        logger.info("Удаление аватарки для студента: {}", studentId);

        Avatar avatar = avatarRepository.findByStudentId(studentId)
                .orElse(null);

        if (avatar != null) {
            // Удаляем файл с диска
            Path filePath = Paths.get(avatar.getFilePath());
            Files.deleteIfExists(filePath);

            // Удаляем запись из БД
            avatarRepository.delete(avatar);
            logger.info("Аватарка удалена");
        }
    }

    private String getExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return "";
        }
        return fileName.substring(lastDotIndex);
    }
}
