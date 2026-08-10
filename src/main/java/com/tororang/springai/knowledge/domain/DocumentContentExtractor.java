package com.tororang.springai.knowledge.domain;

public interface DocumentContentExtractor {

    String extract(DocumentFileType fileType, byte[] fileContent);
}
