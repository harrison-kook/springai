package com.tororang.springai.member.domain;

import java.util.UUID;

public class Member {

    private final UUID id;
    private final String email;
    private String name;

    private Member(UUID id, String email, String name) {
        if (email == null || email.isBlank() || !email.contains("@")) {
            throw new IllegalArgumentException("email must be a valid, non-empty address");
        }
        validateName(name);
        this.id = id;
        this.email = email;
        this.name = name;
    }

    public static Member register(String email, String name) {
        return new Member(UUID.randomUUID(), email, name);
    }

    public void changeName(String newName) {
        validateName(newName);
        this.name = newName;
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be empty");
        }
    }

    public UUID id() {
        return id;
    }

    public String email() {
        return email;
    }

    public String name() {
        return name;
    }
}
