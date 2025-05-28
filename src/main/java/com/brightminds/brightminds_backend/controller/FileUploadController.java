package com.brightminds.brightminds_backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "*")
public class FileUploadController {
    
    @Value("${file.upload-dir}")
    private String uploadDir;
    
    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // Create the upload directory if it doesn't exist
            File directory = new File(uploadDir + "/images");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            // Generate a unique filename to prevent collisions
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = UUID.randomUUID().toString() + extension;
            
            // Save the file
            File dest = new File(directory.getAbsolutePath() + File.separator + uniqueFilename);
            file.transferTo(dest);
            
            // Return the relative path that can be used in the frontend
            String imagePath = "/images/" + uniqueFilename;
            
            Map<String, String> response = new HashMap<>();
            response.put("imagePath", imagePath);
            
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(500)
                .body("Failed to upload file: " + e.getMessage());
        }
    }
}
