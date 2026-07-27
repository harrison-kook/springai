package com.tororang.springai.member.application;

import com.tororang.springai.member.application.dto.MemberResult;
import com.tororang.springai.member.domain.Member;
import com.tororang.springai.member.domain.MemberRepository;
import com.tororang.springai.member.domain.exception.MemberNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindMemberUseCaseTest {

    @Mock
    private MemberRepository memberRepository;

    @Test
    void 존재하는_id로_조회하면_회원_정보가_반환된다() {
        Member member = Member.register("chulsoo@example.com", "철수");
        when(memberRepository.findById(member.id())).thenReturn(Optional.of(member));
        FindMemberUseCase useCase = new FindMemberUseCase(memberRepository);

        MemberResult result = useCase.find(member.id());

        assertThat(result.id()).isEqualTo(member.id());
        assertThat(result.email()).isEqualTo("chulsoo@example.com");
        assertThat(result.name()).isEqualTo("철수");
    }

    @Test
    void 존재하지_않는_id로_조회하면_예외가_발생한다() {
        UUID unknownId = UUID.randomUUID();
        when(memberRepository.findById(unknownId)).thenReturn(Optional.empty());
        FindMemberUseCase useCase = new FindMemberUseCase(memberRepository);

        assertThatThrownBy(() -> useCase.find(unknownId))
                .isInstanceOf(MemberNotFoundException.class);
    }
}
