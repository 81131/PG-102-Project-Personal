package com.goldenflame.pg102.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageService() {
        this.fileStorageLocation = Paths.get("src/main/resources/static/images").toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            if (originalFileName.contains("..")) {
                throw new RuntimeException("Sorry! Filename contains invalid path sequence " + originalFileName);
            }

            String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String uniqueFileName = UUID.randomUUID().toString() + extension;

            Path targetLocation = this.fileStorageLocation.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return "/images/" + uniqueFileName; // Return the web-accessible path
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + originalFileName + ". Please try again!", ex);
        }
    }

    public void deleteFile(String imageUrl) {
        try {
            if (imageUrl != null && !imageUrl.isEmpty()) {
                // Remove the leading "/images/" to get the actual file name
                String fileName = imageUrl.substring("/images/".length());
                Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
                Files.deleteIfExists(filePath);
            }
        } catch (IOException ex) {
            System.err.println("Could not delete file: " + imageUrl);
        }
    }
}