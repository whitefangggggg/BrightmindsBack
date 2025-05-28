package com.brightminds.brightminds_backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils; // For filename sanitization
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
// Removed UUID import as it's no longer used for the primary filename

@RestController
@RequestMapping("/api/upload")
// @CrossOrigin annotation is removed as it's handled globally in WebConfig.java
public class FileUploadController {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file,
                                         @RequestParam(value = "gameType", required = false) String gameType) { // Added gameType parameter
        try {
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename()); // Sanitize the original filename

            // Basic validation for filename
            if (originalFilename == null || originalFilename.isEmpty() || originalFilename.contains("..")) {
                return ResponseEntity.status(400).body("Invalid filename.");
            }

            // Determine the target subfolder
            String subfolderPath = "";
            if (gameType != null && !gameType.trim().isEmpty()) {
                // Sanitize gameType to create a safe directory name (e.g., lowercase, no special chars)
                String sanitizedGameType = gameType.trim().toLowerCase().replaceAll("[^a-z0-9-_]", "");
                if (!sanitizedGameType.isEmpty()) {
                    subfolderPath = File.separator + sanitizedGameType;
                }
            }

            File targetDirectory = new File(uploadDir + File.separator + "images" + subfolderPath);
            if (!targetDirectory.exists()) {
                if (!targetDirectory.mkdirs()) {
                    // Log this error on the server for diagnostics
                    System.err.println("Failed to create directory: " + targetDirectory.getAbsolutePath());
                    return ResponseEntity.status(500).body("Failed to create upload directory.");
                }
            }

            // Use the original filename (or handle potential collisions if needed)
            String filenameToSave = originalFilename;
            File destinationFile = new File(targetDirectory.getAbsolutePath() + File.separator + filenameToSave);

            // Optional: Handle filename collisions (e.g., append a number if file exists)
            // For simplicity, this example will overwrite. For a production system, you'd want collision handling.
            // int count = 0;
            // String nameWithoutExtension = filenameToSave.substring(0, filenameToSave.lastIndexOf('.'));
            // String extension = filenameToSave.substring(filenameToSave.lastIndexOf('.'));
            // while (destinationFile.exists()) {
            //     count++;
            //     filenameToSave = nameWithoutExtension + "(" + count + ")" + extension;
            //     destinationFile = new File(targetDirectory.getAbsolutePath() + File.separator + filenameToSave);
            // }

            file.transferTo(destinationFile);

            // Construct the relative path for the client
            String relativeImagePath = "/images" + (subfolderPath.isEmpty() ? "" : "/" + subfolderPath.substring(1)) + "/" + filenameToSave;
            // Ensure consistent use of forward slashes for URL paths
            relativeImagePath = relativeImagePath.replace(File.separatorChar, '/');


            Map<String, String> response = new HashMap<>();
            response.put("imagePath", relativeImagePath); // e.g., /images/4pics1word/img1.jpg

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            System.err.println("File upload failed: " + e.getMessage()); // Log actual error
            e.printStackTrace(); // Print stack trace for debugging
            return ResponseEntity.status(500)
                    .body("Failed to upload file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred during file upload: " + e.getMessage()); // Log actual error
            e.printStackTrace(); // Print stack trace for debugging
            return ResponseEntity.status(500)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }
}