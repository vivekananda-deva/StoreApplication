package com.padmavathi.store.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/uploads")
public class UploadController {

    private static final Map<String, String> TYPES = Map.of(
            "image/jpeg", ".jpg", "image/png", ".png", "image/webp", ".webp");

    private final Path dir;

    public UploadController(@Value("${shop.upload-dir:uploads}") String uploadDir) throws IOException {
        this.dir = Path.of(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(dir);
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> upload(@RequestParam("file") MultipartFile file)
            throws IOException {

        String ext = TYPES.get(file.getContentType());
        if (file.isEmpty() || ext == null) {
            throw new IllegalArgumentException("Please choose a JPG, PNG or WebP photo.");
        }

        String name = UUID.randomUUID() + ext;
        file.transferTo(dir.resolve(name));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("url", "/uploads/" + name));
    }
}
