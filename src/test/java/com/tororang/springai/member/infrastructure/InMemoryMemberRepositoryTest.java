package com.tororang.springai.member.infrastructure;

import com.tororang.springai.member.domain.Member;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryMemberRepositoryTest {

    private final InMemoryMemberRepository repository = new InMemoryMemberRepository();

    @Test
    void save_후_findById로_동일한_회원을_조회할_수_있다() {
        Member member = Member.register("chulsoo@example.com", "철수");

        repository.save(member);

        assertThat(repository.findById(member.id())).contains(member);
    }

    @Test
    void 존재하지_않는_id로_조회하면_빈_Optional을_반환한다() {
        assertThat(repository.findById(UUID.randomUUID())).isEmpty();
    }

    @Test
    void save_후_findByEmail로_동일한_회원을_조회할_수_있다() {
        Member member = Member.register("chulsoo@example.com", "철수");

        repository.save(member);

        assertThat(repository.findByEmail("chulsoo@example.com")).contains(member);
    }

    @Test
    void 저장된_이메일은_존재하고_저장되지_않은_이메일은_존재하지_않는다() {
        Member member = Member.register("chulsoo@example.com", "철수");
        repository.save(member);

        assertThat(repository.existsByEmail("chulsoo@example.com")).isTrue();
        assertThat(repository.existsByEmail("unknown@example.com")).isFalse();
    }
}
