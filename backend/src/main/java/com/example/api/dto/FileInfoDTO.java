package com.example.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FileInfoDTO {
    private String name;

    @JsonProperty("isFolder")
    private boolean isFolder;

    private long size;

    private String relativePath; // Novo campo

    // Construtor sem argumentos
    public FileInfoDTO() {
    }

    // Construtor com argumentos
    public FileInfoDTO(String name, boolean isFolder, long size, String relativePath) {
        this.name = name;
        this.isFolder = isFolder;
        this.size = size;
        this.relativePath = relativePath;
    }

    // Getters e Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFolder() {
        return isFolder;
    }

    public void setFolder(boolean folder) {
        isFolder = folder;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }
}
