package com.example.api.controllers;

import com.example.api.dto.FileInfoDTO;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
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
    @PostMapping("/unzip")
    public ResponseEntity<String> unzipFile(@RequestParam("path") String path) {
        System.out.println("Path received for unzipping: " + path); // Log para ver o caminho recebido
        try {
            Path zipFilePath = Paths.get("uploads").resolve(path).normalize();

            // Verifica se o arquivo ZIP existe
            if (!Files.exists(zipFilePath) || !zipFilePath.toString().endsWith(".zip")) {
                return ResponseEntity.status(404).body("ZIP file not found: " + path);
            }

            // Define o diretório de extração
            Path extractDir = zipFilePath.getParent().resolve(zipFilePath.getFileName().toString().replace(".zip", ""));
            Files.createDirectories(extractDir);

            // Extrai o conteúdo do ZIP
            try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath.toFile()))) {
                ZipEntry zipEntry;
                while ((zipEntry = zis.getNextEntry()) != null) {
                    Path newFilePath = extractDir.resolve(zipEntry.getName()).normalize();
                    if (zipEntry.isDirectory()) {
                        Files.createDirectories(newFilePath);
                    } else {
                        Files.createDirectories(newFilePath.getParent());
                        Files.copy(zis, newFilePath, StandardCopyOption.REPLACE_EXISTING);
                    }
                    zis.closeEntry();
                }
            }

            return ResponseEntity.ok("File unzipped successfully: " + zipFilePath.getFileName());
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Could not unzip file: " + e.getMessage());
        }
    }
    @PostMapping("/save")
    public ResponseEntity<String> saveFile(@RequestParam("path") String path,
                                           @RequestParam("content") String content) {
        try {
            Path filePath = Paths.get("uploads").resolve(path).normalize();

            // Valide se o arquivo realmente existe
            if (!Files.exists(filePath)) {
                return ResponseEntity.status(404).body("File not found: " + path);
            }

            // Gravar o conteúdo no arquivo
            Files.write(filePath, content.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
            return ResponseEntity.ok("File saved successfully: " + path);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Could not save file: " + e.getMessage());
        }
    }

    // Endpoint para download de arquivos
    @GetMapping("/download")
    public ResponseEntity<Resource> download(@RequestParam("path") String path) {
        try {
            Path targetPath = Paths.get("uploads").resolve(path).normalize();

            if (!Files.exists(targetPath)) {
                return ResponseEntity.status(404).body(null);
            }

            Resource resource;
            String downloadFileName;

            if (Files.isDirectory(targetPath)) {
                // Caso seja uma pasta, cria um ZIP temporário
                Path zipFile = Files.createTempFile("folder-", ".zip");
                try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipFile))) {
                    Files.walk(targetPath).forEach(file -> {
                        try {
                            String zipEntryName = targetPath.relativize(file).toString();
                            zos.putNextEntry(new ZipEntry(zipEntryName));
                            if (!Files.isDirectory(file)) {
                                Files.copy(file, zos);
                            }
                            zos.closeEntry();
                        } catch (IOException e) {
                            throw new RuntimeException("Erro ao adicionar arquivo ao ZIP", e);
                        }
                    });
                }
                resource = new UrlResource(zipFile.toUri());
                downloadFileName = targetPath.getFileName() + ".zip";
            } else {
                // Caso seja um arquivo, retorna diretamente
                resource = new UrlResource(targetPath.toUri());
                downloadFileName = targetPath.getFileName().toString();
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + downloadFileName + "\"")
                    .body(resource);

        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }


    // Endpoint para listar arquivos e pastas em um diretório específico
    @GetMapping("/list")
    public ResponseEntity<List<FileInfoDTO>> listFiles(@RequestParam(value = "path", defaultValue = "") String path) {
        try {
            Path baseDir = Paths.get("uploads").resolve(path).normalize();
            if (!Files.exists(baseDir)) {
                return ResponseEntity.status(404).body(Collections.emptyList());
            }

            List<FileInfoDTO> fileInfos = Files.list(baseDir)
                    .map(p -> {
                        String name = p.getFileName().toString();
                        boolean isFolder = Files.isDirectory(p);
                        long size = 0;
                        try {
                            if (isFolder) {
                                // Opcionalmente, calcular o tamanho total da pasta
                                size = Files.walk(p)
                                        .filter(Files::isRegularFile)
                                        .mapToLong(f -> {
                                            try {
                                                return Files.size(f);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                                return 0L;
                                            }
                                        }).sum();
                            } else {
                                size = Files.size(p);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        // Obter o caminho relativo em relação ao diretório base
                        String relativePath = baseDir.relativize(p).toString().replace("\\", "/");

                        return new FileInfoDTO(name, isFolder, size, relativePath);
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(fileInfos);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Collections.emptyList());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFile(@RequestParam("path") String path) {
        try {
            Path filePath = Paths.get("uploads").resolve(path).normalize();
            if (Files.exists(filePath)) {
                Files.walk(filePath)
                        .sorted((a, b) -> b.compareTo(a)) // Primeiro os arquivos mais internos
                        .forEach(p -> {
                            try {
                                Files.delete(p);
                            } catch (IOException e) {
                                throw new RuntimeException("Failed to delete " + p, e);
                            }
                        });
                return ResponseEntity.ok("File or directory deleted successfully: " + filePath);
            } else {
                return ResponseEntity.status(404).body("File not found: " + path);
            }
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Could not delete file: " + e.getMessage());
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
    // Endpoint para criar uma nova pasta
    @PostMapping("/create-folder")
    public ResponseEntity<String> createFolder(@RequestParam("folderName") String folderName,
                                               @RequestParam(value = "path", defaultValue = "") String path) {
        try {
            Path folderPath = Paths.get("uploads").resolve(path).resolve(folderName).normalize();
            if (!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
                return ResponseEntity.ok("Folder created successfully: " + folderName);
            } else {
                return ResponseEntity.status(400).body("Folder already exists: " + folderName);
            }
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Could not create folder: " + folderName + ". Error: " + e.getMessage());
        }
    }
    @RequestMapping(value = "/move", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Map<String, String>> moveFileOrFolder(@RequestBody Map<String, String> payload) {
        String fileName = payload.get("fileName");
        String from = payload.get("from");
        String to = payload.get("to");

        Map<String, String> response = new HashMap<>();

        if (fileName == null || from == null || to == null) {
            response.put("status", "error");
            response.put("message", "Missing required parameters.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            Path sourcePath = Paths.get("uploads").resolve(from).resolve(fileName).normalize();
            Path targetPath = Paths.get("uploads").resolve(to).resolve(fileName).normalize();

            if (!Files.exists(sourcePath)) {
                response.put("status", "error");
                response.put("message", "Source not found: " + sourcePath);
                return ResponseEntity.status(404).body(response);
            }

            // Verifica se é diretório ou arquivo
            if (Files.isDirectory(sourcePath)) {
                // Mover um diretório com seu conteúdo
                Files.walkFileTree(sourcePath, new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        Path targetDir = targetPath.resolve(sourcePath.relativize(dir));
                        Files.createDirectories(targetDir); // Cria diretórios no destino
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Path targetFile = targetPath.resolve(sourcePath.relativize(file));
                        Files.move(file, targetFile, StandardCopyOption.REPLACE_EXISTING); // Move arquivos
                        return FileVisitResult.CONTINUE;
                    }
                });

                // Remove o diretório original após mover seu conteúdo
                Files.walkFileTree(sourcePath, new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }
                });

            } else {
                // Mover um arquivo individual
                Files.createDirectories(targetPath.getParent());
                Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }

            response.put("status", "success");
            response.put("message", "Moved successfully from " + from + " to " + to);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("status", "error");
            response.put("message", "Could not move: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/rename")
    public ResponseEntity<String> renameFileOrFolder(
            @RequestParam("oldPath") String oldPath,
            @RequestParam("newPath") String newPath) {
        try {
            Path sourcePath = Paths.get("uploads").resolve(oldPath).normalize();
            Path targetPath = Paths.get("uploads").resolve(newPath).normalize();

            if (!Files.exists(sourcePath)) {
                return ResponseEntity.status(404).body("Source not found: " + oldPath);
            }

            // Verifica se o destino já existe
            if (Files.exists(targetPath)) {
                return ResponseEntity.status(400).body("Target already exists: " + newPath);
            }

            // Movimenta o arquivo ou pasta para o novo caminho
            Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);

            return ResponseEntity.ok("Item renamed successfully from " + oldPath + " to " + newPath);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Could not rename item: " + e.getMessage());
        }
    }


}
