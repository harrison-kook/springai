package com.tororang.springai.knowledge.domain;

public enum DocumentFileType {

    PDF, XLSX, DOCX, MD;

    public static DocumentFileType fromFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("filename must not be empty");
        }

        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == filename.length() - 1) {
            throw new IllegalArgumentException("unsupported file type: " + filename);
        }

        String extension = filename.substring(dotIndex + 1).toLowerCase();
        return switch (extension) {
            case "pdf" -> PDF;
            case "xlsx" -> XLSX;
            case "docx" -> DOCX;
            case "md" -> MD;
            default -> throw new IllegalArgumentException("unsupported file type: " + filename);
        };
    }
}