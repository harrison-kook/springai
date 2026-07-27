package com.tororang.springai.member.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberTest {

    @Test
    void email이_빈_문자열이면_예외가_발생한다() {
        assertThatThrownBy(() -> Member.register("", "철수"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void email이_null이면_예외가_발생한다() {
        assertThatThrownBy(() -> Member.register(null, "철수"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void email에_at_기호가_없으면_예외가_발생한다() {
        assertThatThrownBy(() -> Member.register("invalid-email", "철수"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void name이_빈_문자열이면_예외가_발생한다() {
        assertThatThrownBy(() -> Member.register("chulsoo@example.com", ""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void name이_null이면_예외가_발생한다() {
        assertThatThrownBy(() -> Member.register("chulsoo@example.com", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void email과_name이_유효하면_정상적으로_생성된다() {
        Member member = Member.register("chulsoo@example.com", "철수");

        assertThat(member.id()).isNotNull();
        assertThat(member.email()).isEqualTo("chulsoo@example.com");
        assertThat(member.name()).isEqualTo("철수");
    }

    @Test
    void changeName을_호출하면_이름이_갱신된다() {
        Member member = Member.register("chulsoo@example.com", "철수");

        member.changeName("영희");

        assertThat(member.name()).isEqualTo("영희");
    }

    @Test
    void changeName에_빈_문자열을_전달하면_예외가_발생한다() {
        Member member = Member.register("chulsoo@example.com", "철수");

        assertThatThrownBy(() -> member.changeName(""))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
