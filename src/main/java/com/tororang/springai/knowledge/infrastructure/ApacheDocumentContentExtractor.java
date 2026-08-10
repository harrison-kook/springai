package com.tororang.springai.knowledge.infrastructure;

import com.tororang.springai.knowledge.domain.DocumentContentExtractor;
import com.tororang.springai.knowledge.domain.DocumentFileType;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ApacheDocumentContentExtractor implements DocumentContentExtractor {

    @Override
    public String extract(DocumentFileType fileType, byte[] fileContent) {
        try {
            return switch (fileType) {
                case PDF -> extractPdf(fileContent);
                case XLSX -> extractXlsx(fileContent);
                case DOCX -> extractDocx(fileContent);
                case MD -> new String(fileContent, StandardCharsets.UTF_8);
            };
        } catch (IOException e) {
            throw new IllegalArgumentException("failed to extract content from file", e);
        }
    }

    private String extractPdf(byte[] fileContent) throws IOException {
        try (PDDocument document = Loader.loadPDF(fileContent)) {
            return new PDFTextStripper().getText(document);
        }
    }

    private String extractXlsx(byte[] fileContent) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(fileContent))) {
            StringBuilder sb = new StringBuilder();
            for (Sheet sheet : workbook) {
                for (Row row : sheet) {
                    for (Cell cell : row) {
                        sb.append(cell.toString()).append(' ');
                    }
                    sb.append('\n');
                }
            }
            return sb.toString();
        }
    }

    private String extractDocx(byte[] fileContent) throws IOException {
        try (XWPFDocument document = new XWPFDocument(new ByteArrayInputStream(fileContent));
                XWPFWordExtractor wordExtractor = new XWPFWordExtractor(document)) {
            return wordExtractor.getText();
        }
    }
}