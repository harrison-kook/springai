package com.tororang.springai.member.application;

import com.tororang.springai.member.application.dto.MemberResult;
import com.tororang.springai.member.domain.Member;
import com.tororang.springai.member.domain.MemberRepository;
import com.tororang.springai.member.domain.exception.MemberNotFoundException;

import java.util.UUID;

public class FindMemberUseCase {

    private final MemberRepository memberRepository;

    public FindMemberUseCase(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public MemberResult find(UUID id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException("member not found: " + id));

        return new MemberResult(member.id(), member.email(), member.name());
    }
}
