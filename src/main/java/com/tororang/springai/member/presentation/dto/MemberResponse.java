package com.tororang.springai.member.presentation.dto;

import com.tororang.springai.member.application.dto.MemberResult;

import java.util.UUID;

public record MemberResponse(UUID memberId, String email, String name) {

    public static MemberResponse from(MemberResult result) {
        return new MemberResponse(result.id(), result.email(), result.name());
    }
}
