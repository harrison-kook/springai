package com.tororang.springai.knowledge.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DocumentFileTypeTest {

    @Test
    void pdf_확장자면_PDF_타입을_반환한다() {
        assertThat(DocumentFileType.fromFilename("report.pdf")).isEqualTo(DocumentFileType.PDF);
    }

    @Test
    void 확장자_대소문자를_구분하지_않는다() {
        assertThat(DocumentFileType.fromFilename("REPORT.PDF")).isEqualTo(DocumentFileType.PDF);
    }

    @Test
    void xlsx_확장자면_XLSX_타입을_반환한다() {
        assertThat(DocumentFileType.fromFilename("sheet.xlsx")).isEqualTo(DocumentFileType.XLSX);
    }

    @Test
    void docx_확장자면_DOCX_타입을_반환한다() {
        assertThat(DocumentFileType.fromFilename("word.docx")).isEqualTo(DocumentFileType.DOCX);
    }

    @Test
    void md_확장자면_MD_타입을_반환한다() {
        assertThat(DocumentFileType.fromFilename("notes.md")).isEqualTo(DocumentFileType.MD);
    }

    @Test
    void 지원하지_않는_확장자면_예외가_발생한다() {
        assertThatThrownBy(() -> DocumentFileType.fromFilename("image.png"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 확장자가_없으면_예외가_발생한다() {
        assertThatThrownBy(() -> DocumentFileType.fromFilename("noextension"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 파일명이_null이거나_빈문자열이면_예외가_발생한다() {
        assertThatThrownBy(() -> DocumentFileType.fromFilename(null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> DocumentFileType.fromFilename(""))
                .isInstanceOf(IllegalArgumentException.class);
    }
}