package com.tororang.springai.knowledge.infrastructure;

import com.tororang.springai.knowledge.domain.DocumentFileType;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ApacheDocumentContentExtractorTest {

    private final ApacheDocumentContentExtractor extractor = new ApacheDocumentContentExtractor();

    @Test
    void PDF에서_텍스트를_추출한다() throws IOException {
        // 표준 Type1 폰트(Helvetica)는 한글 인코딩을 지원하지 않아 영문으로 검증한다.
        byte[] pdfBytes = createPdf("Hello PDF Document");

        String content = extractor.extract(DocumentFileType.PDF, pdfBytes);

        assertThat(content).contains("Hello PDF Document");
    }

    @Test
    void XLSX에서_텍스트를_추출한다() throws IOException {
        byte[] xlsxBytes = createXlsx("휴가 정책");

        String content = extractor.extract(DocumentFileType.XLSX, xlsxBytes);

        assertThat(content).contains("휴가 정책");
    }

    @Test
    void DOCX에서_텍스트를_추출한다() throws IOException {
        byte[] docxBytes = createDocx("회사 소개 문서");

        String content = extractor.extract(DocumentFileType.DOCX, docxBytes);

        assertThat(content).contains("회사 소개 문서");
    }

    @Test
    void MD는_그대로_텍스트로_추출한다() {
        byte[] mdBytes = "# 제목\n본문 내용".getBytes(StandardCharsets.UTF_8);

        String content = extractor.extract(DocumentFileType.MD, mdBytes);

        assertThat(content).isEqualTo("# 제목\n본문 내용");
    }

    @Test
    void 손상된_파일이면_예외가_발생한다() {
        byte[] brokenBytes = "이건 PDF가 아닙니다".getBytes(StandardCharsets.UTF_8);

        assertThatThrownBy(() -> extractor.extract(DocumentFileType.PDF, brokenBytes))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private byte[] createPdf(String text) throws IOException {
        try (PDDocument document = new PDDocument();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PDPage page = new PDPage();
            document.addPage(page);
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                contentStream.newLineAtOffset(50, 700);
                contentStream.showText(text);
                contentStream.endText();
            }
            document.save(out);
            return out.toByteArray();
        }
    }

    private byte[] createXlsx(String text) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet();
            Row row = sheet.createRow(0);
            row.createCell(0).setCellValue(text);
            workbook.write(out);
            return out.toByteArray();
        }
    }

    private byte[] createDocx(String text) throws IOException {
        try (XWPFDocument document = new XWPFDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText(text);
            document.write(out);
            return out.toByteArray();
        }
    }
}