package com.tororang.springai.member.application;

import com.tororang.springai.member.application.dto.RegisterMemberCommand;
import com.tororang.springai.member.domain.Member;
import com.tororang.springai.member.domain.MemberRepository;
import com.tororang.springai.member.domain.exception.DuplicateEmailException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterMemberUseCaseTest {

    @Mock
    private MemberRepository memberRepository;

    @Test
    void 새_회원을_등록하면_저장소에_저장되고_id가_반환된다() {
        when(memberRepository.existsByEmail("chulsoo@example.com")).thenReturn(false);
        when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));
        RegisterMemberUseCase useCase = new RegisterMemberUseCase(memberRepository);

        UUID id = useCase.register(new RegisterMemberCommand("chulsoo@example.com", "철수"));

        ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository).save(captor.capture());
        assertThat(captor.getValue().id()).isEqualTo(id);
        assertThat(captor.getValue().email()).isEqualTo("chulsoo@example.com");
    }

    @Test
    void 이미_존재하는_이메일로_등록하면_예외가_발생하고_저장되지_않는다() {
        when(memberRepository.existsByEmail("chulsoo@example.com")).thenReturn(true);
        RegisterMemberUseCase useCase = new RegisterMemberUseCase(memberRepository);

        assertThatThrownBy(() -> useCase.register(new RegisterMemberCommand("chulsoo@example.com", "철수")))
                .isInstanceOf(DuplicateEmailException.class);

        verify(memberRepository, never()).save(any());
    }
}
