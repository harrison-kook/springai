package com.tororang.springai.member.application;

import com.tororang.springai.member.application.dto.RegisterMemberCommand;
import com.tororang.springai.member.domain.Member;
import com.tororang.springai.member.domain.MemberRepository;
import com.tororang.springai.member.domain.exception.DuplicateEmailException;

import java.util.UUID;

public class RegisterMemberUseCase {

    private final MemberRepository memberRepository;

    public RegisterMemberUseCase(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public UUID register(RegisterMemberCommand command) {
        if (memberRepository.existsByEmail(command.email())) {
            throw new DuplicateEmailException("email already registered: " + command.email());
        }

        Member member = Member.register(command.email(), command.name());
        memberRepository.save(member);
        return member.id();
    }
}
