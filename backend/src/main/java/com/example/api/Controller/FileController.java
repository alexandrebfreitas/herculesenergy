package com.example.api.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Controller
@RequestMapping("/file")
public class FileController {

    // Endpoint para upload de arquivos
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
                                             @RequestParam(value = "path", defaultValue = "") String path) {
        try {
            Path uploadDirectory = Paths.get("uploads").resolve(path).normalize();
            Files.createDirectories(uploadDirectory);
            Path copyLocation = uploadDirectory.resolve(file.getOriginalFilename());
            Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);
            return ResponseEntity.ok("File uploaded successfully: " + file.getOriginalFilename());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Could not upload file: " + file.getOriginalFilename() + ". Error: " + e.getMessage());
        }
    }

    // Endpoint para download de arquivos
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam("path") String path) {
        try {
            Path filePath = Paths.get("uploads").resolve(path).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.status(404).body(null);
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // Endpoint para listar arquivos e pastas em um diretório específico
    @GetMapping("/list")
    public ResponseEntity<List<String>> listFiles(@RequestParam(value = "path", defaultValue = "") String path) {
        try {
            Path baseDir = Paths.get("uploads").resolve(path).normalize(); // Resolve o caminho relativo dentro de uploads
            if (!Files.exists(baseDir)) {
                return ResponseEntity.status(404).body(List.of("Directory not found"));
            }

            List<String> fileNames = Files.list(baseDir)
                    .map(p -> p.getFileName().toString() + (Files.isDirectory(p) ? "/" : "")) // Adiciona '/' ao final das pastas
                    .collect(Collectors.toList());

            return ResponseEntity.ok(fileNames);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(List.of("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/zip-folder")
    public ResponseEntity<Resource> zipFolder(@RequestParam("path") String path) {
        try {
            Path folderPath = Paths.get("uploads").resolve(path).normalize();
            if (!Files.exists(folderPath) || !Files.isDirectory(folderPath)) {
                return ResponseEntity.status(404).body(null);
            }

            // Criar arquivo ZIP
            Path zipFilePath = Paths.get("uploads", path + ".zip");
            try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFilePath.toFile()))) {
                Files.walk(folderPath).forEach(file -> {
                    try {
                        String zipEntryName = folderPath.relativize(file).toString();
                        zipOut.putNextEntry(new ZipEntry(zipEntryName));
                        if (!Files.isDirectory(file)) {
                            Files.copy(file, zipOut);
                        }
                        zipOut.closeEntry();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }

            // Baixar o arquivo ZIP criado
            Resource resource = new UrlResource(zipFilePath.toUri());
            if (resource.exists()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.status(404).body(null);
            }
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}