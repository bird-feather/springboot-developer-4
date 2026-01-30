package me.shinsunyoung.springbootdeveloper.service;

import me.shinsunyoung.springbootdeveloper.dto.UploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    UploadResponse store(MultipartFile file);
}
