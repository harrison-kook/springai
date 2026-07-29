package com.tororang.springai.knowledge.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DocumentTest {

    @Test
    void title이_빈_문자열이면_예외가_발생한다() {
        assertThatThrownBy(() -> Document.create("", "본문 내용"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void title이_null이면_예외가_발생한다() {
        assertThatThrownBy(() -> Document.create(null, "본문 내용"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void content가_빈_문자열이면_예외가_발생한다() {
        assertThatThrownBy(() -> Document.create("제목", ""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void content가_null이면_예외가_발생한다() {
        assertThatThrownBy(() -> Document.create("제목", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void title과_content가_유효하면_정상적으로_생성된다() {
        Document document = Document.create("제목", "본문 내용");

        assertThat(document.id()).isNotNull();
        assertThat(document.title()).isEqualTo("제목");
        assertThat(document.content()).isEqualTo("본문 내용");
    }
}
