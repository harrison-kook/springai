package com.tororang.springai.knowledge.application.dto;

public record IndexDocumentFileCommand(String title, String filename, byte[] fileContent) {
}