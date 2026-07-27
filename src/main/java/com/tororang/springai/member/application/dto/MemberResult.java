package com.tororang.springai.member.application.dto;

import java.util.UUID;

public record MemberResult(UUID id, String email, String name) {
}
