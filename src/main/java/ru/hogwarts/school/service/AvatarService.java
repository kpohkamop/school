package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
@Transactional
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

    public void uploadAvatar(Long studentId, MultipartFile file) throws IOException {
        logger.info("Was invoked method for upload avatar for student with id: {}", studentId);

        Student student = studentService.findStudent(studentId);
        if (student == null) {
            logger.error("Student not found with id: {}", studentId);
            throw new IllegalArgumentException("Студент с ID " + studentId + " не найден");
        }

        Path avatarsPath = Paths.get(avatarsDir);
        if (!Files.exists(avatarsPath)) {
            logger.debug("Creating avatars directory: {}", avatarsDir);
            Files.createDirectories(avatarsPath);
        }

        String fileExtension = getExtension(file.getOriginalFilename());
        String fileName = studentId + "_" + System.currentTimeMillis() + fileExtension;
        Path filePath = Paths.get(avatarsDir, fileName);

        Files.write(filePath, file.getBytes(), CREATE_NEW);
        logger.debug("File saved to disk: {}", filePath);

        Avatar avatar = avatarRepository.findByStudentId(studentId)
                .orElse(new Avatar());

        avatar.setStudent(student);
        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(file.getSize());
        avatar.setMediaType(file.getContentType());
        avatar.setData(file.getBytes());

        avatarRepository.save(avatar);
        logger.info("Avatar successfully uploaded for student with id: {}", studentId);
    }

    @Transactional(readOnly = true)
    public Avatar getAvatarFromDb(Long studentId) {
        logger.info("Was invoked method for get avatar from DB for student: {}", studentId);
        return avatarRepository.findByStudentId(studentId)
                .orElseThrow(() -> {
                    logger.error("Avatar not found for student with id: {}", studentId);
                    return new IllegalArgumentException("Аватарка для студента " + studentId + " не найдена");
                });
    }

    @Transactional(readOnly = true)
    public byte[] getAvatarFromFile(Long studentId) throws IOException {
        logger.info("Was invoked method for get avatar from file for student: {}", studentId);

        Avatar avatar = avatarRepository.findByStudentId(studentId)
                .orElseThrow(() -> {
                    logger.error("Avatar not found for student with id: {}", studentId);
                    return new IllegalArgumentException("Аватарка для студента " + studentId + " не найдена");
                });

        Path filePath = Paths.get(avatar.getFilePath());
        if (!Files.exists(filePath)) {
            logger.error("Avatar file not found at path: {}", avatar.getFilePath());
            throw new IllegalArgumentException("Файл аватарки не найден: " + avatar.getFilePath());
        }

        byte[] data = Files.readAllBytes(filePath);
        logger.debug("Avatar file read successfully, size: {} bytes", data.length);
        return data;
    }

    @Transactional(readOnly = true)
    public Page<Avatar> getAllAvatars(Pageable pageable) {
        logger.info("Was invoked method for get all avatars with pagination - page: {}, size: {}",
                pageable.getPageNumber(), pageable.getPageSize());
        Page<Avatar> avatars = avatarRepository.findAll(pageable);
        logger.debug("Found {} avatars, total pages: {}", avatars.getNumberOfElements(), avatars.getTotalPages());
        return avatars;
    }

    public byte[] getAvatarPreview(Long studentId, int width, int height) throws IOException {
        logger.info("Was invoked method for get avatar preview for student: {}, size: {}x{}", studentId, width, height);

        byte[] originalData = getAvatarFromFile(studentId);

        BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(originalData));
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D graphics = resizedImage.createGraphics();
        graphics.drawImage(originalImage, 0, 0, width, height, null);
        graphics.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "jpg", baos);

        logger.debug("Avatar preview created successfully");
        return baos.toByteArray();
    }

    public void deleteAvatar(Long studentId) throws IOException {
        logger.info("Was invoked method for delete avatar for student: {}", studentId);

        Avatar avatar = avatarRepository.findByStudentId(studentId).orElse(null);

        if (avatar != null) {
            Path filePath = Paths.get(avatar.getFilePath());
            Files.deleteIfExists(filePath);
            logger.debug("Avatar file deleted: {}", filePath);

            avatarRepository.delete(avatar);
            logger.info("Avatar deleted for student with id: {}", studentId);
        } else {
            logger.warn("Attempt to delete non-existent avatar for student: {}", studentId);
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