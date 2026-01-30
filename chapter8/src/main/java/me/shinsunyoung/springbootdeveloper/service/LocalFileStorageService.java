package me.shinsunyoung.springbootdeveloper.service;

import me.shinsunyoung.springbootdeveloper.dto.UploadResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

@Service
public class LocalFileStorageService implements FileStorageService {

    private final Path uploadDir;

    public LocalFileStorageService(@Value("${app.upload-dir:uploads}") String uploadDir) {
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @Override
    public UploadResponse store(MultipartFile file) {
        try {
            Files.createDirectories(uploadDir);

            String original = Optional.ofNullable(file.getOriginalFilename()).orElse("file");
            String ext = original.contains(".") ? original.substring(original.lastIndexOf('.')) : "";
            String saved = UUID.randomUUID() + ext;

            Files.copy(file.getInputStream(),
                    uploadDir.resolve(saved),
                    StandardCopyOption.REPLACE_EXISTING);

            return new UploadResponse("/uploads/" + saved);

        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
